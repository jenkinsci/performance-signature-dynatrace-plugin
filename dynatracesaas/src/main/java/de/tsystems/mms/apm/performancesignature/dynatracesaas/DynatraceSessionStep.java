package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.google.common.collect.ImmutableSet;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import hudson.Extension;
import hudson.model.TaskListener;
import hudson.security.Permission;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.util.Set;

public class DynatraceSessionStep extends Step {
    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new DynatraceSessionStepExecution(this, stepContext);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }


    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "recordDynatraceSession";
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "record Dynatrace Saas/Managed session";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(TaskListener.class);
        }

        @Nonnull
        @Restricted(NoExternalUse.class) // Only for UI calls
        public ListBoxModel doFillDynatraceProfileItems(@QueryParameter final String dynatraceProfile) {
            if (!Jenkins.getInstance().hasPermission(Permission.CONFIGURE)) {
                return new StandardListBoxModel().includeCurrentValue(dynatraceProfile);
            }
            return DynatraceUtils.listToListBoxModel(DynatraceUtils.getDynatraceConfigurations());
        }
    }
}
