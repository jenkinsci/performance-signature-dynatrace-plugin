package de.tsystems.mms.apm.performancesignature.dynatrace;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CredProfilePair;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockserver.integration.ClientAndServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpRequest.request;

public class Java11Test {
    @ClassRule
    public static final JenkinsRule j = new JenkinsRule();
    private static ListBoxModel dynatraceConfigurations;
    private static ClientAndServer mockServer;

    @BeforeClass
    public static void setUp() throws Exception {
        List<CredProfilePair> credProfilePairs = Collections.singletonList(new CredProfilePair("easy Travel", "myCreds"));
        List<DynatraceServerConfiguration> configurations = new ArrayList<>();
        configurations.add(new DynatraceServerConfiguration("Mock Server",
                "https://demo6880830.mockable.io", credProfilePairs, true, DynatraceServerConfiguration.DescriptorImpl.defaultDelay,
                DynatraceServerConfiguration.DescriptorImpl.defaultRetryCount, DynatraceServerConfiguration.DescriptorImpl.defaultReadTimeout, false));
        SystemCredentialsProvider.getInstance().getCredentials().add(new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
                "myCreds", null, "admin", "admin"));
        SystemCredentialsProvider.getInstance().save();

        PerfSigGlobalConfiguration.get().setConfigurations(configurations);
        Jenkins.getInstance().save();
        PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations()).stream().map(option -> option.name).forEach(System.out::println);
        dynatraceConfigurations = PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
    }

    @BeforeClass
    public static void startServer() {
        mockServer = startClientAndServer(8021);
        mockServer
                .when(
                        request().withSecure(true))
                .respond(
                        callback()
                                .withCallbackClass("de.tsystems.mms.apm.performancesignature.dynatrace.MockServerResponses")
                );
    }

    @AfterClass
    public static void stopServer() {
        mockServer.stop();
    }

    @Test
    public void testSessionRecording() throws IOException {
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(0).name);
        connection.getServerLicense();

        connection.startRecording("testSessionRecording", "triggered by UnitTest",
                PerfSigStartRecording.DescriptorImpl.defaultRecordingOption, false, false);
    }

    @Test
    public void testJenkinsConfiguration() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        String testCase = "loadtest";

        project.getBuildersList().add(new PerfSigStartRecording(dynatraceConfigurations.get(0).name, testCase));
        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        PerfSigEnvInvisAction invisAction = build.getAction(PerfSigEnvInvisAction.class);
        assertNotNull(invisAction);
        assertTrue(invisAction.getSessionName().matches("easy Travel_test0_Build-\\d+_" + testCase));
        assertEquals(invisAction.getTestCase(), testCase);
        assertFalse(invisAction.getTestRunId().isEmpty());
        assertNotNull(invisAction.getTimeframeStart());
    }
}
