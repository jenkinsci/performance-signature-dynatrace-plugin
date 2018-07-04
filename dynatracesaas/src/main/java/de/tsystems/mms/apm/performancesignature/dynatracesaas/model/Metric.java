package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import hudson.Extension;
import hudson.RelativePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

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
        @Override
        public String getDisplayName() {
            return "";
        }

        public ListBoxModel doFillMetricIdItems(@RelativePath("..") @QueryParameter final String envId) {
            DynatraceServerConfiguration serverConfiguration = DynatraceUtils.getServerConfiguration(envId);
            if (serverConfiguration != null) {
                DynatraceServerConnection connection = new DynatraceServerConnection(serverConfiguration);
                return DynatraceUtils.listToListBoxModel(connection.getTimeseries());
            }
            return null;
        }
    }
}
