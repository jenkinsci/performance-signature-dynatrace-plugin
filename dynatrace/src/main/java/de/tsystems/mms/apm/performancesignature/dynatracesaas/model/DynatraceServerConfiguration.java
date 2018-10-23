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

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.util.Collections;

public class DynatraceServerConfiguration extends AbstractDescribableImpl<DynatraceServerConfiguration> {
    private final String name;
    private final String apiTokenId;
    private final boolean verifyCertificate;
    private final boolean useProxy;
    private final String serverUrl;

    @DataBoundConstructor
    public DynatraceServerConfiguration(final String name, final String serverUrl, final String apiTokenId,
                                        final boolean verifyCertificate, final boolean useProxy) {
        this.name = name;
        this.serverUrl = serverUrl;
        this.apiTokenId = apiTokenId;
        this.verifyCertificate = verifyCertificate;
        this.useProxy = useProxy;
    }

    public String getName() {
        return name;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getApiTokenId() {
        return apiTokenId;
    }

    public boolean isVerifyCertificate() {
        return verifyCertificate;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<DynatraceServerConfiguration> {
        public static final String defaultServerUrl = "https://dynatrace.instance/e/1234-5678-9012-3456";
        public static final boolean defaultVerifyCertificate = false;
        public static final boolean defaultUseProxy = false;

        @Override
        public String getDisplayName() {
            return "";
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillApiTokenIdItems(@AncestorInPath Item item, @QueryParameter String credentialsId) {
            if (checkMissingPermission(item)) {
                return new StandardListBoxModel().includeCurrentValue(credentialsId);
            }

            return new StandardListBoxModel()
                    .includeEmptyValue()
                    .includeMatchingAs(
                            ACL.SYSTEM,
                            Jenkins.getInstance(),
                            StandardCredentials.class,
                            Collections.emptyList(),
                            CredentialsMatchers.anyOf(
                                    CredentialsMatchers.instanceOf(DynatraceApiToken.class),
                                    CredentialsMatchers.instanceOf(StringCredentials.class)))
                    .includeCurrentValue(credentialsId);
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doCheckServerUrl(@AncestorInPath Item item, @QueryParameter final String serverUrl) {
            FormValidation validationResult = FormValidation.ok();
            if (checkMissingPermission(item)) {
                return validationResult;
            }
            if (PerfSigUIUtils.checkNotNullOrEmpty(serverUrl)) {
                return validationResult;
            } else {
                return FormValidation.error(Messages.DynatraceServerConfiguration_ServerNotValid());
            }
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doTestServerConnection(@AncestorInPath Item item,
                                                     @QueryParameter final String serverUrl, @QueryParameter final String apiTokenId,
                                                     @QueryParameter final boolean verifyCertificate, @QueryParameter final boolean useProxy) {
            if (checkMissingPermission(item)) {
                return FormValidation.ok();
            }

            final DynatraceServerConnection connection = new DynatraceServerConnection(serverUrl, apiTokenId, verifyCertificate, useProxy);

            try {
                connection.getServerVersion();
                return FormValidation.ok(Messages.CredJobPair_TestConnectionSuccessful());
            } catch (Exception e) {
                return FormValidation.error(e, Messages.CredJobPair_TestConnectionNotSuccessful());
            }
        }

        private boolean checkMissingPermission(final Item item) {
            return item == null ? !Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER) :
                    !item.hasPermission(Item.EXTENDED_READ) && !item.hasPermission(CredentialsProvider.USE_ITEM);
        }
    }
}
