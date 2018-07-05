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

package de.tsystems.mms.apm.performancesignature.dynatracesaas.util;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.DynatraceGlobalConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.Messages;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.DynatraceApiToken;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Timeseries;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.AbortException;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import java.util.ArrayList;
import java.util.List;

import static com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials;

public final class DynatraceUtils {
    private DynatraceUtils() {
    }

    public static ListBoxModel listToListBoxModel(final List<?> list) {
        final ListBoxModel listBoxModel = new ListBoxModel();
        if (list == null || list.isEmpty()) return listBoxModel;

        Class clazz = list.get(0).getClass();
        if (clazz == String.class) {
            list.stream()
                    .map(String.class::cast)
                    .forEach(listBoxModel::add);
        } else if (clazz == DynatraceServerConfiguration.class) {
            list.stream()
                    .map(DynatraceServerConfiguration.class::cast)
                    .forEach(conf -> listBoxModel.add(conf.getName()));
        } else if (clazz == Timeseries.class) {
            list.stream()
                    .map(Timeseries.class::cast)
                    .forEach(metric -> listBoxModel.add(metric.getDetailedSource() + " - " + metric.getDisplayName(), metric.getTimeseriesId()));
        }
        return listBoxModel;
    }

    public static String getApiToken(final String apiTokenId) {
        StandardCredentials credentials = CredentialsMatchers.firstOrNull(
                lookupCredentials(StandardCredentials.class, (Item) null, ACL.SYSTEM, new ArrayList<>()),
                CredentialsMatchers.withId(apiTokenId));
        if (credentials != null) {
            if (credentials instanceof DynatraceApiToken) {
                return ((DynatraceApiToken) credentials).getApiToken().getPlainText();
            }
            if (credentials instanceof StringCredentials) {
                return ((StringCredentials) credentials).getSecret().getPlainText();
            }
        }
        throw new IllegalStateException("No credentials found for credentialsId: " + apiTokenId);
    }

    public static List<DynatraceServerConfiguration> getDynatraceConfigurations() {
        return DynatraceGlobalConfiguration.get().getConfigurations();
    }

    public static DynatraceServerConfiguration getServerConfiguration(final String dynatraceServer) {
        for (DynatraceServerConfiguration serverConfiguration : getDynatraceConfigurations()) {
            if (dynatraceServer.equals(serverConfiguration.getName())) {
                return serverConfiguration;
            }
        }
        return null;
    }

    public static DynatraceServerConnection createJenkinsServerConnection(final String dynatraceServer) throws AbortException, RESTErrorException {
        DynatraceServerConfiguration serverConfiguration = DynatraceUtils.getServerConfiguration(dynatraceServer);
        if (serverConfiguration == null) {
            throw new AbortException(Messages.DynatraceRecorder_FailedToLookupServer());
        }

        DynatraceServerConnection serverConnection = new DynatraceServerConnection(serverConfiguration);
        if (!serverConnection.validateConnection()) {
            throw new RESTErrorException(Messages.DynatraceRecorder_ConnectionError());
        }
        return serverConnection;
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
}
