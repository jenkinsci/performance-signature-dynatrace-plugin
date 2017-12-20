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

package de.tsystems.mms.apm.performancesignature.dynatrace;

import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.SessionData;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils;
import hudson.AbortException;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import hudson.util.ListBoxModel;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class StopRecordingTest {

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();
    private static ListBoxModel dynatraceConfigurations;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private final String testCase = "unittest";
    private DTServerConnection connection;

    @BeforeClass
    public static void setUp() throws Exception {
        dynatraceConfigurations = TestUtils.prepareDTConfigurations();
    }

    public StopRecordingTest() throws AbortException, RESTErrorException {
        this.connection = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(0).name);
    }

    @Test
    public void testStopSessionRecording1() throws IOException {
        assertNull(connection.stopRecording());
    }

    @Test
    public void testStopSessionRecording2() throws IOException {
        String result = connection.startRecording("testSessionRecording", "triggered by UnitTest",
                PerfSigStartRecording.DescriptorImpl.defaultRecordingOption, false, true);

        String result2 = connection.stopRecording();

        assertEquals(result, result2);
        assertTrue(result.contains("easy Travel"));
    }

    @Test
    public void testStopSessionRecording3() throws AbortException, RESTErrorException {
        DTServerConnection connection2 = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(1).name);

        exception.expect(CommandExecutionException.class);
        exception.expectMessage("pre-production licenses");
        connection2.stopRecording();
    }

    @Test
    public void testJenkinsConfiguration() throws Exception {
        PerfSigEnvInvisAction invisAction = createTestProject(0);

        assertTrue(invisAction != null);
        assertTrue(invisAction.getSessionName().matches("easy Travel_test0_Build-\\d+_unittest.*"));
        assertTrue(invisAction.getTestCase().equals(testCase));
        assertFalse(invisAction.getTestRunId().isEmpty());
        assertTrue(invisAction.getTimeframeStart() != null);
    }

    @Test
    public void testJenkinsConfiguration2() throws Exception {
        PerfSigEnvInvisAction invisAction = createTestProject(1);

        assertTrue(invisAction != null);
        assertTrue(invisAction.getSessionName().matches("easy Travel_test0_Build-\\d+_unittest.*"));
        assertTrue(invisAction.getTestCase().equals(testCase));
        assertNull(invisAction.getTestRunId());
        assertTrue(invisAction.getTimeframeStart() != null);
    }

    private PerfSigEnvInvisAction createTestProject(int id) throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new PerfSigStartRecording(dynatraceConfigurations.get(id).name, testCase));
        //wait some time to get some data into the session
        if (TestUtils.isWindows()) {
            project.getBuildersList().add(new BatchFile("ping -n 10 127.0.0.1 > NUL"));
        } else {
            project.getBuildersList().add(new Shell("sleep 10"));
        }
        project.getBuildersList().add(new PerfSigStopRecording(dynatraceConfigurations.get(id).name));
        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        PerfSigEnvInvisAction invisAction = build.getAction(PerfSigEnvInvisAction.class);
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(id).name);
        assertTrue(containsSession(connection.getSessions().getSessions(), invisAction.getSessionId()));

        return invisAction;
    }

    private boolean containsSession(List<SessionData> sessions, String sessionId) {
        for (SessionData sessionData : sessions) {
            if (sessionData.getId().equalsIgnoreCase(sessionId)) {
                return true;
            }
        }
        return false;
    }

}
