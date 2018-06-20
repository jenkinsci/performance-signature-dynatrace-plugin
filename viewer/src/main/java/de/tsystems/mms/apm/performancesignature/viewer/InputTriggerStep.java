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

import com.google.common.collect.ImmutableSet;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.pipeline.Handle;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.util.Set;

public class InputTriggerStep extends Step {
    private Handle handle;
    private final String triggerId;

    @DataBoundConstructor
    public InputTriggerStep(final String triggerId) {
        this.triggerId = triggerId;
    }

    public Handle getHandle() {
        return handle;
    }

    @DataBoundSetter
    public void setHandle(final Handle handle) {
        this.handle = handle;
    }

    public String getTriggerId() {
        return triggerId;
    }

    @Override
    public StepExecution start(final StepContext context) throws Exception {
        return new InputTriggerStepExecution(this, context);
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {
        @Restricted(NoExternalUse.class)
        public FormValidation doCheckTriggerId(@QueryParameter("triggerId") final String value) {
            if (PerfSigUIUtils.checkNotNullOrEmpty(value)) {
                return FormValidation.ok();
            } else {
                return FormValidation.error("empty triggerId");
            }
        }

        @Override
        public String getDisplayName() {
            return Messages.InputTriggerStep_DisplayName();
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, FilePath.class, Launcher.class, TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "triggerInputStep";
        }
    }
}
