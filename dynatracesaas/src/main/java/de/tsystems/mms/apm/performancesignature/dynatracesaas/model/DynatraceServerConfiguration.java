/*
 * Copyright (c) 2014 T-Systems Multimedia Solutions GmbH
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
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.util.Collections;

public class DynatraceServerConfiguration extends AbstractDescribableImpl<DynatraceServerConfiguration> {
    private final String name;
    private final String apiTokenId;
    private final boolean verifyCertificate;
    private final CustomProxy customProxy;
    private final String serverUrl;

    @DataBoundConstructor
    public DynatraceServerConfiguration(final String name, final String serverUrl, final String apiTokenId,
                                        final boolean verifyCertificate, final boolean proxy, final int proxySource, final String proxyServer,
                                        final int proxyPort, final String proxyUser, final String proxyPassword) {
        this.name = name;
        this.serverUrl = serverUrl;
        this.apiTokenId = apiTokenId;
        this.verifyCertificate = verifyCertificate;

        this.customProxy = proxy ? new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, proxySource) : null;
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

    public CustomProxy getCustomProxy() {
        return customProxy;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<DynatraceServerConfiguration> {
        public static final String defaultServerUrl = "https://myjenkins.com/e/1234-5678-9012-3456";
        public static final boolean defaultVerifyCertificate = false;

        @Override
        public String getDisplayName() {
            return "";
        }

        public ListBoxModel doFillApiTokenIdItems(@QueryParameter String name, @QueryParameter String url) {
            if (Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
                return new StandardListBoxModel()
                        .includeEmptyValue()
                        .includeMatchingAs(ACL.SYSTEM,
                                Jenkins.getInstance(),
                                StandardCredentials.class,
                                Collections.emptyList(),
                                CredentialsMatchers.always());
            }
            //ToDo: fill me
            return new StandardListBoxModel();
        }

        public FormValidation doCheckApiTokenId(@QueryParameter String value) {
            if (StringUtils.isBlank(value)) {
                return FormValidation.error("API Token for Dynatrace access required");
            } else {
                return FormValidation.ok();
            }
        }

        public FormValidation doTestServerConnection(@QueryParameter final String serverUrl, @QueryParameter final String apiTokenId,
                                                     @QueryParameter boolean verifyCertificate,
                                                     @QueryParameter final boolean proxy, @QueryParameter final String proxyServer,
                                                     @QueryParameter final int proxyPort, @QueryParameter final String proxyUser,
                                                     @QueryParameter final String proxyPassword) {

            CustomProxy customProxyServer = null;
            if (proxy) {
                customProxyServer = new CustomProxy(proxyServer, proxyPort, proxyUser, proxyPassword, StringUtils.isBlank(proxyServer));
            }
            final DynatraceServerConnection connection = new DynatraceServerConnection(serverUrl, apiTokenId, verifyCertificate, customProxyServer);

            if (connection.validateConnection()) {
                return FormValidation.ok(Messages.CredJobPair_TestConnectionSuccessful());
            } else {
                return FormValidation.error(Messages.CredJobPair_TestConnectionNotSuccessful());
            }
        }

        public FormValidation doCheckServerUrl(@QueryParameter final String serverUrl) {
            if (PerfSigUIUtils.checkNotNullOrEmpty(serverUrl)) {
                return FormValidation.ok();
            } else {
                return FormValidation.error(Messages.DynatraceServerConfiguration_ServerNotValid());
            }
        }
    }
}
