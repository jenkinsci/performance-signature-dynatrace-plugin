package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;


public class CustomProperty extends AbstractDescribableImpl<CustomProperty> {
    private final String key;
    private final String value;

    @DataBoundConstructor
    public CustomProperty(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public DecriptorImpl getDescriptor() {
        return (DecriptorImpl) Jenkins.get().getDescriptorOrDie(getClass());
    }

    @Extension
    public static class DecriptorImpl extends Descriptor<CustomProperty> {

        public static DescriptorExtensionList<CustomProperty, Descriptor<CustomProperty>> all() {
            return Jenkins.get().getDescriptorList(CustomProperty.class);
        }
    }
}
