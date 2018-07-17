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
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DynatraceReportStep extends Step {
    private final String envId;
    private List<Metric> metrics;

    @DataBoundConstructor
    public DynatraceReportStep(final String envId, final List<Metric> metrics) {
        this.envId = envId;
        this.metrics = metrics;
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

    @Extension
    public static final class DescriptorImpl extends StepDescriptor {
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "createPerfSigDynatraceReports";
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillEnvIdItems(@AncestorInPath Item item) {
            if (!item.hasPermission(Item.CONFIGURE) && item.hasPermission(Item.EXTENDED_READ)) {
                return new ListBoxModel();
            }
            return DynatraceUtils.listToListBoxModel(DynatraceUtils.getDynatraceConfigurations());
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.DynatraceRecorder_DisplayName();
        }
    }
}
