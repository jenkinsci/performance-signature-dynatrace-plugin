package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Application;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.List;

public interface TopologySmartscapeApplicationApi {
    /**
     * Gets the list of all applications in your environment along with their parameters
     * You can optionally specify timeframe, to filter the output only to applications, active in specified time.
     *
     * @param startTimestamp Start timestamp of the requested timeframe, in milliseconds (UTC).   If no timeframe specified the 72 hours behind from now is used. (optional)
     * @param endTimestamp   End timestamp of the requested timeframe, in milliseconds (UTC).   If no timeframe specified then now is used. (optional)
     * @param relativeTime   Relative timeframe, back from now.  (optional)
     * @param tag            Filters the resulting set of applications by the specified tag.    An application has to match ALL specified tags. (optional)
     * @param entity         Only return specified applications. (optional)
     * @return Call&lt;List&lt;Application&gt;&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("entity/applications")
    Call<List<Application>> getApplications(
            @retrofit2.http.Query("startTimestamp") Long startTimestamp, @retrofit2.http.Query("endTimestamp") Long endTimestamp, @retrofit2.http.Query("relativeTime") String relativeTime, @retrofit2.http.Query("tag") List<String> tag, @retrofit2.http.Query("entity") List<String> entity
    );

    /**
     * Gets parameters of the specified application
     *
     * @param meIdentifier Dynatrace entity ID of the application you&#39;re inquiring.   You can find them in the URL of the corresponding application page, for example, &#x60;APPLICATION-007&#x60;. (required)
     * @return Call&lt;Application&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("entity/applications/{meIdentifier}")
    Call<Application> getSingleApplication(
            @retrofit2.http.Path("meIdentifier") String meIdentifier
    );
}
