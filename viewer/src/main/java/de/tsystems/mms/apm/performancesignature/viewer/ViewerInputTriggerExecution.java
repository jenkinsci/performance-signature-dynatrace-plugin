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

import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.viewer.rest.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.viewer.rest.ConnectionHelper;
import hudson.AbortException;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.BuildContext;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.RemoteJenkinsServer;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.pipeline.Handle;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import java.io.IOException;
import java.net.URL;


public class ViewerInputTriggerExecution extends SynchronousNonBlockingStepExecution<Void> {
    private final Handle handle;
    private final String triggerId;

    public ViewerInputTriggerExecution(final StepContext context, final Handle handle, final String triggerId) {
        super(context);
        this.handle = handle;
        this.triggerId = triggerId;
    }

    public Handle getHandle() {
        return handle;
    }

    public String getTriggerId() {
        return triggerId;
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

        int buildNumber = handle.getBuildNumber();
        logger.log(Messages.ViewerInputTrigger_TriggerInputStep(handle.getJobName(), buildNumber));
        triggerInputStep(context, handle, getTriggerId());
        logger.log(Messages.ViewerInputTrigger_TriggeredInputStep(handle.getJobName(), buildNumber));
        return null;
    }

    private void triggerInputStep(final BuildContext context, final Handle handle, final String triggerId) {
        try {
            URL url = new URL(handle.getJobUrl() + "/input/" + triggerId + "/proceedEmpty");
            ConnectionHelper connectionHelper = new ConnectionHelper(handle);
            connectionHelper.postToUrl(url, context);
        } catch (IOException e) {
            throw new CommandExecutionException("error triggering input step: " + e.getMessage(), e);
        }
    }
}
