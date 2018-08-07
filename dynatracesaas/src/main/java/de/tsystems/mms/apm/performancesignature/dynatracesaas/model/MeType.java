package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.util.ListBoxModel;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class MeType extends AbstractDescribableImpl<MeType> {
    private final String meType;

    @DataBoundConstructor
    public MeType(final String meType) {
        this.meType = meType;
    }

    public String getMeType() {
        return meType;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<MeType> {
        @Nonnull
        @Override
        public String getDisplayName() {
            return "";
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillMeTypeItems(@AncestorInPath Item item) {
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return new ListBoxModel();
            }
            return DynatraceUtils.listToListBoxModel(Arrays.asList(TagMatchRule.MeTypesEnum.values()));
        }
    }
}