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

package de.tsystems.mms.apm.performancesignature.viewer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.viewer.rest.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.ConnectionHelper;
import de.tsystems.mms.apm.performancesignature.viewer.rest.ContentRetrievalException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.model.Artifact;
import de.tsystems.mms.apm.performancesignature.viewer.rest.model.BuildData;
import de.tsystems.mms.apm.performancesignature.viewer.rest.model.RootElement;
import hudson.AbortException;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.BuildContext;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteJenkinsServer;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.pipeline.Handle;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PullPerfSigDataStepExecution extends SynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 1L;
    private transient final PullPerfSigDataStep step;
    private final transient Gson gson;

    PullPerfSigDataStepExecution(final PullPerfSigDataStep step, final StepContext context) throws AbortException {
        super(context);
        if (step.getHandle() == null) {
            throw new AbortException("'handle' has not been defined for this 'pullPerfSigReports' step");
        }
        this.step = step;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Date.class,
                        new JsonDeserializer<Date>() {
                            @Override
                            public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context1) throws JsonParseException {
                                return new Date(jsonElement.getAsJsonPrimitive().getAsLong());
                            }
                        })
                .create();
    }

    @Override
    protected Void run() throws Exception {
        StepContext stepContext = getContext();
        Run<?, ?> run = stepContext.get(Run.class);
        FilePath workspace = stepContext.get(FilePath.class);
        TaskListener listener = stepContext.get(TaskListener.class);

        if (run == null || listener == null) {
            throw new AbortException("run or listener are not available");
        }
        PluginLogger logger = PerfSigUIUtils.createLogger(listener.getLogger());
        BuildContext context = new BuildContext(run, workspace, listener, listener.getLogger(), new RemoteJenkinsServer());

        if (!step.isIgnorePerfSigData()) {
            logger.log("parsing Performance Signature data from job " + step.getHandle().getJobName() + " #" + step.getHandle().getBuildNumber());
            final List<DashboardReport> dashboardReports = getMeasureDataFromJSON(context, step.getHandle());
            if (dashboardReports == null) {
                throw new RESTErrorException(Messages.PullPerfSigDataStep_JSONReportError());
            }

            for (DashboardReport dashboardReport : dashboardReports) {
                boolean exportedSession = downloadSession(context, PerfSigUIUtils.getReportDirectory(run), dashboardReport.getName(), logger);
                if (!exportedSession) {
                    logger.log(Messages.PullPerfSigDataStep_SessionDownloadError(dashboardReport.getName()));
                } else {
                    logger.log(Messages.PullPerfSigDataStep_SessionDownloadSuccessful(dashboardReport.getName()));
                }

                PerfSigUIUtils.handleIncidents(run, dashboardReport.getIncidents(), logger, step.getNonFunctionalFailure());
            }

            boolean exportedPDFReports = downloadPDFReports(context, PerfSigUIUtils.getReportDirectory(run), logger);
            if (!exportedPDFReports) {
                logger.log(Messages.PullPerfSigDataStep_ReportDownloadError());
            } else {
                logger.log(Messages.PullPerfSigDataStep_ReportDownloadSuccessful());
            }

            PerfSigBuildAction action = new PerfSigBuildAction(dashboardReports);
            run.addAction(action);
        }

        boolean downloadedArtifacts = downloadArtifacts(context, workspace, logger);
        if (!downloadedArtifacts) {
            logger.log(Messages.PullPerfSigDataStep_ArtifactDownloadError());
        } else {
            logger.log(Messages.PullPerfSigDataStep_ArtifactDownloadSuccessful());
        }

        return null;
    }

    private List<DashboardReport> getMeasureDataFromJSON(final BuildContext context, final Handle handle)
            throws InterruptedException {
        try {
            URL url = new URL(handle.getBuildUrl() + "performance-signature/api/json?depth=10");
            ConnectionHelper connectionHelper = new ConnectionHelper(handle);
            String json = connectionHelper.getStringFromUrl(url, context);

            RootElement rootElement = gson.fromJson(json, RootElement.class);
            return rootElement.getDashboardReports();
        } catch (IOException | JsonSyntaxException e) {
            throw new ContentRetrievalException(ExceptionUtils.getStackTrace(e) + "could not retrieve records from remote Jenkins: ", e);
        }
    }

    private List<String> getReportList(final BuildContext context, final ReportType type)
            throws IOException, InterruptedException {
        URL url = new URL(step.getHandle().getBuildUrl() + "performance-signature/get" + type + "ReportList");
        ConnectionHelper connectionHelper = new ConnectionHelper(step.getHandle());
        String json = connectionHelper.getStringFromUrl(url, context);

        List<String> obj = gson.fromJson(json, new TypeToken<List<String>>() {
        }.getType());
        return obj != null ? obj : Collections.<String>emptyList();
    }

    private List<Artifact> getArtifactsList(final BuildContext context) throws IOException, InterruptedException {
        URL url = new URL(step.getHandle().getBuildUrl() + "api/json");
        ConnectionHelper connectionHelper = new ConnectionHelper(step.getHandle());
        String json = connectionHelper.getStringFromUrl(url, context);

        BuildData artifacts = gson.fromJson(json, new TypeToken<BuildData>() {
        }.getType());
        return artifacts != null ? artifacts.getArtifacts() : Collections.<Artifact>emptyList();
    }

    private boolean downloadArtifacts(final BuildContext context, final FilePath dir, final PluginLogger logger)
            throws InterruptedException {
        boolean result = true;
        try {
            List<Artifact> artifactsList = getArtifactsList(context);
            for (Artifact artifact : artifactsList) {
                URL url = new URL(step.getHandle().getBuildUrl() + "artifact/" + artifact.getRelativePath());
                result &= downloadArtifact(context, new FilePath(dir, artifact.getFileName()), url, logger);
            }
            return result;
        } catch (IOException e) {
            throw new CommandExecutionException("error downloading Artifacts: " + e.getMessage(), e);
        }
    }

    private boolean downloadPDFReports(final BuildContext context, final FilePath dir, final PluginLogger logger)
            throws InterruptedException {
        boolean result = true;
        try {
            for (ReportType reportType : ReportType.values()) {
                List reportlist = getReportList(context, reportType);
                for (Object report : reportlist) {
                    URL url = new URL(step.getHandle().getBuildUrl() + "performance-signature/get" + reportType + "Report?number="
                            + reportlist.indexOf(report));
                    result &= downloadArtifact(context, new FilePath(dir, report + ".pdf"), url, logger);
                }
            }
            return result;
        } catch (IOException e) {
            throw new CommandExecutionException("error downloading PDF Reports: " + e.getMessage(), e);
        }
    }

    private boolean downloadSession(final BuildContext context, final FilePath dir,
                                    final String testCase, final PluginLogger logger) throws InterruptedException {
        try {
            URL url = new URL(step.getHandle().getBuildUrl() + "performance-signature/getSession?testCase=" + testCase);
            String sessionFileName = step.getHandle().getJobName() + "_Build_" + step.getHandle().getBuildNumber() + "_" + testCase + ".dts";
            return downloadArtifact(context, new FilePath(dir, sessionFileName), url, logger);
        } catch (IOException e) {
            throw new CommandExecutionException("error downloading sessions: " + e.getMessage(), e);
        }
    }

    private boolean downloadArtifact(final BuildContext context, final FilePath file, final URL url, final PluginLogger logger) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper(step.getHandle());
            InputStream inputStream = connectionHelper.getInputStreamFromUrl(url, context);
            file.copyFrom(inputStream);
            return true;
        } catch (IOException | InterruptedException e) {
            logger.log("Could not download artifact: " + FilenameUtils.getBaseName(url.toString()));
            return false;
        }
    }

    private enum ReportType {
        Single, Comparison
    }
}
