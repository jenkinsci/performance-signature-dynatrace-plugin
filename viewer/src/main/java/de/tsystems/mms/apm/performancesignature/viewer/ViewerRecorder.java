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
import de.tsystems.mms.apm.performancesignature.viewer.rest.ContentRetrievalException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.model.RootElement;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.BasicBuildContext;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.BuildContext;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteJenkinsServer;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.pipeline.Handle;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.utils.FormValidationUtils;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewerRecorder extends Step {
    private final RemoteBuildConfiguration remoteBuildConfig;
    private final Handle handle;
    private String remoteJenkinsName;
    private int nonFunctionalFailure;

    @DataBoundConstructor
    public ViewerRecorder(final Handle handle, final String remoteJenkinsName) {
        remoteBuildConfig = new RemoteBuildConfiguration();
        remoteBuildConfig.setShouldNotFailBuild(false);     //We need to get notified. Failure feedback is collected async then.
        remoteBuildConfig.setBlockBuildUntilComplete(true); //default for Pipeline Step
        remoteBuildConfig.setJob(handle.getJobFullDisplayName());
        remoteBuildConfig.setRemoteJenkinsName(remoteJenkinsName);

        this.remoteJenkinsName = remoteJenkinsName;
        this.handle = handle;
    }

    public String getHandle() {
        return handle.toString();
    }

    public String getRemoteJenkinsName() {
        return remoteJenkinsName;
    }

    public int getNonFunctionalFailure() {
        return nonFunctionalFailure;
    }

    @DataBoundSetter
    public void setNonFunctionalFailure(final int nonFunctionalFailure) {
        this.nonFunctionalFailure = nonFunctionalFailure < 0 ? DescriptorImpl.defaultNonFunctionalFailure : nonFunctionalFailure;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, remoteBuildConfig, handle);
    }

    private enum ReportType {
        Single, Comparison
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {
        public static final int defaultNonFunctionalFailure = 0;

        @Override
        public String getFunctionName() {
            return "pullPerfSigReports";
        }

        @Override
        public String getDisplayName() {
            return Messages.ViewerRecorder_DisplayName();
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            Set<Class<?>> set = new HashSet<>();
            Collections.addAll(set, Run.class, FilePath.class, Launcher.class, TaskListener.class);
            return set;
        }

        @Restricted(NoExternalUse.class)
        @Nonnull
        public ListBoxModel doFillRemoteJenkinsNameItems() {
            RemoteBuildConfiguration.DescriptorImpl descriptor = Descriptor.findByDescribableClassName(
                    ExtensionList.lookup(RemoteBuildConfiguration.DescriptorImpl.class), RemoteBuildConfiguration.class.getName());
            if (descriptor == null) throw new RuntimeException("Could not get descriptor for RemoteBuildConfiguration");
            return descriptor.doFillRemoteJenkinsNameItems();
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doCheckRemoteJenkinsName(
                @QueryParameter("remoteJenkinsName") final String value,
                @QueryParameter("remoteJenkinsUrl") final String remoteJenkinsUrl,
                @QueryParameter("job") final String job) {
            FormValidationUtils.RemoteURLCombinationsResult result = FormValidationUtils.checkRemoteURLCombinations(remoteJenkinsUrl, value, job);
            if (result.isAffected(FormValidationUtils.AffectedField.REMOTE_JENKINS_NAME)) return result.formValidation;
            return FormValidation.ok();
        }
    }

    public class Execution extends SynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = 5339071627093320735L;
        private final RemoteBuildConfiguration remoteBuildConfig;
        private final Handle handle;
        private final String buildUrlString;

        Execution(StepContext context, RemoteBuildConfiguration remoteBuildConfig, Handle handle) {
            super(context);
            this.remoteBuildConfig = remoteBuildConfig;
            this.handle = handle;
            this.buildUrlString = handle.getJobUrl() + "/" + handle.getBuildNumber();
        }

        @Override
        protected Void run() throws Exception {
            StepContext stepContext = getContext();
            Run<?, ?> run = stepContext.get(Run.class);
            FilePath workspace = stepContext.get(FilePath.class);
            TaskListener listener = stepContext.get(TaskListener.class);
            PluginLogger logger = PerfSigUIUtils.createLogger(listener.getLogger());

            RemoteJenkinsServer effectiveRemoteServer = remoteBuildConfig.evaluateEffectiveRemoteHost(new BasicBuildContext(run, workspace, listener));
            BuildContext context = new BuildContext(run, workspace, listener, listener.getLogger(), effectiveRemoteServer);

            logger.log("parsing xml data from job " + handle.getJobName() + " #" + handle.getBuildNumber());
            final List<DashboardReport> dashboardReports = getMeasureDataFromJSON(context);
            if (dashboardReports == null) {
                throw new RESTErrorException(Messages.ViewerRecorder_XMLReportError());
            }

            for (DashboardReport dashboardReport : dashboardReports) {
                boolean exportedSession = downloadSession(context, PerfSigUIUtils.getReportDirectory(run), dashboardReport.getName());
                if (!exportedSession) {
                    logger.log(Messages.ViewerRecorder_SessionDownloadError(dashboardReport.getName()));
                } else {
                    logger.log(Messages.ViewerRecorder_SessionDownloadSuccessful(dashboardReport.getName()));
                }

                PerfSigUIUtils.handleIncidents(run, dashboardReport.getIncidents(), logger, nonFunctionalFailure);
            }

            boolean exportedPDFReports = downloadPDFReports(context, PerfSigUIUtils.getReportDirectory(run));
            if (!exportedPDFReports) {
                logger.log(Messages.ViewerRecorder_ReportDownloadError());
            } else {
                logger.log(Messages.ViewerRecorder_ReportDownloadSuccessful());
            }

            PerfSigBuildAction action = new PerfSigBuildAction(dashboardReports);
            run.addAction(action);
            return null;
        }

        private List<DashboardReport> getMeasureDataFromJSON(BuildContext context) {
            try {
                JSONObject response = remoteBuildConfig.sendHTTPCall(buildUrlString + "/performance-signature/api/json?depth=10", "GET", context);

                RootElement rootElement = new Gson().fromJson(response.toString(), RootElement.class);
                return rootElement.getDashboardReports();
            } catch (IOException | JsonSyntaxException e) {
                throw new ContentRetrievalException(ExceptionUtils.getStackTrace(e) + "could not retrieve records from remote Jenkins: ", e);
            }
        }

        private boolean downloadSession(final BuildContext context, final FilePath dir, final String testCase) {
            try {
                String response = remoteBuildConfig.sendHTTPCall(buildUrlString
                        + "/performance-signature/getSession?testCase=" + testCase, "GET", context, 3).getRawBody();
                String sessionFileName = handle.getJobName() + "_Build_" + handle.getBuildNumber() + "_" + testCase + ".dts";
                return downloadArtifact(context, new FilePath(dir, sessionFileName), response);
            } catch (IOException e) {
                throw new CommandExecutionException("error downloading sessions: " + e.getMessage(), e);
            }
        }

        private boolean downloadPDFReports(final BuildContext context, final FilePath dir) {
            boolean result = true;
            try {
                for (ReportType reportType : ReportType.values()) {
                    List reportlist = getReportList(context, reportType);
                    for (Object report : reportlist) {
                        /*URL url = new URL(getJenkinsJob().getUrl() + buildNumber + "/performance-signature/get" + reportType + "Report?number="
                                + reportlist.indexOf(report));
                        result &= downloadArtifact(context, new FilePath(dir, report + ".pdf"), response);*/
                    }
                }
                return result;
            } catch (IOException | InterruptedException e) {
                throw new CommandExecutionException("error downloading PDF Reports: " + e.getMessage(), e);
            }
        }

        private List getReportList(final BuildContext context, final ReportType type) throws IOException, InterruptedException {

            JSONObject response = remoteBuildConfig.sendHTTPCall(buildUrlString + "/performance-signature/get" + type + "ReportList", "GET", context);
            /*String xml = getJenkinsJob().getClient().get(url.toString());
            XStream2 xStream = new XStream2();
            List obj = (List) xStream.fromXML(xml);
            return obj != null ? obj : Collections.emptyList();*/
            return null;
        }

        private boolean downloadArtifact(final BuildContext context, final FilePath file, final String response) {
            try {
                file.write(response, null);
                return true;
            } catch (IOException | InterruptedException e) {
                context.logger.println("[PERFSIG] Could not download artifact: " + file.getName());
                return false;
            }
        }
    }
}
