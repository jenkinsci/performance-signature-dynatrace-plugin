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

package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.AggregationTypeEnum;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.TimeseriesDataPointQueryResult;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.TimeseriesDefinition;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.TestUtils;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import hudson.AbortException;
import hudson.model.Result;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.File;
import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.Assert.*;

public class TimeSeriesApiTest {

    @ClassRule
    public static final JenkinsRule j = new JenkinsRule();

    private static ListBoxModel dynatraceConfigurations;
    private DynatraceServerConnection connection;

    public TimeSeriesApiTest() throws AbortException, RESTErrorException {
        connection = DynatraceUtils.createDynatraceServerConnection(dynatraceConfigurations.get(0).name, false);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        dynatraceConfigurations = TestUtils.prepareDynatraceConfigurations();
    }

    @Test
    public void testTimeseriesApi() {
        TimeseriesDataPointQueryResult response = connection.getTimeseriesData("com.dynatrace.builtin:host.cpu.user",
                Instant.now().minus(2, HOURS).toEpochMilli(), Instant.now().toEpochMilli(), AggregationTypeEnum.AVG, null, null);
        assertNotNull(response);
    }

    @Test
    public void testJenkinsConfiguration() throws Exception {
        WorkflowJob p = j.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("node('master'){" +
                "recordDynatraceSession(envId: 'PoC PerfSig', testCase: 'loadtest') { sleep 60 }\n" +
                "perfSigDynatraceReports envId: 'PoC PerfSig', metrics: [[metricId: 'com.dynatrace.builtin:host.cpu.user']]}", true));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        j.assertLogContains("getting metric data from Dynatrace Server", b);
        //assertTrue(s.contains("getting PDF report: Singlereport")); //no Comparisonreport available
        //assertTrue(s.contains("session successfully downloaded"));

        PerfSigBuildAction buildAction = b.getAction(PerfSigBuildAction.class);
        assertNotNull(buildAction);
        assertNotNull(buildAction.getDashboardReports());
        DashboardReport dashboardReport = buildAction.getDashboardReports().get(0);
        assertNotNull(dashboardReport);
        assertNotNull(dashboardReport.getChartDashlets());
        assertEquals(1, dashboardReport.getChartDashlets().size());
    }

    @Test
    public void testFileConfiguration() throws Exception {
        String file = TestUtils.toPath(new File("src/test/resources/specfile.json"));

        WorkflowJob p = j.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("node('master'){" +
                "recordDynatraceSession(envId: 'PoC PerfSig', testCase: 'loadtest') { sleep 60 }\n" +
                "perfSigDynatraceReports envId: 'PoC PerfSig', specFile: '" + file + "'}", true));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        j.assertLogContains("getting metric data from Dynatrace Server", b);
        //assertTrue(s.contains("getting PDF report: Singlereport")); //no Comparisonreport available
        //assertTrue(s.contains("session successfully downloaded"));

        PerfSigBuildAction buildAction = b.getAction(PerfSigBuildAction.class);
        assertNotNull(buildAction);
        assertNotNull(buildAction.getDashboardReports());
        DashboardReport dashboardReport = buildAction.getDashboardReports().get(0);
        assertNotNull(dashboardReport);
        assertNotNull(dashboardReport.getChartDashlets());
        assertEquals(2, dashboardReport.getChartDashlets().size());
    }

    @Test
    public void testEmptyConfiguration() throws Exception {
        WorkflowJob p = j.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("node('master'){" +
                "perfSigDynatraceReports envId: '', metrics: null}", true));
        WorkflowRun b = j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0));
        j.assertLogContains("At least one of file or metrics needs to be provided", b);
    }

    @Test
    public void testGetDashboardViaRest() {
        List<TimeseriesDefinition> dashboardList = connection.getTimeseries();
        assertTrue(!dashboardList.isEmpty());
    }
}
