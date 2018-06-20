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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.viewer.rest.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.ConnectionHelper;
import de.tsystems.mms.apm.performancesignature.viewer.rest.ContentRetrievalException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.model.RootElement;
import hudson.AbortException;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.util.XStream2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.BuildContext;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteJenkinsServer;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.pipeline.Handle;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class ViewerRecorderExecution extends SynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 5339071627093320735L;
    private final Handle handle;
    private int nonFunctionalFailure;

    ViewerRecorderExecution(final StepContext context, final Handle handle, final int nonFunctionalFailure) {
        super(context);
        this.handle = handle;
        this.nonFunctionalFailure = nonFunctionalFailure;
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

        logger.log("parsing xml data from job " + handle.getJobName() + " #" + handle.getBuildNumber());
        final List<DashboardReport> dashboardReports = getMeasureDataFromJSON(context, handle);
        if (dashboardReports == null) {
            throw new RESTErrorException(Messages.ViewerRecorder_XMLReportError());
        }

        for (DashboardReport dashboardReport : dashboardReports) {
            boolean exportedSession = downloadSession(context, handle, PerfSigUIUtils.getReportDirectory(run), dashboardReport.getName(), logger);
            if (!exportedSession) {
                logger.log(Messages.ViewerRecorder_SessionDownloadError(dashboardReport.getName()));
            } else {
                logger.log(Messages.ViewerRecorder_SessionDownloadSuccessful(dashboardReport.getName()));
            }

            PerfSigUIUtils.handleIncidents(run, dashboardReport.getIncidents(), logger, nonFunctionalFailure);
        }

        boolean exportedPDFReports = downloadPDFReports(context, handle, PerfSigUIUtils.getReportDirectory(run), logger);
        if (!exportedPDFReports) {
            logger.log(Messages.ViewerRecorder_ReportDownloadError());
        } else {
            logger.log(Messages.ViewerRecorder_ReportDownloadSuccessful());
        }

        PerfSigBuildAction action = new PerfSigBuildAction(dashboardReports);
        run.addAction(action);
        return null;
    }

    private List<DashboardReport> getMeasureDataFromJSON(final BuildContext context, final Handle handle)
            throws InterruptedException {
        try {
            URL url = new URL(handle.getBuildUrl() + "/performance-signature/api/json?depth=10");
            ConnectionHelper connectionHelper = new ConnectionHelper(handle);
            String json = connectionHelper.getStringFromUrl(url, context);

            RootElement rootElement = new Gson().fromJson(json, RootElement.class);
            return rootElement.getDashboardReports();
        } catch (IOException | JsonSyntaxException e) {
            throw new ContentRetrievalException(ExceptionUtils.getStackTrace(e) + "could not retrieve records from remote Jenkins: ", e);
        }
    }

    private List getReportList(final BuildContext context, final Handle handle, final ReportType type)
            throws IOException, InterruptedException {
        URL url = new URL(handle.getBuildUrl() + "/performance-signature/get" + type + "ReportList");
        ConnectionHelper connectionHelper = new ConnectionHelper(handle);
        String xml = connectionHelper.getStringFromUrl(url, context);
        XStream2 xStream = new XStream2();
        List obj = (List) xStream.fromXML(xml);
        return obj != null ? obj : Collections.emptyList();
    }

    private boolean downloadPDFReports(final BuildContext context, final Handle handle, final FilePath dir, final PluginLogger logger)
            throws InterruptedException {
        boolean result = true;
        try {
            for (ReportType reportType : ReportType.values()) {
                List reportlist = getReportList(context, handle, reportType);
                for (Object report : reportlist) {
                    URL url = new URL(handle.getBuildUrl() + "/performance-signature/get" + reportType + "Report?number="
                            + reportlist.indexOf(report));
                    result &= downloadArtifact(context, new FilePath(dir, report + ".pdf"), url, logger);
                }
            }
            return result;
        } catch (IOException e) {
            throw new CommandExecutionException("error downloading PDF Reports: " + e.getMessage(), e);
        }
    }

    private boolean downloadSession(final BuildContext context, final Handle handle, final FilePath dir,
                                    final String testCase, final PluginLogger logger) throws InterruptedException {
        try {
            URL url = new URL(handle.getBuildUrl() + "/performance-signature/getSession?testCase=" + testCase);
            String sessionFileName = handle.getJobName() + "_Build_" + handle.getBuildNumber() + "_" + testCase + ".dts";
            return downloadArtifact(context, new FilePath(dir, sessionFileName), url, logger);
        } catch (IOException e) {
            throw new CommandExecutionException("error downloading sessions: " + e.getMessage(), e);
        }
    }

    private boolean downloadArtifact(final BuildContext context, final FilePath file, final URL url, final PluginLogger logger) {
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper(handle);
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
