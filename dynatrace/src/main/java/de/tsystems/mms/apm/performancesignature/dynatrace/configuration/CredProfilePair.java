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
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
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
        @Override
        public String getDisplayName() {
            return "";
        }

        @Restricted(NoExternalUse.class)
        @Nonnull
        public ListBoxModel doFillCredentialsIdItems(@QueryParameter String credentialsId) {
            if (!Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
                return new StandardListBoxModel().includeCurrentValue(credentialsId);
            }
            return new StandardUsernameListBoxModel()
                    .includeEmptyValue()
                    .includeMatchingAs(
                            ACL.SYSTEM,
                            Jenkins.getInstance(),
                            StandardUsernamePasswordCredentials.class,
                            Collections.emptyList(),
                            CredentialsMatchers.always())
                    .includeCurrentValue(credentialsId);
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doCheckCredentialsId(@QueryParameter String value) {
            if (!Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {
                return FormValidation.ok();
            }
            for (ListBoxModel.Option o : CredentialsProvider.listCredentials(StandardUsernamePasswordCredentials.class,
                    Jenkins.getInstance(),
                    ACL.SYSTEM,
                    Collections.emptyList(),
                    CredentialsMatchers.always())) {
                if (StringUtils.equals(value, o.value)) {
                    return FormValidation.ok();
                }
            }
            return FormValidation.error("The selected credentials cannot be found");
        }

        @Restricted(NoExternalUse.class)
        @Nonnull
        public ListBoxModel doFillProfileItems(@RelativePath("..") @QueryParameter final String serverUrl, @QueryParameter final String credentialsId,
                                               @RelativePath("..") @QueryParameter final boolean verifyCertificate, @RelativePath("..") @QueryParameter final boolean useProxy) {

            if (StringUtils.isBlank(serverUrl) || StringUtils.isBlank(credentialsId)) {
                return new StandardListBoxModel().includeEmptyValue();
            }
            try {
                CredProfilePair pair = new CredProfilePair("", credentialsId);
                final DTServerConnection connection = new DTServerConnection(serverUrl, pair, verifyCertificate, 0, useProxy);
                return PerfSigUtils.listToListBoxModel(connection.getSystemProfiles().getSystemprofiles());
            } catch (CommandExecutionException ex) {
                return new StandardListBoxModel().includeEmptyValue();
            }
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doCheckProfile(@QueryParameter final String profile) {
            FormValidation validationResult;
            if (PerfSigUIUtils.checkNotNullOrEmpty(profile)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigRecorder_DTProfileNotValid());
            }
            return validationResult;
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doTestDynaTraceConnection(@QueryParameter final String serverUrl, @QueryParameter final String credentialsId,
                                                        @QueryParameter final boolean verifyCertificate, @QueryParameter final boolean useProxy) {

            CredProfilePair pair = new CredProfilePair("", credentialsId);
            final DTServerConnection connection = new DTServerConnection(serverUrl, pair, verifyCertificate, 0, useProxy);

            if (connection.validateConnection()) {
                return FormValidation.ok(Messages.PerfSigRecorder_TestConnectionSuccessful());
            } else {
                return FormValidation.warning(Messages.PerfSigRecorder_TestConnectionNotSuccessful());
            }
        }
    }
}
