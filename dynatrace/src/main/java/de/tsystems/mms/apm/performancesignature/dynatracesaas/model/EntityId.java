/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
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
        return (EntityIdDescriptor) Jenkins.get().getDescriptorOrDie(getClass());
    }

    public abstract static class EntityIdDescriptor extends Descriptor<EntityId> {

        public static DescriptorExtensionList<EntityId, Descriptor<EntityId>> all() {
            return Jenkins.get().getDescriptorList(EntityId.class);
        }
    }
}
