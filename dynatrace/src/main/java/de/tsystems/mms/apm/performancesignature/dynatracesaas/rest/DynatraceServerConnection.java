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

package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api.*;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.*;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import retrofit2.Call;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DynatraceServerConnection {
    public static final String BUILD_URL_ENV_PROPERTY = "BUILD_URL";
    private static final Logger LOGGER = Logger.getLogger(DynatraceServerConnection.class.getName());
    private final ApiClient apiClient;
    private DynatraceServerConfiguration configuration;

    public DynatraceServerConnection(final String serverUrl, final String apiTokenId, final boolean verifyCertificate,
                                     final boolean useProxy) {
        this.apiClient = new ApiClient()
                .setVerifyingSsl(verifyCertificate)
                .setBasePath(serverUrl)
                //.setDebugging(true)
                .setApiKey(DynatraceUtils.getApiToken(apiTokenId));

        Proxy proxy = Proxy.NO_PROXY;
        ProxyConfiguration proxyConfig = Jenkins.get().proxy;
        if (proxyConfig != null && useProxy) {
            proxy = proxyConfig.createProxy(PerfSigUIUtils.getHostFromUrl(serverUrl));
            if (proxyConfig.getUserName() != null) {
                // Add an authenticator which provides the credentials for proxy authentication
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    public PasswordAuthentication getPasswordAuthentication() {
                        if (getRequestorType() != RequestorType.PROXY) return null;
                        return new PasswordAuthentication(proxyConfig.getUserName(),
                                proxyConfig.getPassword().toCharArray());
                    }
                });
            }
        }
        apiClient.setProxy(proxy);
    }

    public DynatraceServerConnection(final DynatraceServerConfiguration config) {
        this(config.getServerUrl(), config.getApiTokenId(), config.isVerifyCertificate(), config.isUseProxy());
        this.configuration = config;
    }

    public boolean validateConnection() {
        try {
            getServerVersion();
            return true;
        } catch (CommandExecutionException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    public String getServerVersion() {
        ClusterVersionApi api = apiClient.createService(ClusterVersionApi.class);
        Call<ClusterVersion> call = api.getVersion();
        try {
            ApiResponse<ClusterVersion> version = apiClient.execute(call);
            return version.getData().getVersion();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error getting version of server: " + ex.getResponseBody(), ex);
        }
    }

    public List<TimeseriesDefinition> getTimeseries() {
        TimeseriesApi api = apiClient.createService(TimeseriesApi.class);
        Call<List<TimeseriesDefinition>> call = api.getAllTimeseriesDefinitions(FilterEnum.ALL.getValue(), null);
        try {
            ApiResponse<List<TimeseriesDefinition>> response = apiClient.execute(call);
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying timeseries: " + ex.getResponseBody(), ex);
        }
    }

    public TimeseriesDataPointQueryResult getTotalTimeseriesData(String timeseriesId, Long startTimestamp, Long endTimestamp,
                                                                 AggregationTypeEnum aggregationType, String entityIds, String tags) {
        return getTimeseriesData(timeseriesId, startTimestamp, endTimestamp, aggregationType, QueryModeEnum.TOTAL, entityIds, tags);
    }

    public TimeseriesDataPointQueryResult getTimeseriesData(String timeseriesId, Long startTimestamp, Long endTimestamp,
                                                            AggregationTypeEnum aggregationType, String entityIds, String tags) {
        return getTimeseriesData(timeseriesId, startTimestamp, endTimestamp, aggregationType, QueryModeEnum.SERIES, entityIds, tags);
    }

    private TimeseriesDataPointQueryResult getTimeseriesData(String timeseriesId, Long startTimestamp, Long endTimestamp,
                                                             AggregationTypeEnum aggregationType, QueryModeEnum queryMode, String entityIds, String tags) {
        TimeseriesApi api = apiClient.createService(TimeseriesApi.class);
        TimeseriesQueryMessage body = new TimeseriesQueryMessage()
                .timeseriesId(timeseriesId)
                .startTimestamp(startTimestamp)
                .endTimestamp(endTimestamp)
                .aggregationType(aggregationType)
                .queryMode(queryMode);
        if (StringUtils.isNotBlank(entityIds)) body.setEntities(Stream.of(entityIds.split(",")).map(String::trim).collect(Collectors.toList()));
        if (StringUtils.isNotBlank(tags)) body.setTags(Stream.of(tags.split(",")).map(String::trim).collect(Collectors.toList()));
        if (aggregationType == AggregationTypeEnum.PERCENTILE) body.percentile(98);

        Call<TimeseriesDataPointQueryResult.Container> call = api.readTimeseriesComplex(body);
        try {
            ApiResponse<TimeseriesDataPointQueryResult.Container> response = apiClient.execute(call);
            return response.getData().result;
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying timeseries data: " + ex.getResponseBody(), ex);
        }
    }

    public EventStoreResult createEvent(EventPushMessage event) {
        EventApi api = apiClient.createService(EventApi.class);
        try {
            ApiResponse<EventStoreResult> response = apiClient.execute(api.postNaturalEvent(event));
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while creating event: " + ex.getResponseBody(), ex);
        }
    }

    public DynatraceServerConfiguration getConfiguration() {
        return configuration;
    }

    public List<Application> getApplications() {
        TopologySmartscapeApplicationApi api = apiClient.createService(TopologySmartscapeApplicationApi.class);
        try {
            ApiResponse<List<Application>> response = apiClient.execute(api.getApplications(null, null, null, null, null));
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying applications: " + ex.getResponseBody(), ex);
        }
    }

    public List<Host> getHosts() {
        TopologySmartscapeHostApi api = apiClient.createService(TopologySmartscapeHostApi.class);
        try {
            ApiResponse<List<Host>> response = apiClient.execute(api.getHosts(null, null, null, null, true, null));
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying hosts: " + ex.getResponseBody(), ex);
        }
    }

    public List<ProcessGroupInstance> getProcesses() {
        TopologySmartscapeProcessApi api = apiClient.createService(TopologySmartscapeProcessApi.class);
        try {
            ApiResponse<List<ProcessGroupInstance>> response = apiClient.execute(api.getProcesses(null, null, null, null, null, null, null, null, null));
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying processes: " + ex.getResponseBody(), ex);
        }
    }

    public List<ProcessGroup> getProcessGroups() {
        TopologySmartscapeProcessGroupApi api = apiClient.createService(TopologySmartscapeProcessGroupApi.class);
        try {
            ApiResponse<List<ProcessGroup>> response = apiClient.execute(api.getProcessGroups(null, null, null, null, null));
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying process groups: " + ex.getResponseBody(), ex);
        }
    }

    public List<Service> getServices() {
        TopologySmartscapeServiceApi api = apiClient.createService(TopologySmartscapeServiceApi.class);
        try {
            ApiResponse<List<Service>> response = apiClient.execute(api.getServices(null, null, null, null, null));
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying services: " + ex.getResponseBody(), ex);
        }
    }
}
