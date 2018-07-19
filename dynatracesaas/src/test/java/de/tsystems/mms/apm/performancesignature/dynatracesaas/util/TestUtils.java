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

    public static ListBoxModel prepareDTConfigurations() throws IOException {

        List<DynatraceServerConfiguration> configurations = Collections.singletonList(new DynatraceServerConfiguration("PoC PerfSig",
                "https://192.168.122.138/e/15b1674c-ac9f-4e61-97e4-2b3df7d81f90/", "myApiToken", false, false));
        SystemCredentialsProvider.getInstance().getCredentials().add(new DynatraceApiTokenImpl(CredentialsScope.GLOBAL,
                "myApiToken", null, Secret.fromString("1TVZ_pc4S_WeY3h5cUXzd")));
        SystemCredentialsProvider.getInstance().save();

        DynatraceGlobalConfiguration.get().setConfigurations(configurations);
        Jenkins.getInstance().save();

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
