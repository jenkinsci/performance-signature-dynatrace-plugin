package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import hudson.Extension;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

public final class DynatraceApiTokenImpl extends BaseStandardCredentials implements DynatraceApiToken {

    private Secret apiToken;

    @DataBoundConstructor
    public DynatraceApiTokenImpl(CredentialsScope scope, String id, String description, Secret apiToken) {
        super(scope, id, description);
        this.apiToken = apiToken;
    }

    @Override
    public Secret getApiToken() {
        return apiToken;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.DynatraceApiToken_name();
        }
    }
}
