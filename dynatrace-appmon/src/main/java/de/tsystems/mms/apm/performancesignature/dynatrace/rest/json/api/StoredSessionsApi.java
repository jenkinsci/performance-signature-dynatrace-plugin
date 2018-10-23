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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api;

import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SessionMetadata;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.Sessions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import static de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiClient.API_SUFFIX;

public interface StoredSessionsApi {
    /**
     * Delete individual stored session
     * Delete a stored session.
     *
     * @param sessionid Unique session id (required)
     * @return Call&lt;Void&gt;
     */
    @DELETE(API_SUFFIX + "sessions/{sessionid}")
    Call<Void> deleteStoredSession(
            @Path("sessionid") String sessionid
    );

    /**
     * Export stored session
     * Export a stored session. For large stored sessions the size of the downloaded file can be huge. Make sure the download machine has enough free space to ensure that the download is successful.
     *
     * @param sessionid                 Unique session id (required)
     * @param removeconfidentialstrings true to remove confidential strings from the exported session, false to keep them included (optional, default to true)
     * @param timeframestart            Timeframe filter start time timestamp (ISO8601) (optional)
     * @param timeframeend              Timeframe filter end time timestamp (ISO8601) (optional)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Accept: application/octet-stream"
    })
    @GET(API_SUFFIX + "sessions/{sessionid}")
    @Streaming
    Call<ResponseBody> getStoredSession(
            @Path("sessionid") String sessionid, @Query("removeconfidentialstrings") Boolean removeconfidentialstrings, @Query("timeframestart") String timeframestart, @Query("timeframeend") String timeframeend
    );

    /**
     * Get session metadata
     * Get comprehensive details of a stored session. For more information look at the response entity.
     *
     * @param sessionid Unique session id (required)
     * @return Call&lt;SessionMetadata&gt;
     */
    @GET(API_SUFFIX + "sessions/{sessionid}/metadata")
    Call<SessionMetadata> getStoredSessionMetaData(
            @Path("sessionid") String sessionid
    );

    /**
     * List stored sessions
     * Get a list of all stored sessions which are available to the current user.
     *
     * @return Call&lt;Sessions&gt;
     */
    @GET(API_SUFFIX + "sessions")
    Call<Sessions> listStoredSessions();


}
