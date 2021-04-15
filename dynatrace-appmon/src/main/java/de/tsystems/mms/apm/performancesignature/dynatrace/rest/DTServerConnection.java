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

package de.tsystems.mms.apm.performancesignature.dynatrace.rest;

import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CredProfilePair;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration.DescriptorImpl;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiClient;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiResponse;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api.AlertsIncidentsAndEventsApi;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api.CustomXMLApi;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api.LiveSessionsApi;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api.ServerManagementApi;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api.StoredSessionsApi;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api.SystemProfilesApi;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api.TestAutomationApi;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.ActivationStatus;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.Alerts;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.DeploymentEvent;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.EventUpdate;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.RecordingStatus;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.Result;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SessionRecordingOptions;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SessionStoringOptions;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.Sessions;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SystemProfileConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SystemProfileConfigurations;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SystemProfiles;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.TestRunDefinition;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.ContentRetrievalException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.Agent;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.AgentList;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.Dashboard;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.DashboardList;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.LicenseInformation;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.XmlResult;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.FilePath;
import hudson.ProxyConfiguration;
import io.mikael.urlbuilder.util.Encoder;
import jenkins.model.Jenkins;
import okhttp3.ResponseBody;
import org.apache.commons.lang.exception.ExceptionUtils;
import retrofit2.Response;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DTServerConnection {
    public static final String BUILD_URL_ENV_PROPERTY = "BUILD_URL";
    public static final String BUILD_VAR_KEY_VERSION_MAJOR = "dtVersionMajor";
    public static final String BUILD_VAR_KEY_VERSION_MINOR = "dtVersionMinor";
    public static final String BUILD_VAR_KEY_VERSION_REVISION = "dtVersionRevision";
    public static final String BUILD_VAR_KEY_VERSION_MILESTONE = "dtVersionMilestone";
    public static final String BUILD_VAR_KEY_CATEGORY = "dtCategory";
    public static final String BUILD_VAR_KEY_MARKER = "dtMarker";
    public static final String BUILD_VAR_KEY_PLATFORM = "dtPlatform";
    private static final String SESSION_PREFIX = "stored:";
    private static final Encoder URLENCODER = new Encoder(StandardCharsets.UTF_8);

    private final String systemProfile;
    private final ApiClient apiClient;
    private final CredProfilePair credProfilePair;
    private DynatraceServerConfiguration configuration;

    public DTServerConnection(final DynatraceServerConfiguration config, final CredProfilePair pair) {
        this(config.getServerUrl(), pair, config.isVerifyCertificate(), config.getReadTimeout(), config.isUseProxy());
        this.configuration = config;
    }

    public DTServerConnection(final String serverUrl, final CredProfilePair pair, final boolean verifyCertificate,
                              final int readTimeout, final boolean useProxy) {
        this.systemProfile = pair.getProfile();
        this.credProfilePair = pair;

        this.apiClient = new ApiClient()
                .setVerifyingSsl(verifyCertificate)
                .setBasePath(serverUrl)
                .setCredentials(pair.getCredentials())
                //.setDebugging(true)
                .setReadTimeout(readTimeout == 0 ? DescriptorImpl.defaultReadTimeout : readTimeout);

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
                                proxyConfig.getSecretPassword().getPlainText().toCharArray());
                    }
                });
            }
        }
        apiClient.setProxy(proxy);
    }

    public DynatraceServerConfiguration getConfiguration() {
        return configuration;
    }

    public CredProfilePair getCredProfilePair() {
        return credProfilePair;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public DashboardReport getDashboardReportFromXML(final String dashBoardName, final String sessionId, final String testCaseName) {
        CustomXMLApi api = apiClient.createService(CustomXMLApi.class);
        try {
            ApiResponse<DashboardReport> response = apiClient.execute(api.getXMLDashboard(dashBoardName, SESSION_PREFIX + sessionId));
            DashboardReport dashboardReport = response.getData();
            dashboardReport.setName(testCaseName);

            //handle dynamic measures in the dashboard xml probably
            dashboardReport.getChartDashlets().forEach(chartDashlet -> {
                List<Measure> dynamicMeasures = new ArrayList<>();
                List<Measure> oldMeasures = new ArrayList<>();
                chartDashlet.getMeasures().stream().filter(measure -> !measure.getMeasures().isEmpty()).forEach(measure -> {
                    oldMeasures.add(measure);
                    List<Measure> copy = new ArrayList<>(measure.getMeasures());
                    measure.getMeasures().clear();
                    dynamicMeasures.addAll(copy);
                });
                if (!dynamicMeasures.isEmpty()) {
                    chartDashlet.getMeasures().removeAll(oldMeasures);
                    chartDashlet.getMeasures().addAll(dynamicMeasures);
                }
                //filter out "Synthetic Web Requests by Timer Name - PurePath Response Time - "
                chartDashlet.getMeasures()
                        .stream().filter(m -> m.getName() != null)
                        .forEach(m -> m.setName(m.getName()
                                .replace("Synthetic Web Requests by Timer Name - PurePath Response Time - ", "")));
            });

            return dashboardReport;
        } catch (Exception ex) {
            throw new ContentRetrievalException(ExceptionUtils.getStackTrace(ex) + "could not retrieve records from Dynatrace server: " + dashBoardName, ex);
        }
    }

    public String getServerVersion() {
        ServerManagementApi api = apiClient.createService(ServerManagementApi.class);
        try {
            ApiResponse<Result> version = apiClient.execute(api.getVersion());
            return version.getData().getResult();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error getting version of server", ex);
        }
    }

    public String storeSession(final String sessionName, final Date timeframeStart, final Date timeframeEnd, final String recordingOption,
                               final boolean sessionLocked, final boolean appendTimestamp) {
        LiveSessionsApi api = apiClient.createService(LiveSessionsApi.class);
        SessionStoringOptions options = new SessionStoringOptions(sessionName, "Session recorded by Jenkins", appendTimestamp,
                recordingOption, sessionLocked, timeframeStart, timeframeEnd);
        try {
            ApiResponse<Void> response = apiClient.execute(api.storeSession(systemProfile, options));
            return PerfSigUtils.getIdFromLocationHeader(response);
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while storing purepaths: " + ex.getResponseBody(), ex);
        }
    }

    public String startRecording(final String sessionName, final String description, final String recordingOption,
                                 final boolean sessionLocked, final boolean appendTimestamp) {
        LiveSessionsApi api = apiClient.createService(LiveSessionsApi.class);
        SessionRecordingOptions options = new SessionRecordingOptions(sessionName, description, appendTimestamp, recordingOption, sessionLocked);
        try {
            ApiResponse<Void> response = apiClient.execute(api.postRecording(systemProfile, options));
            return PerfSigUtils.getIdFromLocationHeader(response);
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while starting session recording: " + ex.getResponseBody(), ex);
        }
    }

    public String stopRecording() {
        LiveSessionsApi api = apiClient.createService(LiveSessionsApi.class);
        try {
            ApiResponse<Void> response = apiClient.execute(api.stopRecording(systemProfile, new RecordingStatus(false)));
            return PerfSigUtils.getIdFromLocationHeader(response);
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while stopping session recording: " + ex.getResponseBody(), ex);
        }
    }

    public boolean getRecordingStatus() {
        LiveSessionsApi api = apiClient.createService(LiveSessionsApi.class);
        try {
            ApiResponse<RecordingStatus> response = apiClient.execute(api.getRecording(systemProfile));
            return response.getData().getRecording();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error querying session recording status: " + ex.getResponseBody(), ex);
        }
    }

    public Sessions getSessions() {
        StoredSessionsApi api = apiClient.createService(StoredSessionsApi.class);
        try {
            ApiResponse<Sessions> response = apiClient.execute(api.listStoredSessions());
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying sessions: " + ex.getResponseBody(), ex);
        }
    }

    public List<Dashboard> getDashboards() {
        CustomXMLApi api = apiClient.createService(CustomXMLApi.class);
        try {
            ApiResponse<DashboardList> response = apiClient.execute(api.listDashboards());
            return response.getData().getDashboards();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying dashboards: " + ex.getMessage(), ex);
        }
    }

    public SystemProfiles getSystemProfiles() {
        SystemProfilesApi api = apiClient.createService(SystemProfilesApi.class);
        try {
            ApiResponse<SystemProfiles> response = apiClient.execute(api.getProfiles());
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying profiles: " + ex.getResponseBody(), ex);
        }
    }

    public List<SystemProfileConfiguration> getProfileConfigurations() {
        SystemProfilesApi api = apiClient.createService(SystemProfilesApi.class);
        try {
            ApiResponse<SystemProfileConfigurations> response = apiClient.execute(api.getSystemProfileConfigurations(systemProfile));
            return response.getData().getConfigurations();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while querying configurations of profile " + systemProfile + ": " + ex.getResponseBody(), ex);
        }
    }

    public boolean activateConfiguration(final String configuration) {
        SystemProfilesApi api = apiClient.createService(SystemProfilesApi.class);
        try {
            apiClient.execute(api.putSystemProfileConfigurationStatus(systemProfile, configuration, new ActivationStatus("ENABLED")));
            return true;
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while activating configuration: " + ex.getResponseBody());
        }
    }

    public LicenseInformation getServerLicense() {
        CustomXMLApi api = apiClient.createService(CustomXMLApi.class);
        try {
            ApiResponse<LicenseInformation> response = apiClient.execute(api.getServerLicense());
            return response.getData();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying server license: " + ex.getMessage(), ex);
        }
    }

    public List<Agent> getAllAgents() {
        CustomXMLApi api = apiClient.createService(CustomXMLApi.class);
        try {
            ApiResponse<AgentList> response = apiClient.execute(api.getAllAgents());
            return response.getData().getAgents();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying agents: " + ex.getMessage(), ex);
        }
    }

    public List<Agent> getAgents() {
        return getAllAgents().stream()
                .filter(agent -> systemProfile.equals(agent.getSystemProfile()))
                .collect(Collectors.toList());
    }

    public boolean hotSensorPlacement(final int agentId) {
        CustomXMLApi api = apiClient.createService(CustomXMLApi.class);
        try {
            ApiResponse<XmlResult> response = apiClient.execute(api.hotSensorPlacement(agentId));
            return response.getData().isResultTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while doing hot sensor placement: " + ex.getMessage(), ex);
        }
    }

    public boolean getPDFReport(final String sessionName, final String comparedSessionName, final String dashboard, final FilePath outputFile) {
        CustomXMLApi api = apiClient.createService(CustomXMLApi.class);
        try {
            Response<ResponseBody> response = api.getPDFReport(dashboard, SESSION_PREFIX + sessionName,
                    SESSION_PREFIX + comparedSessionName, "PDF").execute();
            if (response.body() != null) {
                outputFile.copyFrom(response.body().byteStream());
                return true;
            }
            return false;
        } catch (Exception ex) {
            throw new CommandExecutionException("error while downloading PDF Report: " + ex.getMessage(), ex);
        }
    }

    public boolean downloadSession(final String sessionId, final FilePath outputFile, boolean removeConfidentialStrings) {
        StoredSessionsApi api = apiClient.createService(StoredSessionsApi.class);
        try {
            Response<ResponseBody> response = api.getStoredSession(sessionId, removeConfidentialStrings, null, null).execute();
            if (response.body() != null) {
                outputFile.copyFrom(response.body().byteStream());
                return true;
            }
            return false;

        } catch (Exception ex) {
            throw new CommandExecutionException("error while downloading session: " + ex.getMessage(), ex);
        }
    }

    public boolean deleteSession(final String sessionId) {
        StoredSessionsApi api = apiClient.createService(StoredSessionsApi.class);
        try {
            apiClient.execute(api.deleteStoredSession(sessionId));
            return true;
        } catch (Exception ex) {
            throw new CommandExecutionException("error while deleting session: " + ex.getMessage(), ex);
        }
    }

    public String threadDump(final String agentName, final String hostName, final int processId, final boolean sessionLocked) {
        CustomXMLApi api = apiClient.createService(CustomXMLApi.class);
        try {
            ApiResponse<XmlResult> response = apiClient.execute(api.createThreadDump(systemProfile, agentName, hostName, processId, sessionLocked));
            return response.getData().getValue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while creating thread dump: " + ex.getMessage(), ex);
        }
    }

    public boolean threadDumpStatus(final String threadDump) {
        CustomXMLApi api = apiClient.createService(CustomXMLApi.class);
        try {
            ApiResponse<XmlResult> response = apiClient.execute(api.getThreadDumpStatus(systemProfile, URLENCODER.encodePath(threadDump)));
            return response.getData().isSuccessTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying thread dump status: " + ex.getMessage(), ex);
        }
    }

    public String memoryDump(final String agentName, final String hostName, final int processId, final String dumpType,
                             final boolean sessionLocked, final boolean captureStrings, final boolean capturePrimitives, final boolean autoPostProcess,
                             final boolean doGC) {
        CustomXMLApi api = apiClient.createService(CustomXMLApi.class);
        try {
            ApiResponse<XmlResult> response = apiClient.execute(api.createMemoryDump(systemProfile, agentName, hostName, processId, dumpType, sessionLocked,
                    captureStrings, capturePrimitives, autoPostProcess, doGC));
            return response.getData().getValue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while creating memory dump: " + ex.getMessage(), ex);
        }
    }

    public boolean memoryDumpStatus(final String memoryDump) {
        CustomXMLApi api = apiClient.createService(CustomXMLApi.class);
        try {
            ApiResponse<XmlResult> response = apiClient.execute(api.getMemoryDumpStatus(systemProfile, URLENCODER.encodePath(memoryDump)));
            return response.getData().isSuccessTrue();
        } catch (Exception ex) {
            throw new CommandExecutionException("error while querying memory dump status: " + ex.getMessage(), ex);
        }
    }

    public String registerTestRun(final TestRunDefinition body) {
        TestAutomationApi api = apiClient.createService(TestAutomationApi.class);
        try {
            ApiResponse<TestRun> response = apiClient.execute(api.postTestRun(systemProfile, body));
            return response.getData().getId();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error registering test run: " + ex.getResponseBody(), ex);
        }
    }

    public TestRun finishTestRun(String testRunID) {
        TestAutomationApi api = apiClient.createService(TestAutomationApi.class);
        try {
            ApiResponse<TestRun> response = apiClient.execute(api.finishTestRun(systemProfile, testRunID));
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error finishing test run: " + ex.getResponseBody(), ex);
        }
    }

    public TestRun getTestRun(String testRunId) {
        TestAutomationApi api = apiClient.createService(TestAutomationApi.class);
        try {
            ApiResponse<TestRun> response = apiClient.execute(api.getTestrunById(systemProfile, testRunId));
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while getting test run details: " + ex.getResponseBody(), ex);
        }
    }

    public List<Alert> getIncidents(Date from, Date to) {
        SimpleDateFormat df = new SimpleDateFormat(ApiClient.REST_DF);
        AlertsIncidentsAndEventsApi api = apiClient.createService(AlertsIncidentsAndEventsApi.class);
        try {
            ApiResponse<Alerts> response = apiClient.execute(api.getIncidents(systemProfile, null, Alert.StateEnum.CREATED.getValue(),
                    df.format(from), df.format(to)));
            return response.getData().getAlerts().parallelStream().map(alertReference -> getIncident(alertReference.getId(), api)).collect(Collectors.toList());
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while getting incidents: " + ex.getResponseBody(), ex);
        }
    }

    private Alert getIncident(String id, AlertsIncidentsAndEventsApi api) {
        try {
            ApiResponse<Alert> response = apiClient.execute(api.getIncident(id));
            return response.getData();
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while getting incident details: " + ex.getResponseBody(), ex);
        }
    }

    public boolean updateDeploymentEvent(String eventId, EventUpdate body) {
        AlertsIncidentsAndEventsApi api = apiClient.createService(AlertsIncidentsAndEventsApi.class);
        try {
            apiClient.execute(api.updateDeploymentEvent(eventId, body));
            return true;
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while updating deployment event: " + ex.getResponseBody(), ex);
        }
    }

    public String createDeploymentEvent(DeploymentEvent event) {
        AlertsIncidentsAndEventsApi api = apiClient.createService(AlertsIncidentsAndEventsApi.class);
        try {
            ApiResponse<Void> response = apiClient.execute(api.createDeploymentEvent(event));
            return PerfSigUtils.getIdFromLocationHeader(response);
        } catch (ApiException ex) {
            throw new CommandExecutionException("error while creating deployment event: " + ex.getResponseBody(), ex);
        }
    }
}
