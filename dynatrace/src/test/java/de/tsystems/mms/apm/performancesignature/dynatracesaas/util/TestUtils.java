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

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.DynatraceGlobalConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.DynatraceApiTokenImpl;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import jenkins.model.Jenkins;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class TestUtils {
    private TestUtils() {
    }

    public static ListBoxModel prepareDynatraceConfigurations() throws IOException {

        List<DynatraceServerConfiguration> configurations = Collections.singletonList(new DynatraceServerConfiguration("PoC PerfSig",
                "https://192.168.122.138/e/b060b9c0-d824-468a-8c7c-9df3816c815a/", "myApiToken", false, false));
        SystemCredentialsProvider.getInstance().getCredentials().add(new DynatraceApiTokenImpl(CredentialsScope.GLOBAL,
                "myApiToken", null, Secret.fromString("Qxp1vNLPSqmb19BKmNwKa")));
        SystemCredentialsProvider.getInstance().save();

        DynatraceGlobalConfiguration.get().setConfigurations(configurations);
        Jenkins.get().save();

        DynatraceUtils.listToListBoxModel(DynatraceUtils.getDynatraceConfigurations()).stream().map(option -> option.name).forEach(System.out::println);

        ListBoxModel dynatraceConfigurations = DynatraceUtils.listToListBoxModel(DynatraceUtils.getDynatraceConfigurations());
        assertTrue(containsOption(dynatraceConfigurations, "PoC PerfSig"));

        for (ListBoxModel.Option configuration : dynatraceConfigurations) {
            DynatraceServerConnection connection = DynatraceUtils.createDynatraceServerConnection(configuration.name, false);
            assumeTrue("assume that the server is reachable", connection.validateConnection());
        }

        return dynatraceConfigurations;
    }

    public static boolean containsOption(ListBoxModel listBoxModel, String search) {
        return listBoxModel.stream().anyMatch(option -> option.name.equalsIgnoreCase(search));
    }

    /**
     * Convert a file to a platform agnostic representation.
     *
     * @param file
     * @return a file path operative system aware
     */
    public static String toPath(File file) {
        if (file == null) {
            return null;
        }

        return file.getAbsolutePath().replace("\\", "/");
    }
}
