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
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.CommandExecutionException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils;
import hudson.AbortException;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.util.ListBoxModel;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;

import static org.junit.Assert.*;

public class StartRecordingTest {

    @ClassRule
    public static final JenkinsRule j = new JenkinsRule();
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private static ListBoxModel dynatraceConfigurations;
    private DTServerConnection connection;

    @BeforeClass
    public static void setUp() throws Exception {
        dynatraceConfigurations = TestUtils.prepareDTConfigurations();
    }

    public StartRecordingTest() throws AbortException, RESTErrorException {
        this.connection = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(0).name);
    }

    @Test
    public void testSessionRecording() {
        String result = connection.startRecording("testContinuousSessionRecording", "triggered by UnitTest",
                PerfSigStartRecording.DescriptorImpl.defaultRecordingOption, false, false);
        assertNotNull(result);
        connection.stopRecording();
    }

    @Test
    public void testSessionRecording2() throws IOException {
        DTServerConnection connection2 = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(1).name);

        exception.expect(CommandExecutionException.class);
        exception.expectMessage("pre-production licenses");
        connection2.startRecording("testContinuousSessionRecording", "triggered by UnitTest",
                PerfSigStartRecording.DescriptorImpl.defaultRecordingOption, false, false);
    }

    @Test
    public void testJenkinsConfiguration() throws Exception {
        final String testCase = "unittest";

        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new PerfSigStartRecording(dynatraceConfigurations.get(0).name, testCase));
        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        PerfSigEnvInvisAction invisAction = build.getAction(PerfSigEnvInvisAction.class);

        assertNotNull(invisAction);
        assertTrue(invisAction.getSessionName().matches("easy Travel_test0_Build-\\d+_unittest"));
        assertEquals(invisAction.getTestCase(), testCase);
        assertFalse(invisAction.getTestRunId().isEmpty());
        assertNotNull(invisAction.getTimeframeStart());
    }

    @Test
    public void testJenkinsConfiguration2() throws Exception {
        final String testCase = "unittest";

        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new PerfSigStartRecording(dynatraceConfigurations.get(1).name, testCase));
        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        PerfSigEnvInvisAction invisAction = build.getAction(PerfSigEnvInvisAction.class);

        assertNotNull(invisAction);
        assertNull(invisAction.getSessionId());
        assertEquals(invisAction.getTestCase(), testCase);
        assertNull(invisAction.getTestRunId());
        assertNotNull(invisAction.getTimeframeStart());
    }
}
