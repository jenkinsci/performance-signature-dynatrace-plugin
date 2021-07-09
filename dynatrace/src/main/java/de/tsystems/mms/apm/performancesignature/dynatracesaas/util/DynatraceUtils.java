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

package de.tsystems.mms.apm.performancesignature.dynatracesaas.util;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.DynatraceGlobalConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.Messages;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.DynatraceApiToken;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.TagInfo;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.TagMatchRule;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.env1.model.TimeseriesDefinition;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.AbortException;
import hudson.model.TaskListener;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import java.util.Collections;
import java.util.List;

import static com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials;

public final class DynatraceUtils {
    private DynatraceUtils() {
    }

    public static ListBoxModel listToListBoxModel(final List<?> list) {
        final ListBoxModel listBoxModel = new ListBoxModel();

        list.forEach(item -> {
            if (item instanceof String) {
                listBoxModel.add((String) item);
            } else if (item instanceof DynatraceServerConfiguration) {
                listBoxModel.add(((DynatraceServerConfiguration) item).getName());
            } else if (item instanceof TagMatchRule.MeTypesEnum) {
                listBoxModel.add(((TagMatchRule.MeTypesEnum) item).getValue());
            } else if (item instanceof TagInfo.ContextEnum) {
                listBoxModel.add(((TagInfo.ContextEnum) item).getValue());
            } else if (item instanceof TimeseriesDefinition) {
                TimeseriesDefinition metric = (TimeseriesDefinition) item;
                listBoxModel.add(metric.getDetailedSource() + " - " + metric.getDisplayName(), metric.getTimeseriesId());
            }
        });
        return listBoxModel;
    }

    public static String getApiToken(final String apiTokenId) {
        StandardCredentials credentials = CredentialsMatchers.firstOrNull(
                lookupCredentials(StandardCredentials.class, Jenkins.get(), ACL.SYSTEM, Collections.emptyList()),
                CredentialsMatchers.withId(apiTokenId));
        if (credentials != null) {
            if (credentials instanceof DynatraceApiToken) {
                return ((DynatraceApiToken) credentials).getApiToken().getPlainText();
            } else if (credentials instanceof StringCredentials) {
                return ((StringCredentials) credentials).getSecret().getPlainText();
            }
        }
        throw new IllegalStateException("No credentials found for credentialsId: " + apiTokenId);
    }

    public static List<DynatraceServerConfiguration> getDynatraceConfigurations() {
        return DynatraceGlobalConfiguration.get().getConfigurations();
    }

    public static DynatraceServerConfiguration getServerConfiguration(final String dynatraceServer) {
        return getDynatraceConfigurations().stream()
                .filter(serverConfiguration -> dynatraceServer.equals(serverConfiguration.getName()))
                .findFirst().orElse(null);
    }

    public static DynatraceServerConnection createDynatraceServerConnection(final String dynatraceServer, final boolean validateConnection)
            throws AbortException, RESTErrorException {
        DynatraceServerConfiguration serverConfiguration = DynatraceUtils.getServerConfiguration(dynatraceServer);
        if (serverConfiguration == null) {
            throw new AbortException(Messages.DynatraceRecorder_FailedToLookupServer());
        }

        DynatraceServerConnection connection = new DynatraceServerConnection(serverConfiguration);
        if (validateConnection) {
            try {
                connection.getServerVersion();
            } catch (Exception e) {
                throw new RESTErrorException(Messages.DynatraceRecorder_ConnectionError(), e);
            }
        }
        return connection;
    }

    /**
     * Escape the given string to be used as URL query value.
     *
     * @param str String to be escaped
     * @return Escaped string
     */
    public static String escapeString(String str) {
        return PerfSigUIUtils.encodeString(str);
    }


    public static TaskListener getTaskListener(final StepContext context) {
        if (!context.isReady()) {
            return null;
        }
        try {
            return context.get(TaskListener.class);
        } catch (Exception x) {
            return null;
        }
    }
}
