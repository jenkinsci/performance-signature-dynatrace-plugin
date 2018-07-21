package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api;

import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.RecordingStatus;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SessionRecordingOptions;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SessionStoringOptions;
import retrofit2.Call;
import retrofit2.http.*;

import static de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiClient.API_SUFFIX;
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

public interface LiveSessionsApi {
    /**
     * Get session recording status
     * Check if the live session is currently being recorded.
     *
     * @param profileid System Profile id (required)
     * @return Call&lt;RecordingStatus&gt;
     */
    @GET(API_SUFFIX + "profiles/{profileid}/session/recording/status")
    Call<RecordingStatus> getRecording(
            @Path("profileid") String profileid
    );

    /**
     * Start session recording
     * Start session recording for a specific System Profile. Starting session recording is only possible for pre-production licenses.
     *
     * @param profileid System Profile id (required)
     * @param body      Session recording options (optional)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Content-Type:application/json"
    })
    @POST(API_SUFFIX + "profiles/{profileid}/session/recording")
    Call<Void> postRecording(
            @Path("profileid") String profileid, @Body SessionRecordingOptions body
    );

    /**
     * Stop session recording
     * Set recording status to false in order to stop session recording and create a reference to the stored session. This call does not complete until all recorded data is fully processed on the Server. Depending on the environment, it can take a few minutes until an HTTP response message is received. Stopping session recording is only possible for pre-production licenses.
     *
     * @param profileid System Profile id (required)
     * @param body      (optional)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Content-Type:application/json"
    })
    @PUT(API_SUFFIX + "profiles/{profileid}/session/recording/status")
    Call<Void> stopRecording(
            @Path("profileid") String profileid, @Body RecordingStatus body
    );

    /**
     * Store session
     * Store all time series and PurePaths in the Server&#39;s memory to a stored session. To limit the data to be stored, specify a start time and end time in the request body, otherwise the last 30 minutes will be stored.
     *
     * @param profileid System Profile id (required)
     * @param body      Session storing options (optional)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Content-Type:application/json"
    })
    @POST(API_SUFFIX + "profiles/{profileid}/session/store")
    Call<Void> storeSession(
            @Path("profileid") String profileid, @Body SessionStoringOptions body
    );
}
