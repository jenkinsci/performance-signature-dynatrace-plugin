package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.RelativePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.util.ListBoxModel;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
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
        public ListBoxModel doFillMetricIdItems(@AncestorInPath Item item,
                                                @RelativePath("..") @QueryParameter final String envId) {
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return new ListBoxModel();
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
