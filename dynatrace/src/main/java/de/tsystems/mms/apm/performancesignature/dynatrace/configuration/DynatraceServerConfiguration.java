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

import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.util.FormValidation;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.CheckForNull;
import java.util.List;

public class DynatraceServerConfiguration extends AbstractDescribableImpl<DynatraceServerConfiguration> {
    private final String name;
    private String serverUrl;
    private final int readTimeout;
    private final boolean verifyCertificate;
    private final boolean useProxy;
    private final int delay;
    private final int retryCount;
    private final List<CredProfilePair> credProfilePairs;

    @DataBoundConstructor
    public DynatraceServerConfiguration(final String name, final String serverUrl, final List<CredProfilePair> credProfilePairs,
                                        final boolean verifyCertificate, final int delay, final int retryCount, final int readTimeout,
                                        final boolean useProxy) {
        this.name = name;
        this.serverUrl = serverUrl;
        this.credProfilePairs = credProfilePairs;
        this.verifyCertificate = verifyCertificate;
        this.useProxy = useProxy;
        this.delay = delay;
        this.retryCount = retryCount;
        this.readTimeout = readTimeout;
    }

    public String getName() {
        return name;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    @CheckForNull
    public CredProfilePair getCredProfilePair(final String profile) {
        String systemProfile = profile.replaceAll("\\(.*", "").trim();
        return credProfilePairs.stream().filter(pair -> pair.getProfile().equals(systemProfile)).findFirst().orElse(null);
    }

    public List<CredProfilePair> getCredProfilePairs() {
        return credProfilePairs;
    }

    public boolean isVerifyCertificate() {
        return verifyCertificate;
    }

    public int getDelay() {
        return delay;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<DynatraceServerConfiguration> {
        public static final String defaultServerUrl = "https://dynatrace.server:8021";
        public static final int defaultDelay = 10;
        public static final int defaultRetryCount = 5;
        public static final int defaultReadTimeout = 300;
        public static final boolean defaultVerifyCertificate = false;
        public static final boolean defaultUseProxy = false;

        @Override
        public String getDisplayName() {
            return "";
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doCheckServerUrl(@AncestorInPath Item item, @QueryParameter final String serverUrl) {
            FormValidation validationResult = FormValidation.ok();
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return validationResult;
            }

            if (PerfSigUIUtils.checkNotNullOrEmpty(serverUrl) && (serverUrl.charAt(serverUrl.length() - 1) != '/')) {
                return validationResult;
            } else {
                return FormValidation.error(Messages.PerfSigRecorder_DTServerUrlNotValid());
            }
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doCheckDelay(@AncestorInPath Item item, @QueryParameter final String delay) {
            FormValidation validationResult = FormValidation.ok();
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return validationResult;
            }

            if (PerfSigUIUtils.checkNotEmptyAndIsNumber(delay)) {
                return validationResult;
            } else {
                return FormValidation.error(Messages.PerfSigRecorder_DelayNotValid());
            }
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doCheckRetryCount(@AncestorInPath Item item, @QueryParameter final String retryCount) {
            FormValidation validationResult = FormValidation.ok();
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return validationResult;
            }

            if (PerfSigUIUtils.checkNotEmptyAndIsNumber(retryCount)) {
                return validationResult;
            } else {
                return FormValidation.error(Messages.PerfSigRecorder_RetryCountNotValid());
            }
        }
    }
}
