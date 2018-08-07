/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json;

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.auth.HttpBasicAuth;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jaxb.JaxbConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    public static final String API_SUFFIX = "api/v2/";
    public static final String REST_DF = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private final OkHttpClient.Builder okBuilder;
    private boolean debugging = false;
    private boolean verifyingSsl;
    private Retrofit.Builder adapterBuilder;
    private HttpLoggingInterceptor loggingInterceptor;

    /*
     * Constructor for ApiClient
     */
    public ApiClient() {
        verifyingSsl = true;
        okBuilder = new OkHttpClient.Builder();
        String baseUrl = "https://localhost" + API_SUFFIX;

        Gson gson = new GsonBuilder()
                .setDateFormat(ApiClient.REST_DF)
                .create();

        adapterBuilder = new Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JaxbConverterFactory.create())
                .addConverterFactory(GsonCustomConverterFactory.create(gson));
    }

    /**
     * Set base path
     *
     * @param basePath Base path of the URL (e.g https://localhost/api/v2
     * @return An instance of OkHttpClient
     */
    public ApiClient setBasePath(String basePath) {
        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        adapterBuilder.baseUrl(basePath);
        return this;
    }

    /**
     * Configure whether to verify certificate and hostname when making https requests.
     * Default to true.
     * NOTE: Do NOT set to false in production code, otherwise you would face multiple types of cryptographic attacks.
     *
     * @param verifyingSsl True to verify TLS/SSL connection
     * @return ApiClient
     */
    public ApiClient setVerifyingSsl(boolean verifyingSsl) {
        this.verifyingSsl = verifyingSsl;
        applySslSettings();
        return this;
    }

    /**
     * Apply SSL related settings to httpClient according to the current values of
     * verifyingSsl and sslCaCert.
     */
    private void applySslSettings() {
        try {
            if (!verifyingSsl) {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                okBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
                okBuilder.hostnameVerifier((hostname, session) -> true);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiClient setProxy(Proxy proxy) {
        okBuilder.proxy(proxy);
        return this;
    }

    /**
     * Enable/disable debugging for this API client.
     *
     * @param debugging To enable (true) or disable (false) debugging
     * @return ApiClient
     */
    public ApiClient setDebugging(boolean debugging) {
        if (debugging != this.debugging) {
            if (debugging) {
                loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                okBuilder.addInterceptor(loggingInterceptor);
            } else {
                okBuilder.interceptors().remove(loggingInterceptor);
                loggingInterceptor = null;
            }
        }
        this.debugging = debugging;
        return this;
    }

    public <S> S createService(Class<S> serviceClass) {
        return adapterBuilder
                .client(okBuilder.build())
                .build()
                .create(serviceClass);
    }

    /**
     * Helper method to set credentials for the HTTP basic authentication.
     *
     * @param creds UsernamePasswordCredentials
     */
    public ApiClient setCredentials(final UsernamePasswordCredentials creds) {
        HttpBasicAuth basicAuth = new HttpBasicAuth();
        basicAuth.setCredentials(creds.getUsername(), creds.getPassword().getPlainText());
        okBuilder.addInterceptor(basicAuth);
        return this;
    }

    public Retrofit.Builder getAdapterBuilder() {
        return adapterBuilder;
    }

    public ApiClient setAdapterBuilder(Retrofit.Builder adapterBuilder) {
        this.adapterBuilder = adapterBuilder;
        return this;
    }

    public OkHttpClient.Builder getOkBuilder() {
        return okBuilder;
    }

    /**
     * Execute HTTP call and deserialize the HTTP response body into the given return type.
     *
     * @param <T>  The return type corresponding to (same with) returnType
     * @param call Call
     * @return ApiResponse object containing response status, headers and
     * data, which is a Java object deserialized from response body and would be null
     * when returnType is null.
     * @throws ApiException If fail to execute the call
     */
    public <T> ApiResponse<T> execute(final Call<T> call) throws ApiException {
        try {
            Response<T> response = call.execute();
            T data = handleResponse(response);
            return new ApiResponse<>(response.code(), response.headers().toMultimap(), data);
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    /**
     * Handle the given response, return the deserialized object when the response is successful.
     *
     * @param <T>      Type
     * @param response Response
     * @return Type
     * @throws ApiException If the response has a unsuccessful status code or
     *                      fail to deserialize the response body
     */
    private <T> T handleResponse(Response<T> response) throws ApiException {
        if (response.isSuccessful()) {
            return response.body();
        } else {
            String respBody = null;
            if (response.errorBody() != null) {
                try {
                    respBody = response.errorBody().string();
                } catch (IOException e) {
                    throw new ApiException(response.message(), e, response.code(), response.headers().toMultimap());
                }
            }
            throw new ApiException(response.message(), response.code(), response.headers().toMultimap(), respBody);
        }
    }

    public ApiClient setReadTimeout(final int timeout) {
        okBuilder.readTimeout(timeout, TimeUnit.SECONDS);
        return this;
    }
}

/**
 * This wrapper is to take care of this case:
 * when the deserialization fails due to JsonParseException and the
 * expected type is String, then just return the body string.
 */
class GsonResponseBodyConverterToString<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    GsonResponseBodyConverterToString(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(@Nonnull ResponseBody value) throws IOException {
        String returned = value.string();
        try {
            return gson.fromJson(returned, type);
        } catch (JsonParseException e) {
            return (T) returned;
        }
    }
}

class GsonCustomConverterFactory extends Converter.Factory {
    private final Gson gson;
    private final GsonConverterFactory gsonConverterFactory;

    private GsonCustomConverterFactory(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }
        this.gson = gson;
        this.gsonConverterFactory = GsonConverterFactory.create(gson);
    }

    public static GsonCustomConverterFactory create(Gson gson) {
        return new GsonCustomConverterFactory(gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type.equals(String.class)) {
            return new GsonResponseBodyConverterToString<>(gson, type);
        }
        return gsonConverterFactory.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return gsonConverterFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }
}
