/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
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

package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.api;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.Service;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
            @Query("startTimestamp") Long startTimestamp, @Query("endTimestamp") Long endTimestamp, @Query("relativeTime") String relativeTime, @Query("tag") List<String> tag, @Query("entity") List<String> entity
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
            @Path("meIdentifier") String meIdentifier
    );
}
