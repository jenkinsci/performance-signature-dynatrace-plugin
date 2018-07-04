package de.tsystems.mms.apm.performancesignature.dynatracesaas.model;

import com.cloudbees.plugins.credentials.CredentialsNameProvider;
import com.cloudbees.plugins.credentials.NameWith;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import hudson.Util;
import hudson.util.Secret;

import javax.annotation.Nonnull;

@NameWith(DynatraceApiToken.NameProvider.class)
public interface DynatraceApiToken extends StandardCredentials {

    Secret getApiToken();

    class NameProvider extends CredentialsNameProvider<DynatraceApiToken> {
        @Override
        public String getName(@Nonnull DynatraceApiToken c) {
            String description = Util.fixEmptyAndTrim(c.getDescription());
            return Messages.DynatraceApiToken_name() + (description != null ? " (" + description + ")" : "");
        }
    }
}
