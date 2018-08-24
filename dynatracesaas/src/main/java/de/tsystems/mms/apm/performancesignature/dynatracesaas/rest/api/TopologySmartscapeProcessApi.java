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

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.ProcessGroupInstance;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.List;

public interface TopologySmartscapeProcessApi {
    /**
     * List all monitored processes along with their parameters
     * You can narrow down the output by specifying filtering parameters of the request.
     *
     * @param startTimestamp          Start timestamp of the requested timeframe, in milliseconds (UTC).   72 hours from now is used if the value is not set. (optional)
     * @param endTimestamp            End timestamp of the requested timeframe, in milliseconds (UTC).   The current timestamp is used if the value is not set. (optional)
     * @param relativeTime            Relative timeframe, back from now. (optional)
     * @param tag                     Filters the resulting set of processes by the specified tag.    A process has to match ALL specified tags. (optional)
     * @param entity                  Only return specified processes. (optional)
     * @param hostTag                 Filters processes by the host they&#39;re running at.   Specify tags of the host you&#39;re interested in. (optional)
     * @param host                    Filters processes by the host they&#39;re running at.   Specify Dynatrace IDs of the host you&#39;re interested in. (optional)
     * @param actualMonitoringState   Filters processes by the actual monitoring state of the process. (optional)
     * @param expectedMonitoringState Filters processes by the expected monitoring state of the process. (optional)
     * @return Call&lt;List&lt;ProcessGroupInstance&gt;&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("entity/infrastructure/processes")
    Call<List<ProcessGroupInstance>> getProcesses(
            @retrofit2.http.Query("startTimestamp") Long startTimestamp, @retrofit2.http.Query("endTimestamp") Long endTimestamp, @retrofit2.http.Query("relativeTime") String relativeTime, @retrofit2.http.Query("tag") List<String> tag, @retrofit2.http.Query("entity") List<String> entity, @retrofit2.http.Query("hostTag") List<String> hostTag, @retrofit2.http.Query("host") List<String> host, @retrofit2.http.Query("actualMonitoringState") String actualMonitoringState, @retrofit2.http.Query("expectedMonitoringState") String expectedMonitoringState
    );

    /**
     * List properties of the specified process
     *
     * @param meIdentifier Dynatrace entity ID of the process you&#39;re inquiring.   You can find it in the URL of the corresponding process page, for example, &#x60;PROCESS_INSTANCE-007&#x60;. (required)
     * @return Call&lt;ProcessGroupInstance&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("entity/infrastructure/processes/{meIdentifier}")
    Call<ProcessGroupInstance> getSingleProcess(
            @retrofit2.http.Path("meIdentifier") String meIdentifier
    );
}
