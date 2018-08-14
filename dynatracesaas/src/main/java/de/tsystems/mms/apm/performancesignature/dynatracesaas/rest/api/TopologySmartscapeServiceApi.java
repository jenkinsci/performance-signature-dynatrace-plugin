package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Service;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.List;

public interface TopologySmartscapeServiceApi {
    /**
     * Lists all available services in your environment
     * You can narrow down the output by specifying filtering parameters of the request.
     *
     * @param startTimestamp Start timestamp of the requested timeframe, in milliseconds (UTC).   72 hours from now is used if the value is not set. (optional)
     * @param endTimestamp   End timestamp of the requested timeframe, in milliseconds (UTC).   The current timestamp is used if the value is not set. (optional)
     * @param relativeTime   Relative timeframe, back from now. (optional)
     * @param tag            Filters the response by the specified tag.    A service has to match ALL specified tags. (optional)
     * @param entity         Filters the response to the specified services only. (optional)
     * @return Call&lt;List&lt;Service&gt;&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("entity/services")
    Call<List<Service>> getServices(
            @retrofit2.http.Query("startTimestamp") Long startTimestamp, @retrofit2.http.Query("endTimestamp") Long endTimestamp, @retrofit2.http.Query("relativeTime") String relativeTime, @retrofit2.http.Query("tag") List<String> tag, @retrofit2.http.Query("entity") List<String> entity
    );

    /**
     * Gets parameters of the specified service
     *
     * @param meIdentifier Dynatrace entity ID of the service you&#39;re inquiring.   You can find it in the URL of the corresponding host page, for example, &#x60;SERVICE-007&#x60;. (required)
     * @return Call&lt;Service&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("entity/services/{meIdentifier}")
    Call<Service> getSingleService(
            @retrofit2.http.Path("meIdentifier") String meIdentifier
    );
}
