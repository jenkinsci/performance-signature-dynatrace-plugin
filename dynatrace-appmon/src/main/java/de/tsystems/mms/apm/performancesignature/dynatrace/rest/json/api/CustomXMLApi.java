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

import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.AgentList;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.DashboardList;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.LicenseInformation;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.XmlResult;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface CustomXMLApi {
    @Headers({
            "Accept: text/xml"
    })
    @GET("rest/management/dashboards")
    Call<DashboardList> listDashboards();

    @Headers({
            "Accept: text/xml"
    })
    @GET("rest/management/dashboard/{dashboardId}")
    Call<DashboardReport> getXMLDashboard(
            @Path("dashboardId") String dashboard, @Query("source") String source
    );

    /*@Headers({
            "Accept: text/xml"
    })*/
    //header leads to http error 406 Not Acceptable
    @GET("rest/management/server/license")
    Call<LicenseInformation> getServerLicense();

    @Headers({
            "Accept: text/xml"
    })
    @GET("rest/management/agents")
    Call<AgentList> getAllAgents();

    @Headers({
            "Accept: text/xml"
    })
    @GET("rest/management/agents/{agentId}/hotsensorplacement")
    Call<XmlResult> hotSensorPlacement(
            @Path("agentId") int agentId
    );

    @Headers({
            "Accept: text/xml"
    })
    @FormUrlEncoded
    @POST("rest/management/profiles/{systemProfile}/memorydump")
    Call<XmlResult> createMemoryDump(@Path("systemProfile") String systemProfile, @Field("agentName") String agentName, @Field("hostName") String hostName,
                                     @Field("processId") int processId, @Field("type") String dumpType, @Field("isSessionLocked") boolean sessionLocked,
                                     @Field("capturestrings") boolean captureStrings, @Field("captureprimitives") boolean capturePrimitives,
                                     @Field("autopostprocess") boolean autoPostProcess, @Field("dogc") boolean doGC
    );

    @Headers({
            "Accept: text/xml"
    })
    @GET("rest/management/profiles/{systemProfile}/memorydumpcreated/{memorydumpId}")
    Call<XmlResult> getMemoryDumpStatus(
            @Path("systemProfile") String profileName, @Path(value = "memorydumpId", encoded = true) String memoryDumpName
    );

    @Headers({
            "Accept: text/xml"
    })
    @FormUrlEncoded
    @POST("rest/management/profiles/{systemProfile}/threaddump")
    Call<XmlResult> createThreadDump(
            @Path("systemProfile") String systemProfile, @Field("agentName") String agentName, @Field("hostName") String hostName, @Field("processId") int processId,
            @Field("isSessionLocked") boolean sessionLocked
    );

    @Headers({
            "Accept: text/xml"
    })
    @GET("rest/management/profiles/{systemProfile}/threaddumpcreated/{threaddumpId}")
    Call<XmlResult> getThreadDumpStatus(
            @Path("systemProfile") String profileName, @Path(value = "threaddumpId", encoded = true) String threadDumpName
    );

    @Headers({
            "Accept: application/octet-stream"
    })
    @GET("rest/management/reports/create/{dashboardId}")
    Call<ResponseBody> getPDFReport(
            @Path("dashboardId") String dashboard, @Query("source") String sessionId, @Query("compare") String comparedSessionId, @Query("type") String type
    );
}
