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

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteBuildConfiguration;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.pipeline.Handle;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.utils.FormValidationUtils;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.util.Set;

public class ViewerInputTrigger extends Step {
    private final RemoteBuildConfiguration remoteBuildConfig;
    private final Handle handle;
    private final String remoteJenkinsName;
    private final String triggerId;

    @DataBoundConstructor
    public ViewerInputTrigger(final Handle handle, final String remoteJenkinsName, final String triggerId) {
        this.remoteBuildConfig = new RemoteBuildConfiguration();

        this.handle = handle;
        this.remoteJenkinsName = remoteJenkinsName;
        this.triggerId = triggerId;
    }

    /*public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws IOException {
        PluginLogger logger = PerfSigUIUtils.createLogger(listener.getLogger());
        JenkinsServerConnection serverConnection = ViewerUtils.createJenkinsServerConnection(jenkinsJob);

        JobWithDetails perfSigJob = serverConnection.getJenkinsJob().details();
        ViewerEnvInvisAction envInvisAction = run.getAction(ViewerEnvInvisAction.class);
        int buildNumber;
        if (envInvisAction != null) {
            buildNumber = envInvisAction.getCurrentBuild();
        } else {
            buildNumber = perfSigJob.getLastBuild().getNumber();
        }

        logger.log(Messages.ViewerInputTrigger_TriggerInputStep(perfSigJob.getName(), buildNumber));
        serverConnection.triggerInputStep(buildNumber, getTriggerId());
        logger.log(Messages.ViewerInputTrigger_TriggeredInputStep(perfSigJob.getName(), buildNumber));
    }*/

    public Handle getHandle() {
        return handle;
    }

    public String getRemoteJenkinsName() {
        return remoteJenkinsName;
    }

    public String getTriggerId() {
        return triggerId;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(context, remoteBuildConfig);
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {
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

        @Override
        public String getDisplayName() {
            return Messages.ViewerInputTrigger_DisplayName();
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return null;
        }

        @Override
        public String getFunctionName() {
            return "triggerInputStep";
        }
    }
    /*
    public void triggerInputStep(final int buildNumber, final String triggerId) {
        try {
            String url = getJenkinsJob().getUrl() + buildNumber + "/input/" + triggerId + "/proceedEmpty";
            getJenkinsJob().getClient().post(url, true);
            getJenkinsJob().getClient().get("url");
        } catch (IOException e) {
            throw new CommandExecutionException("error triggering input step: " + e.getMessage(), e);
        }
    }*/

    private class Execution extends SynchronousNonBlockingStepExecution<Void> {
        public Execution(StepContext context, RemoteBuildConfiguration remoteBuildConfig) {
            super(context);
        }


        @Override
        protected Void run() throws Exception {

            return null;
        }
    }
}
