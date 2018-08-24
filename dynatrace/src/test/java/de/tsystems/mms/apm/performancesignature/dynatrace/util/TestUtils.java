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

package de.tsystems.mms.apm.performancesignature.dynatrace.util;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import de.tsystems.mms.apm.performancesignature.dynatrace.PerfSigGlobalConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CredProfilePair;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration.DescriptorImpl;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class TestUtils {

    private TestUtils() {
    }

    public static ListBoxModel prepareDTConfigurations() throws IOException {
        List<CredProfilePair> credProfilePairs = Collections.singletonList(new CredProfilePair("easy Travel", "myCreds"));
        List<DynatraceServerConfiguration> configurations = new ArrayList<>();
        configurations.add(new DynatraceServerConfiguration("PoC PerfSig",
                "https://192.168.192.202:8021", credProfilePairs, false, DescriptorImpl.defaultDelay,
                DescriptorImpl.defaultRetryCount, DescriptorImpl.defaultReadTimeout, false));
        configurations.add(new DynatraceServerConfiguration("TestMigration",
                "https://192.168.194.68:8021", credProfilePairs, false, DescriptorImpl.defaultDelay,
                DescriptorImpl.defaultRetryCount, DescriptorImpl.defaultReadTimeout, false));
        SystemCredentialsProvider.getInstance().getCredentials().add(new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
                "myCreds", null, "admin", "admin"));
        SystemCredentialsProvider.getInstance().save();

        PerfSigGlobalConfiguration.get().setConfigurations(configurations);
        Jenkins.getInstance().save();

        PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations()).stream().map(option -> option.name).forEach(System.out::println);

        ListBoxModel dynatraceConfigurations = PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
        assertTrue(containsOption(dynatraceConfigurations, "easy Travel (admin) @ PoC PerfSig"));
        assertTrue(containsOption(dynatraceConfigurations, "easy Travel (admin) @ TestMigration"));

        for (ListBoxModel.Option configuration : dynatraceConfigurations) {
            DTServerConnection connection = PerfSigUtils.createDTServerConnection(configuration.name, false);
            assumeTrue("assume that the server is reachable", validateConnection(connection));
        }


        return dynatraceConfigurations;
    }

    private static boolean validateConnection(DTServerConnection connection) {
        try {
            connection.getServerVersion();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean containsOption(ListBoxModel listBoxModel, String search) {
        return listBoxModel.stream().anyMatch(option -> option.name.equalsIgnoreCase(search));
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
