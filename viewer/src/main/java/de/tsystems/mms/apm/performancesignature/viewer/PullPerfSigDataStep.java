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

import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.ParameterizedRemoteTrigger.pipeline.Handle;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Set;

public class PullPerfSigDataStep extends Step {
    private Handle handle;
    private int nonFunctionalFailure;
    private boolean ignorePerfSigData;

    @DataBoundConstructor
    public PullPerfSigDataStep() {
    }

    public Handle getHandle() {
        return handle;
    }

    @DataBoundSetter
    public void setHandle(final Handle handle) {
        this.handle = handle;
    }

    public boolean isIgnorePerfSigData() {
        return ignorePerfSigData;
    }

    @DataBoundSetter
    public void setIgnorePerfSigData(final boolean ignorePerfSigData) {
        this.ignorePerfSigData = ignorePerfSigData;
    }

    public int getNonFunctionalFailure() {
        return nonFunctionalFailure;
    }

    @DataBoundSetter
    public void setNonFunctionalFailure(final int nonFunctionalFailure) {
        this.nonFunctionalFailure = nonFunctionalFailure < 0 ? DescriptorImpl.defaultNonFunctionalFailure : nonFunctionalFailure;
    }

    @Override
    public StepExecution start(final StepContext context) {
        return new PullPerfSigDataStepExecution(this, context);
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {
        public static final int defaultNonFunctionalFailure = 0;
        public static final boolean defaultIgnorePerfSigData = false;

        @Override
        public String getFunctionName() {
            return "pullPerfSigReports";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.PullPerfSigDataStep_DisplayName();
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, FilePath.class, Launcher.class, TaskListener.class);
        }
    }
}
