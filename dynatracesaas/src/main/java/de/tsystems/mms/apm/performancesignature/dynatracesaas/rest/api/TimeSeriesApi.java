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

package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api;

import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.ApiClient;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.ApiException;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.ApiResponse;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.Pair;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Result;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Timeseries;
import okhttp3.Call;

import java.lang.reflect.Type;
import java.util.*;

import static de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Timeseries.AggregationEnum;

public class TimeSeriesApi {
    private ApiClient apiClient;

    public TimeSeriesApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Call getTimeseriesCall(String timeseriesId, Long startTimestamp, Long endTimestamp,
                                  AggregationEnum aggregationType, String queryMode) throws ApiException {
        // create path and map variables
        String localVarPath = ApiClient.API_SUFFIX + "/timeseries";

        List<Pair> localVarQueryParams = new ArrayList<>();
        if (timeseriesId != null) {
            localVarQueryParams.addAll(apiClient.parameterToPair("timeseriesId", timeseriesId));
        }
        if (startTimestamp != null) {
            localVarQueryParams.addAll(apiClient.parameterToPair("startTimestamp", startTimestamp));
        }
        if (endTimestamp != null) {
            localVarQueryParams.addAll(apiClient.parameterToPair("endTimestamp", endTimestamp));
        }
        if (aggregationType != null) {
            localVarQueryParams.addAll(apiClient.parameterToPair("aggregationType", aggregationType));
        }
        if (queryMode != null) {
            localVarQueryParams.addAll(apiClient.parameterToPair("queryMode", queryMode));
        }

        Map<String, String> localVarHeaderParams = new HashMap<>();
        localVarHeaderParams.put("Accept", "application/json");

        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams, null, localVarHeaderParams, null);
    }

    public Result getTimeseriesData(String timeseriesId, Date startTimestamp, Date endTimestamp,
                                    AggregationEnum aggregationType, String queryMode) throws ApiException {
        ApiResponse<Result.Container> resp = getTimeseriesDataWithHttpInfo(timeseriesId, startTimestamp, endTimestamp, aggregationType, queryMode);
        return resp.getData().result;
    }

    public ApiResponse<Result.Container> getTimeseriesDataWithHttpInfo(String timeseriesId, Date startTimestamp,
                                                                       Date endTimestamp, AggregationEnum aggregationType, String queryMode) throws ApiException {
        Call call = getTimeseriesCall(timeseriesId, startTimestamp.getTime(), endTimestamp.getTime(), aggregationType, queryMode);
        Type localVarReturnType = new TypeToken<Result.Container>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    public List<Timeseries> getTimeseries() throws ApiException {
        ApiResponse<List<Timeseries>> resp = getTimeseriesWithHttpInfo();
        return resp.getData();
    }

    public ApiResponse<List<Timeseries>> getTimeseriesWithHttpInfo() throws ApiException {
        Call call = getTimeseriesCall(null, null, null, null, null);
        Type localVarReturnType = new TypeToken<List<Timeseries>>() {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }
}
