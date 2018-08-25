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


public class InputTriggerStepExecution extends SynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 1L;
    private transient final InputTriggerStep step;

    InputTriggerStepExecution(final InputTriggerStep step, final StepContext context) throws AbortException {
        super(context);
        if (step.getHandle() == null) {
            throw new AbortException("'handle' has not been defined for this 'triggerInputStep' step");
        } else if (step.getTriggerId() == null) {
            throw new AbortException("'triggerId' has not been defined for this 'triggerInputStep' step");
        }
        this.step = step;
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

        int buildNumber = step.getHandle().getBuildNumber();
        logger.log(Messages.InputTriggerStep_TriggerInputStep(step.getHandle().getJobName(), buildNumber));
        triggerInputStep(context, step.getHandle(), step.getTriggerId());
        logger.log(Messages.InputTriggerStep_TriggeredInputStep(step.getHandle().getJobName(), buildNumber));
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
