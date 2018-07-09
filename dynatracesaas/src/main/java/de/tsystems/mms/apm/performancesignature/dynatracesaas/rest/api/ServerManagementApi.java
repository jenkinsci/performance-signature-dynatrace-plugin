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

package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api;

import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.ApiClient;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.ApiException;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.ApiResponse;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.Pair;
import okhttp3.Call;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerManagementApi {
    private ApiClient apiClient;

    public ServerManagementApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Build call for getVersion
     *
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     */
    public Call getVersionCall() throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/config/clusterversion";

        List<Pair> localVarQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();
        localVarHeaderParams.put("Accept", "application/json");

        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, null, localVarHeaderParams, null);
    }

    /**
     * Request the version of the AppMon Server
     * Get the version of the running server.
     *
     * @return Result
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public String getVersion() throws ApiException {
        ApiResponse<String> resp = getVersionWithHttpInfo();
        return resp.getData();
    }

    /**
     * Request the version of the AppMon Server
     * Get the version of the running server.
     *
     * @return ApiResponse&lt;Result&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     */
    public ApiResponse<String> getVersionWithHttpInfo() throws ApiException {
        Call call = getVersionCall();
        Type localVarReturnType = new TypeToken<String>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }
}
