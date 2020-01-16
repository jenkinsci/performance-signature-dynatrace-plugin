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

package de.tsystems.mms.apm.performancesignature.dynatrace;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.ConfigurationTestCase;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.Dashboard;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.GenericTestCase;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.ui.model.ClientLinkGenerator;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import hudson.util.ListBoxModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.nio.charset.Charset;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils.getOptions;
import static org.junit.Assert.*;

public class RecorderOneTest {

    @ClassRule
    public static final JenkinsRule j = new JenkinsRule();
    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(
            getOptions().usingFilesUnderDirectory(options().filesRoot().child("RecorderOneTest").getPath())
    );

    private static ListBoxModel dynatraceConfigurations;

    @BeforeClass
    public static void setUp() throws Exception {
        dynatraceConfigurations = TestUtils.prepareDTConfigurations();
    }

    @Test
    public void testJenkinsConfiguration() throws Exception {
        String testCase = "RecorderOneTest";

        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new PerfSigStartRecording(dynatraceConfigurations.get(0).name, testCase));
        //wait some time to get some data into the session
        if (SystemUtils.IS_OS_WINDOWS) {
            project.getBuildersList().add(new BatchFile("ping -n 10 127.0.0.1 > NUL"));
        } else {
            project.getBuildersList().add(new Shell("sleep 10"));
        }
        project.getBuildersList().add(new PerfSigStopRecording(dynatraceConfigurations.get(0).name));
        ConfigurationTestCase configurationTestCase = new GenericTestCase(testCase,
                Collections.singletonList(new Dashboard("PerformanceSignature_singlereport")),
                Collections.singletonList(new Dashboard("PerformanceSignature_comparisonreport")),
                "PerformanceSignature_xml");
        configurationTestCase.setClientDashboard(ClientLinkGenerator.PUREPATH_OVERVIEW);

        PerfSigRecorder recorder = new PerfSigRecorder(dynatraceConfigurations.get(0).name, Collections.singletonList(configurationTestCase));
        recorder.setExportSessions(true);
        recorder.setDeleteSessions(true);
        project.getPublishersList().add(recorder);
        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        String s = FileUtils.readFileToString(build.getLogFile(), Charset.defaultCharset());
        assertTrue(s.contains("connection successful, getting reports for this build and testcase " + testCase));
        assertTrue(s.contains("getting PDF report: Singlereport")); //no Comparisonreport available
        assertTrue(s.contains("parsing XML report"));
        assertTrue(s.contains("session successfully downloaded"));

        PerfSigBuildAction buildAction = build.getAction(PerfSigBuildAction.class);
        assertNotNull(buildAction);
        assertNotNull(buildAction.getDashboardReports());
        DashboardReport dashboardReport = buildAction.getDashboardReports().get(0);
        assertNotNull(dashboardReport);
        assertNotNull(dashboardReport.getChartDashlets());
        assertEquals(7, dashboardReport.getChartDashlets().size());
    }

    @Test
    public void testPipelineConfiguration() throws Exception {
        String testCase = "RecorderOneTest";

        WorkflowJob p = j.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("node('master') {" +
                "startSession dynatraceProfile: 'easy Travel (admin) @ PoC PerfSig', testCase: 'RecorderOneTest'\n" +
                "sleep 10\n" +
                "stopSession 'easy Travel (admin) @ PoC PerfSig'\n" +
                "perfSigReports configurationTestCases: [[$class: 'GenericTestCase', clientDashboard: 'PurePath Overview'," +
                "name: 'RecorderOneTest', xmlDashboard: 'PerformanceSignature_xml']]," +
                "dynatraceProfile: 'easy Travel (fn_perfsig) @ PoC PerfSig', deleteSessions: true, exportSessions: false, removeConfidentialStrings: true\n" +
                "}", true));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));

        j.assertLogContains("connection successful, getting reports for this build and testcase " + testCase, b);
        j.assertLogContains("parsing XML report", b);

        PerfSigBuildAction buildAction = b.getAction(PerfSigBuildAction.class);
        assertNotNull(buildAction);
        assertNotNull(buildAction.getDashboardReports());
        DashboardReport dashboardReport = buildAction.getDashboardReports().get(0);
        assertNotNull(dashboardReport);
        assertNotNull(dashboardReport.getChartDashlets());
        assertEquals(7, dashboardReport.getChartDashlets().size());
    }
}
