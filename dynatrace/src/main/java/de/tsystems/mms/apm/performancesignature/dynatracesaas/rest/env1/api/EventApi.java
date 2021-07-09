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

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.EventPushMessage;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.EventQueryResult;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.EventRestEntry;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.EventStoreResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventApi {
    /**
     * Delivers a single Event by its ID
     *
     * @param eventId Unique environment identifier for a specific event (required)
     * @return Call&lt;EventRestEntry&gt;
     */
    @GET("events/{eventId}")
    Call<EventRestEntry> getEventById(
            @Path("eventId") String eventId
    );

    /**
     * Pushes custom events to one or more monitored entities.
     * The events REST endpoint enables 3rd party integrations to push custom events to one or more monitored entities via the API. The intent of this interface is to allow 3rd party systems, such as CI platforms (Jenkins, Bamboo, Electric Cloud, etc.) to provide additional detail for Dynatrace automated root cause analysis. The events API offers a set of semantically predefined event types that allow the Dynatrace problem correlation engine to correctly handle information provided by external systems. The predefined semantics of these event types allows for more precise root cause detection.
     *
     * @param body (optional)
     * @return Call&lt;EventStoreResult&gt;
     */
    @Headers({
            "Content-Type:application/json"
    })
    @POST("events")
    Call<EventStoreResult> postNaturalEvent(
            @Body EventPushMessage body
    );

    /**
     * Delivers a collection of events that fit the given filters
     * As the number of uncorrelated events in an environment can be huge, this REST endpoint is limited to a maximum of 150 single events per request. This means that API consumers must use filters to further focus their queries, either on specific monitored entities or to requests that occurred during a specific time frame.
     *
     * @param from         Start of timeframe in milliseconds since Unix epoch. (optional)
     * @param to           End of timeframe in milliseconds since Unix epoch. Default timeframe is the last 30 days (optional)
     * @param relativeTime Relative timeframe, back from the current time. (optional)
     * @param eventType    Filter the event feed based on a specific event type (optional)
     * @param entityId     Only receive events for a given monitored entity, such as a host, process, or service (optional)
     * @param cursor       If a query returns a cursor string this string can be used to fetch the next 150 events of a query.Note that there is no need to specify additional parameters (for instance eventType, from or to) as the cursor string already contains all these parameters (optional)
     * @return Call&lt;EventQueryResult&gt;
     */
    @GET("events")
    Call<EventQueryResult> queryEvents(
            @Query("from") Long from, @Query("to") Long to, @Query("relativeTime") String relativeTime, @Query("eventType") String eventType, @Query("entityId") String entityId, @Query("cursor") String cursor
    );

}
