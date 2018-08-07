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

package de.tsystems.mms.apm.performancesignature.dynatrace.configuration;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.RelativePath;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.util.Collections;

public class CredProfilePair extends AbstractDescribableImpl<CredProfilePair> {
    private final String profile;
    private final String credentialsId;

    @DataBoundConstructor
    public CredProfilePair(final String profile, final String credentialsId) {
        this.profile = profile;
        this.credentialsId = credentialsId;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    public UsernamePasswordCredentials getCredentials() {
        return PerfSigUtils.getCredentials(credentialsId);
    }

    public String getProfile() {
        return profile;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<CredProfilePair> {
        @Nonnull
        @Override
        public String getDisplayName() {
            return "";
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item item, @QueryParameter String credentialsId) {
            if (checkMissingPermission(item)) {
                return new StandardListBoxModel().includeCurrentValue(credentialsId);
            }

            return new StandardUsernameListBoxModel()
                    .includeEmptyValue()
                    .includeMatchingAs(
                            ACL.SYSTEM,
                            Jenkins.getInstance(),
                            StandardUsernamePasswordCredentials.class,
                            Collections.emptyList(),
                            CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class))
                    .includeCurrentValue(credentialsId);
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillProfileItems(@AncestorInPath Item item,
                                               @RelativePath("..") @QueryParameter final String serverUrl,
                                               @QueryParameter final String credentialsId,
                                               @RelativePath("..") @QueryParameter final boolean verifyCertificate,
                                               @RelativePath("..") @QueryParameter final boolean useProxy) {
            ListBoxModel result = new ListBoxModel();
            if (checkMissingPermission(item)) {
                return result;
            }
            if (StringUtils.isBlank(serverUrl) || StringUtils.isBlank(credentialsId)) {
                return result;
            }
            try {
                CredProfilePair pair = new CredProfilePair("", credentialsId);
                final DTServerConnection connection = new DTServerConnection(serverUrl, pair, verifyCertificate, 0, useProxy);
                return PerfSigUtils.listToListBoxModel(connection.getSystemProfiles().getSystemprofiles());
            } catch (CommandExecutionException ex) {
                return result;
            }
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doCheckProfile(@AncestorInPath Item item, @QueryParameter final String profile) {
            FormValidation validationResult = FormValidation.ok();
            if (checkMissingPermission(item)) {
                return validationResult;
            }

            if (PerfSigUIUtils.checkNotNullOrEmpty(profile)) {
                return validationResult;
            } else {
                return FormValidation.error(Messages.PerfSigRecorder_DTProfileNotValid());
            }
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doTestDynaTraceConnection(@AncestorInPath Item item,
                                                        @QueryParameter final String serverUrl, @QueryParameter final String credentialsId,
                                                        @QueryParameter final boolean verifyCertificate, @QueryParameter final boolean useProxy) {
            if (checkMissingPermission(item)) {
                return FormValidation.ok();
            }

            CredProfilePair pair = new CredProfilePair("", credentialsId);
            final DTServerConnection connection = new DTServerConnection(serverUrl, pair, verifyCertificate, 0, useProxy);

            try {
                connection.getServerVersion();
                return FormValidation.ok(Messages.PerfSigRecorder_TestConnectionSuccessful());
            } catch (Exception e) {
                return FormValidation.error(e, Messages.PerfSigRecorder_TestConnectionNotSuccessful());
            }
        }

        private boolean checkMissingPermission(final Item item) {
            return item == null ? !Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER) :
                    !item.hasPermission(Item.EXTENDED_READ) && !item.hasPermission(CredentialsProvider.USE_ITEM);
        }
    }
}
