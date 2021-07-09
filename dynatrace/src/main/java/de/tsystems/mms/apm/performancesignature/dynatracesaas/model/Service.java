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

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.RelativePath;
import hudson.model.Item;
import hudson.util.ListBoxModel;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;

public class Service extends EntityId {

    @DataBoundConstructor
    public Service(final String entityId) {
        super(entityId);
    }

    @Extension
    public static final class EntityIdDescriptor extends EntityId.EntityIdDescriptor {
        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillEntityIdItems(@AncestorInPath Item item, @RelativePath("..") @QueryParameter final String envId) {
            ListBoxModel model = new ListBoxModel();
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return model;
            }
            DynatraceServerConfiguration serverConfiguration = DynatraceUtils.getServerConfiguration(envId);
            if (serverConfiguration != null) {
                DynatraceServerConnection connection = new DynatraceServerConnection(serverConfiguration);

                List<de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.Service> entityIds = connection.getServices();
                entityIds.stream()
                        .filter(entityId -> !entityId.getDisplayName().startsWith("OneAgent"))
                        .sorted(Comparator.comparing(de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.Service::getDisplayName))
                        .forEach(application -> model.add(application.getDisplayName(), application.getEntityId()));
                return model;
            }
            return model;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Service";
        }
    }
}
