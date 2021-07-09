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

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.Application;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
            @Query("startTimestamp") Long startTimestamp, @Query("endTimestamp") Long endTimestamp, @Query("relativeTime") String relativeTime, @Query("tag") List<String> tag, @Query("entity") List<String> entity
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
            @Path("meIdentifier") String meIdentifier
    );
}
