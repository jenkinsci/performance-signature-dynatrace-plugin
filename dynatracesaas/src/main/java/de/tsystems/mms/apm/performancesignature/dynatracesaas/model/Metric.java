package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import hudson.Extension;
import hudson.RelativePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.security.Permission;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;

public class Metric extends AbstractDescribableImpl<Metric> {
    private final String metricId;

    @DataBoundConstructor
    public Metric(final String metricId) {
        this.metricId = metricId;
    }

    public String getMetricId() {
        return metricId;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Metric> {
        @Nonnull
        @Override
        public String getDisplayName() {
            return "";
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillMetricIdItems(@RelativePath("..") @QueryParameter final String envId,
                                                @QueryParameter final String metricId) {
            if (!Jenkins.getInstance().hasPermission(Permission.CONFIGURE)) {
                return new StandardListBoxModel().includeCurrentValue(metricId);
            }

            DynatraceServerConfiguration serverConfiguration = DynatraceUtils.getServerConfiguration(envId);
            if (serverConfiguration != null) {
                DynatraceServerConnection connection = new DynatraceServerConnection(serverConfiguration);
                return DynatraceUtils.listToListBoxModel(connection.getTimeseries());
            }
            return new ListBoxModel();
        }
    }
}
