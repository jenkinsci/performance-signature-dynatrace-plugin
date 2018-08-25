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

import de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.Alerts;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.DeploymentEvent;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.EventUpdate;
import retrofit2.Call;
import retrofit2.http.*;

import static de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiClient.API_SUFFIX;

public interface AlertsIncidentsAndEventsApi {
    /**
     * Create deployment event
     * Create an deployment event for a System Profile. The request must contain the event as JSON representation. If the request does not contain a start and end date, the current server time will be used. The default severity is &#39;informational&#39; and the default state is &#39;Created&#39;.  Events with a severity of &#39;informational&#39; are automatically set to state &#39;Confirmed&#39;. You can set such events to other states with a subsequent update.  It is possible to specify the start date and leave the end date unset, the end date can then be provided later with an update.  At least the JSON properties &#39;systemprofile&#39; and &#39;message&#39; have to be specified.
     *
     * @param body Event record (required)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Content-Type:application/json"
    })
    @POST(API_SUFFIX + "events/Deployment")
    Call<Void> createDeploymentEvent(
            @Body DeploymentEvent body
    );

    /**
     * Get deployment event record
     * Get the JSON representation of a deployment event.
     *
     * @param eventid ID of event (required)
     * @return Call&lt;DeploymentEvent&gt;
     */
    @GET(API_SUFFIX + "events/Deployment/{eventid}")
    Call<DeploymentEvent> getDeploymentEvent(
            @Path("eventid") String eventid
    );

    /**
     * Get Alert record
     * Get the JSON representation of an alert (incident).
     *
     * @param alertid ID of alert (required)
     * @return Call&lt;AlertResponse&gt;
     */
    @GET(API_SUFFIX + "alerts/{alertid}")
    Call<Alert> getIncident(
            @Path("alertid") String alertid
    );

    /**
     * List Alerts
     * Get a list of all alerts (incidents) that match the filter settings. If no start and end date is specified, a default time frame of three days is selected.
     *
     * @param systemprofile System Profile id (optional)
     * @param incidentrule  Incident Rule name (optional)
     * @param state         Alert state (optional)
     * @param from          Minimum start date of the alert (ISO8601) (optional)
     * @param to            Maximum end date of the alert (ISO8601) (optional)
     * @return Call&lt;Alerts&gt;
     */
    @GET(API_SUFFIX + "alerts")
    Call<Alerts> getIncidents(
            @Query("systemprofile") String systemprofile, @Query("incidentrule") String incidentrule, @Query("state") String state, @Query("from") String from, @Query("to") String to
    );

    /**
     * Update deployment event record
     * Several attributes of a deployment event can be modified by updating it. You can either retrieve the event record via the GET call first and then send the modified JSON object, or you could make a partial update by providing only the properties that should get updated.
     *
     * @param eventid ID of event (required)
     * @param body    Event record (required)
     * @return Call&lt;Void&gt;
     */
    @Headers({
            "Content-Type:application/json"
    })
    @PUT(API_SUFFIX + "events/Deployment/{eventid}")
    Call<Void> updateDeploymentEvent(
            @Path("eventid") String eventid, @Body EventUpdate body
    );
}
