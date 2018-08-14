package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import hudson.DescriptorExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

import java.io.Serializable;

public abstract class EntityId implements Describable<EntityId>, Serializable {
    private final String entityId;

    public EntityId(final String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }

    public EntityIdDescriptor getDescriptor() {
        return (EntityIdDescriptor) Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    public abstract static class EntityIdDescriptor extends Descriptor<EntityId> {

        public static DescriptorExtensionList<EntityId, Descriptor<EntityId>> all() {
            return Jenkins.getInstance().getDescriptorList(EntityId.class);
        }
    }
}
