package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Timeseries;
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

import java.util.List;

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
        dynatraceConfigurations = TestUtils.prepareDTConfigurations();
    }

    @Test
    public void testJenkinsConfiguration() throws Exception {
        WorkflowJob p = j.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("node('master'){" +
                "createPerfSigDynatraceReports envId: 'PoC PerfSig', metrics: [[metricId: 'com.dynatrace.builtin:host.cpu.user']]}", true));
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
    public void testEmptyConfiguration() throws Exception {
        WorkflowJob p = j.createProject(WorkflowJob.class);
        p.setDefinition(new CpsFlowDefinition("node('master'){" +
                "createPerfSigDynatraceReports envId: '', metrics: null}", true));
        WorkflowRun b = j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0));
        j.assertLogContains("failed to lookup Dynatrace server configuration", b);
    }

    @Test
    public void testGetDashboardViaRest() {
        List<Timeseries> dashboardList = connection.getTimeseries();
        assertTrue(!dashboardList.isEmpty());
    }

    @Test
    public void testServerVersionViaRest() throws Exception {
        DynatraceServerConnection connection = DynatraceUtils.createDynatraceServerConnection(dynatraceConfigurations.get(0).name, false);
        assertNotNull(connection.getServerVersion());
    }
}
