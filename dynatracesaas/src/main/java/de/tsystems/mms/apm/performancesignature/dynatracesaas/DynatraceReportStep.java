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

package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import com.google.common.collect.ImmutableSet;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.Metric;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynatraceReportStep extends Step {
    private final String envId;
    private List<Metric> metrics;
    private String specFile;

    @DataBoundConstructor
    public DynatraceReportStep(final String envId) {
        this.envId = envId;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new DynatraceReportStepExecution(this, context);
    }

    public String getEnvId() {
        return envId;
    }

    public List<Metric> getMetrics() {
        if (metrics == null) {
            metrics = new ArrayList<>();
        }
        return metrics;
    }

    @DataBoundSetter
    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public String getSpecFile() {
        return specFile;
    }

    @DataBoundSetter
    public void setSpecFile(String specFile) {
        this.specFile = specFile;
    }

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "perfSigDynatraceReports";
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillEnvIdItems(@AncestorInPath Item item) {
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return new ListBoxModel();
            }
            return DynatraceUtils.listToListBoxModel(DynatraceUtils.getDynatraceConfigurations());
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.DynatraceRecorder_DisplayName();
        }

        @Override
        public DynatraceReportStep newInstance(Map<String, Object> arguments) throws Exception {
            DynatraceReportStep step = (DynatraceReportStep) super.newInstance(arguments);
            if (StringUtils.isBlank(step.getSpecFile()) && CollectionUtils.isEmpty(step.getMetrics())) {
                throw new IllegalArgumentException("At least one of file or metrics needs to be provided to " + getFunctionName());
            }
            return step;
        }
    }
}
