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

package de.tsystems.mms.apm.performancesignature.dynatracesaas.rest;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api.ServerManagementApi;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.api.TimeSeriesApi;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Result;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Timeseries;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Timeseries.AggregationEnum;

public class DynatraceServerConnection {
    private static final Logger LOGGER = Logger.getLogger(DynatraceServerConnection.class.getName());
    private final ApiClient apiClient;

    public DynatraceServerConnection(final String serverUrl, final String apiTokenId, final boolean verifyCertificate,
                                     final boolean useProxy) {
        this.apiClient = new ApiClient();
        apiClient.setVerifyingSsl(verifyCertificate);
        apiClient.setBasePath(serverUrl);
        apiClient.setApiKeyPrefix("Api-Token");
        apiClient.setApiKey(DynatraceUtils.getApiToken(apiTokenId));
        //apiClient.setDebugging(true);

        Proxy proxy = Proxy.NO_PROXY;
        ProxyConfiguration proxyConfig = Jenkins.getInstance().proxy;
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
        ServerManagementApi api = new ServerManagementApi(apiClient);
        try {
            return api.getVersion();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error getting version of server: " + ex.getResponseBody(), ex);
        }
    }

    public List<Timeseries> getTimeseries() {
        TimeSeriesApi api = new TimeSeriesApi(apiClient);
        try {
            return api.getTimeseries();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying timeseries: " + ex.getResponseBody(), ex);
        }
    }

    public Result getTotalTimeseriesData(String timeseriesId, Date startTimestamp, Date endTimestamp,
                                         AggregationEnum aggregationType) {
        TimeSeriesApi api = new TimeSeriesApi(apiClient);
        try {
            return api.getTimeseriesData(timeseriesId, startTimestamp, endTimestamp, aggregationType, "total");
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying timeseries data: " + ex.getResponseBody(), ex);
        }
    }

    public Result getTimeseriesData(String timeseriesId, Date startTimestamp, Date endTimestamp,
                                    AggregationEnum aggregationType) {
        TimeSeriesApi api = new TimeSeriesApi(apiClient);
        try {
            return api.getTimeseriesData(timeseriesId, startTimestamp, endTimestamp, aggregationType, "series");
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying timeseries data: " + ex.getResponseBody(), ex);
        }
    }
}
