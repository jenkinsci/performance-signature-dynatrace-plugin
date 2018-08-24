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

package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Host;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.List;

public interface TopologySmartscapeHostApi {
    /**
     * Lists all available hosts in your environment
     * You can narrow down the output by specifying filtering parameters of the request.
     *
     * @param startTimestamp           Start timestamp of the requested timeframe, in milliseconds (UTC).   If not set, then 72 hours behind from now is used. (optional)
     * @param endTimestamp             Start timestamp of the requested timeframe, in milliseconds (UTC).   If not set, then now is used. (optional)
     * @param relativeTime             Relative timeframe, back from now. (optional)
     * @param tag                      Filters the resulting set of hosts by the specified tag.    A host has to match ALL specified tags. (optional)
     * @param showMonitoringCandidates Include/exclude monitoring canditate to the response.   Monitoring candidates are network entities, which are detected but not monitored. (optional)
     * @param entity                   Only return specified hosts. (optional)
     * @return Call&lt;List&lt;Host&gt;&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("entity/infrastructure/hosts")
    Call<List<Host>> getHosts(
            @retrofit2.http.Query("startTimestamp") Long startTimestamp, @retrofit2.http.Query("endTimestamp") Long endTimestamp, @retrofit2.http.Query("relativeTime") String relativeTime, @retrofit2.http.Query("tag") List<String> tag, @retrofit2.http.Query("showMonitoringCandidates") Boolean showMonitoringCandidates, @retrofit2.http.Query("entity") List<String> entity
    );

    /**
     * Gets parameters of the specified host
     *
     * @param meIdentifier Dynatrace entity ID of the host you&#39;re inquiring.   You can find it in the URL of the corresponding host page, for example, &#x60;HOST-007&#x60;. (required)
     * @return Call&lt;Host&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("entity/infrastructure/hosts/{meIdentifier}")
    Call<Host> getSingleHost(
            @retrofit2.http.Path("meIdentifier") String meIdentifier
    );
}
