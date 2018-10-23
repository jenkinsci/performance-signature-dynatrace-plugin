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

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.ProcessGroup;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface TopologySmartscapeProcessGroupApi {
    /**
     * Lists all process groups of your environment, along with their parameters
     * You can narrow down the output by specifying filtering paramters of the request.
     *
     * @param startTimestamp Start timestamp of the requested timeframe, in milliseconds (UTC).   If not set, then 72 hours behind from now is used. (optional)
     * @param endTimestamp   End timestamp of the requested timeframe, in milliseconds (UTC).   If not set, then now is used. (optional)
     * @param relativeTime   Relative timeframe, back from now. (optional)
     * @param tag            Filters the resulting set of processes by the specified tags.    A process group has to match ALL specified tags. (optional)
     * @param entity         Only return specified process groups. (optional)
     * @return Call&lt;List&lt;ProcessGroup&gt;&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("entity/infrastructure/process-groups")
    Call<List<ProcessGroup>> getProcessGroups(
            @Query("startTimestamp") Long startTimestamp, @Query("endTimestamp") Long endTimestamp, @Query("relativeTime") String relativeTime, @Query("tag") List<String> tag, @Query("entity") List<String> entity
    );

    /**
     * List properties of the specified process group
     *
     * @param meIdentifier Dynatrace entity ID of the process group you&#39;re inquiring.   You can find it in the URL of the corresponding process group page, for example, &#x60;PROCESS_GROUP_INSTANCE-007&#x60;. (required)
     * @return Call&lt;ProcessGroup&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("entity/infrastructure/process-groups/{meIdentifier}")
    Call<ProcessGroup> getSingleProcessGroup(
            @Path("meIdentifier") String meIdentifier
    );
}
