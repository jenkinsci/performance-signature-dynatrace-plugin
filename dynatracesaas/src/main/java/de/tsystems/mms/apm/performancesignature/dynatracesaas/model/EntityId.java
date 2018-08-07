package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

public class EntityId extends AbstractDescribableImpl<EntityId> {
    private final String entityId;

    @DataBoundConstructor
    public EntityId(final String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<EntityId> {
        @Nonnull
        @Override
        public String getDisplayName() {
            return "";
        }
    }
}
