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

package de.tsystems.mms.apm.performancesignature.dynatrace;

import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.ConfigurationTestCase;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.ConfigurationTestCase.ConfigurationTestCaseDescriptor;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.Dashboard;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.UnitTestCase;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SessionData;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.ui.model.ClientLinkGenerator;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PluginLogger;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.DescriptorExtensionList;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerfSigRecorder extends Recorder implements SimpleBuildStep {
    private final String dynatraceProfile;
    private final List<ConfigurationTestCase> configurationTestCases;
    private boolean exportSessions;
    private boolean deleteSessions;
    private boolean removeConfidentialStrings;
    private int nonFunctionalFailure;
    private transient List<SessionData> availableSessions;

    @DataBoundConstructor
    public PerfSigRecorder(final String dynatraceProfile, final List<ConfigurationTestCase> configurationTestCases) {
        this.dynatraceProfile = dynatraceProfile;
        this.configurationTestCases = configurationTestCases;
    }

    @Override
    public void perform(@NonNull Run<?, ?> run, @NonNull FilePath workspace, @NonNull EnvVars env, @NonNull Launcher launcher,
                        @NonNull TaskListener listener) throws InterruptedException, IOException {
        PluginLogger logger = PerfSigUIUtils.createLogger(listener.getLogger());
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceProfile);
        DynatraceServerConfiguration serverConfiguration = connection.getConfiguration();
        if (serverConfiguration == null) {
            throw new AbortException(Messages.PerfSigRecorder_FailedToLookupServer());
        }

        if (configurationTestCases == null) {
            throw new AbortException(Messages.PerfSigRecorder_MissingTestCases());
        }

        if (serverConfiguration.getDelay() != 0) {
            logger.log(Messages.PerfSigRecorder_SleepingDelay(serverConfiguration.getDelay()));
            Thread.sleep(serverConfiguration.getDelay() * 1000L);
        }

        if (connection.getRecordingStatus()) {
            logger.log(Messages.PerfSigStartRecording_AnotherSessionStillRecording());
            PerfSigStopRecording stopRecording = new PerfSigStopRecording(dynatraceProfile);
            stopRecording.perform(run, workspace, env, launcher, listener);
        }

        String sessionId;
        String comparisonSessionId = null;
        String comparisonSessionName = null;
        String singleFilename;
        String comparisonFilename;
        int comparisonBuildNumber = 0;
        final int buildNumber = run.getNumber();
        final List<DashboardReport> dashboardReports = new ArrayList<>();

        Run<?, ?> previousRun = run.getPreviousNotFailedBuild();
        if (previousRun != null) {
            Result previousRunResult = previousRun.getResult();
            Run<?, ?> previousCompletedRun = run.getPreviousCompletedBuild();
            if (previousRunResult != null && !previousRunResult.isCompleteBuild() && previousCompletedRun != null) {
                previousRun = previousCompletedRun;
            }
            comparisonBuildNumber = previousRun.getNumber();
            logger.log(Messages.PerfSigRecorder_LastSuccessfulBuild(String.valueOf(comparisonBuildNumber)));
        } else {
            logger.log(Messages.PerfSigRecorder_NoComparisonPossible());
        }

        try {
            for (ConfigurationTestCase configurationTestCase : getConfigurationTestCases()) {
                if (!configurationTestCase.validate()) {
                    throw new AbortException(Messages.PerfSigRecorder_TestCaseValidationError());
                }
                logger.log(Messages.PerfSigRecorder_ConnectionSuccessful(configurationTestCase.getName()));

                final PerfSigEnvInvisAction buildEnvVars = getBuildEnvVars(run, configurationTestCase.getName());
                if (buildEnvVars != null) {
                    sessionId = buildEnvVars.getSessionId();
                } else {
                    throw new AbortException(Messages.PerfSigRecorder_NoSessionNameFound());
                }

                if (comparisonBuildNumber != 0) {
                    final PerfSigEnvInvisAction otherEnvVars = getBuildEnvVars(previousRun, configurationTestCase.getName());
                    if (otherEnvVars != null) {
                        comparisonSessionId = otherEnvVars.getSessionId();
                        comparisonSessionName = otherEnvVars.getSessionName();
                    }
                }

                availableSessions = connection.getSessions().getSessions();
                int retryCount = 0;
                while ((!validateSessionId(sessionId)) && (retryCount < serverConfiguration.getRetryCount())) {
                    retryCount++;
                    availableSessions = connection.getSessions().getSessions();
                    logger.log(Messages.PerfSigRecorder_WaitingForSession(retryCount, serverConfiguration.getRetryCount()));
                    Thread.sleep(10000L);
                }

                if (!validateSessionId(sessionId)) {
                    throw new AbortException(Messages.PerfSigRecorder_SessionNotAvailable(sessionId));
                }
                if (!validateSessionId(comparisonSessionId)) {
                    logger.log(Messages.PerfSigRecorder_ComparisonNotPossible(comparisonSessionId));
                }

                for (Dashboard singleDashboard : configurationTestCase.getSingleDashboards()) {
                    singleFilename = "Singlereport_" + buildEnvVars.getSessionName() + "_" + singleDashboard.getName() + ".pdf";
                    logger.log(Messages.PerfSigRecorder_GettingPDFReport() + " " + singleFilename);
                    boolean singleResult = connection.getPDFReport(sessionId, null, singleDashboard.getName(),
                            new FilePath(PerfSigUIUtils.getReportDirectory(run), singleFilename));
                    if (!singleResult) {
                        throw new RESTErrorException(Messages.PerfSigRecorder_SingleReportError());
                    }
                }
                for (Dashboard comparisonDashboard : configurationTestCase.getComparisonDashboards()) {
                    if (comparisonSessionId != null && comparisonSessionName != null && validateSessionId(comparisonSessionId)) {
                        comparisonFilename = "Comparisonreport_" + comparisonSessionName.replace(comparisonBuildNumber + "_",
                                buildNumber + "_" + comparisonBuildNumber + "_") + "_" + comparisonDashboard.getName() + ".pdf";
                        logger.log(Messages.PerfSigRecorder_GettingPDFReport() + " " + comparisonFilename);
                        boolean comparisonResult = connection.getPDFReport(sessionId, comparisonSessionId, comparisonDashboard.getName(),
                                new FilePath(PerfSigUIUtils.getReportDirectory(run), comparisonFilename));
                        if (!comparisonResult) {
                            throw new RESTErrorException(Messages.PerfSigRecorder_ComparisonReportError());
                        }
                    }
                }
                logger.log(Messages.PerfSigRecorder_ParseXMLReport());
                final List<Alert> incidents = connection.getIncidents(buildEnvVars.getTimeframeStart(), buildEnvVars.getTimeframeStop());
                final DashboardReport dashboardReport = connection.getDashboardReportFromXML(configurationTestCase.getXmlDashboard(), sessionId, configurationTestCase.getName());
                if (dashboardReport == null || CollectionUtils.isEmpty(dashboardReport.getChartDashlets())) {
                    throw new RESTErrorException(Messages.PerfSigRecorder_XMLReportError());
                }

                dashboardReport.getIncidents().addAll(incidents);
                dashboardReport.setUnitTest(configurationTestCase instanceof UnitTestCase);
                ClientLinkGenerator clientLinkGenerator = new ClientLinkGenerator(serverConfiguration.getServerUrl(), configurationTestCase.getXmlDashboard(),
                        sessionId, configurationTestCase.getClientDashboard());
                dashboardReport.setClientUrl(clientLinkGenerator.generateLink());
                dashboardReports.add(dashboardReport);

                if (exportSessions) {
                    boolean exportedSession = connection.downloadSession(sessionId,
                            new FilePath(PerfSigUIUtils.getReportDirectory(run), buildEnvVars.getSessionName() + ".dts"), removeConfidentialStrings);
                    if (!exportedSession) {
                        throw new RESTErrorException(Messages.PerfSigRecorder_SessionDownloadError());
                    } else {
                        logger.log(Messages.PerfSigRecorder_SessionDownloadSuccessful());
                    }
                }

                if (deleteSessions && validateSessionId(comparisonSessionId)) {
                    boolean deletedSession = connection.deleteSession(comparisonSessionId);
                    if (!deletedSession) {
                        logger.log(Messages.PerfSigRecorder_SessionDeleteError(comparisonSessionName));
                    } else {
                        logger.log(Messages.PerfSigRecorder_SessionDeleteSuccessful(comparisonSessionName));
                    }
                }

                PerfSigUIUtils.handleIncidents(run, dashboardReport.getIncidents(), logger, nonFunctionalFailure);
            }
        } finally {
            PerfSigBuildAction action = new PerfSigBuildAction(dashboardReports);
            run.addAction(action);
        }
    }

    @CheckForNull
    private PerfSigEnvInvisAction getBuildEnvVars(final Run<?, ?> build, final String testCase) {
        final List<PerfSigEnvInvisAction> envVars = build.getActions(PerfSigEnvInvisAction.class);
        return envVars.stream().filter(vars -> vars.getTestCase().equals(testCase)).findFirst().orElse(null);
    }

    private boolean validateSessionId(final String id) {
        if (StringUtils.isBlank(id)) return false;
        return availableSessions.stream().anyMatch(session -> session.getId().equalsIgnoreCase(id));
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public boolean isExportSessions() {
        return exportSessions;
    }

    @DataBoundSetter
    public void setExportSessions(final boolean exportSessions) {
        this.exportSessions = exportSessions;
    }

    public boolean isDeleteSessions() {
        return deleteSessions;
    }

    @DataBoundSetter
    public void setDeleteSessions(boolean deleteSessions) {
        this.deleteSessions = deleteSessions;
    }

    public List<ConfigurationTestCase> getConfigurationTestCases() {
        return configurationTestCases == null ? Collections.emptyList() : configurationTestCases;
    }

    public int getNonFunctionalFailure() {
        return nonFunctionalFailure;
    }

    @DataBoundSetter
    public void setNonFunctionalFailure(final int nonFunctionalFailure) {
        this.nonFunctionalFailure = nonFunctionalFailure < 0 ? DescriptorImpl.defaultNonFunctionalFailure : nonFunctionalFailure;
    }

    public String getDynatraceProfile() {
        return dynatraceProfile;
    }

    public boolean isRemoveConfidentialStrings() {
        return removeConfidentialStrings;
    }

    @DataBoundSetter
    public void setRemoveConfidentialStrings(boolean removeConfidentialStrings) {
        this.removeConfidentialStrings = removeConfidentialStrings;
    }

    @Symbol("perfSigReports")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public static final boolean defaultExportSessions = true;
        public static final boolean defaultDeleteSessions = false;
        public static final boolean defaultRemoveConfidentialStrings = true;
        public static final int defaultNonFunctionalFailure = 2;

        public DescriptorImpl() {
            load();
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillDynatraceProfileItems(@AncestorInPath Item item) {
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return new ListBoxModel();
            }
            return PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.PerfSigRecorder_DisplayName();
        }

        public DescriptorExtensionList<ConfigurationTestCase, Descriptor<ConfigurationTestCase>> getTestCaseTypes() {
            return ConfigurationTestCaseDescriptor.all();
        }
    }
}
