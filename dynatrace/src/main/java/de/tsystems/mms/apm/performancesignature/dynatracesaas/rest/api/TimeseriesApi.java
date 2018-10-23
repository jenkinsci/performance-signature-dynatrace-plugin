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

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.TimeseriesDataPointQueryResult;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.TimeseriesDefinition;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.TimeseriesQueryMessage;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.TimeseriesQueryResult;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface TimeseriesApi {
    /**
     * Lists all metric definitions, along with parameters of each metric.
     * You can specify filtering paramters to return only matched metrics. If no parameters are specified, the call will list all the defined and exposed metrics.
     *
     * @param source         Metric type. Allowed values are &#x60;BUILTIN&#x60;, &#x60;PLUGIN&#x60;, and &#x60;CUSTOM&#x60;. (optional)
     * @param detailedSource The feature, where metrics originate, such as Synthetic or RUM. (optional)
     * @return Call&lt;List&lt;TimeseriesDefinition&gt;&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("timeseries")
    Call<List<TimeseriesDefinition>> getAllTimeseriesDefinitions(
            @Query("source") String source, @Query("detailedSource") String detailedSource
    );

    /**
     * Lists all available metric data points, matching specified parameters.
     * Provides advanced filtering possibilities, comparing to the &#x60;GET /timeseries/{metricIdentifier}&#x60; request.
     *
     * @param body JSON body of the request, containing parameters to identify the required data points.
     * @return Call&lt;TimeseriesQueryResultWrapper.Container&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @POST("timeseries")
    Call<TimeseriesDataPointQueryResult.Container> readTimeseriesComplex(
            @Body TimeseriesQueryMessage body
    );

    /**
     * Gets the parameters of the specified metric and optionally data points.
     * To obtain data points, set **includeData** to &#x60;true&#x60;.   You can obtain either data points or the scalar result of the specified timeseries, depending on the **queryMode**.   To obtain data points you must specify the timeframe, either as **relativeTime** or as combination of **startTimestamp** and **endTimestamp**. You must also provide **aggregationType**, supported by the metric.
     *
     * @param timeseriesIdentifier Case-sensitive identifier of the timeseries, where you want to read parameters and data points. (required)
     * @param includeData          Flag to include data points to the response. \\n\\n To obtain data points you must specify the timeframe and aggregation type. (optional)
     * @param aggregationType      The aggregation type for the resulting data points. \\n\\n\&quot; +      \&quot;If the requested metric doesn&#39;t support the specified aggregation, the request will result in an error. (optional)
     * @param startTimestamp       Start timestamp of the requested timeframe, in milliseconds (UTC). The start time must be earlier than the end time. (optional)
     * @param endTimestamp         End timestamp of the requested timeframe, in milliseconds (UTC). End time must be later than the start time. \\n\\n \&quot; +      \&quot;If later than the current time, Dynatrace automatically uses current time instead. (optional)
     * @param predict              Used to predict future data points. (optional)
     * @param relativeTime         Relative timeframe, back from the current time. (optional)
     * @param queryMode            The type of result that the call should return. Valid result modes are: \\n\&quot; +         \&quot;&#x60;series&#x60;: returns all the data points of the timeseries in specified timeframe. \\n &#x60;total&#x60;: returns one scalar value for the specified timeframe. \\n\\n\&quot;+         \&quot;By default, the &#x60;series&#x60; mode is used. (optional)
     * @param entity               Filters requested data points by entity which should deliver them. \\n\\n\&quot; +         \&quot;Allowed values are Dynatrace entity IDs. You can find them in the URL of the corresponding Dynatrace entity page, for example, &#x60;HOST-007&#x60;. \\n\\n\&quot; +         \&quot;If the selected entity doesn&#39;t support the requested timeseries, the request will result in an error. (optional)
     * @param tag                  Filters the resulting set of applications by the specified tag. \\n\\n\&quot; +         \&quot;Use multiple tag parameters to combine multiple tag filters using the logical operator AND. \\n\\n\&quot; +         \&quot;In case of key-value tags, such as imported AWS or CloudFoundry tags use following format: &#x60;[context]key:value&#x60;. (optional)
     * @param percentile           In case of the percentile aggregation type, this parameter specifies which percentile of the selected response time metric should be delivered. \&quot; +         \&quot;Valid values for percentile are between 1 and 99. \\n\\n\&quot; +         \&quot;Please keep in mind that percentile export is only possible for response-time based metrics such as application and service response times. (optional)
     * @return Call&lt;List&lt;TimeseriesQueryResult&gt;&gt;
     */
    @Headers({
            "Content-Type:application/json; charset=utf-8"
    })
    @GET("timeseries/{timeseriesIdentifier}")
    Call<TimeseriesQueryResult> readTimeseriesData(
            @Path("timeseriesIdentifier") String timeseriesIdentifier,
            @Query("includeData") Boolean includeData,
            @Query("aggregationType") String aggregationType,
            @Query("startTimestamp") Long startTimestamp,
            @Query("endTimestamp") Long endTimestamp,
            @Query("predict") Boolean predict,
            @Query("relativeTime") String relativeTime,
            @Query("queryMode") String queryMode,
            @Query("entity") List<String> entity,
            @Query("tag") List<String> tag,
            @Query("percentile") Integer percentile
    );
}
