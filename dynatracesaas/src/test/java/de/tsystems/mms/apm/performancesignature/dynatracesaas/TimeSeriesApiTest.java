package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.Metric;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Timeseries;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.TestUtils;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import hudson.AbortException;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.util.ListBoxModel;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
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
        String testCase = "RecorderTest";

        FreeStyleProject project = j.createFreeStyleProject();
        /*project.getBuildersList().add(new PerfSigStartRecording(dynatraceConfigurations.get(0).name, testCase));
        //wait some time to get some data into the session
        if (TestUtils.isWindows()) {
            project.getBuildersList().add(new BatchFile("ping -n 30 127.0.0.1 > NUL"));
        } else {
            project.getBuildersList().add(new Shell("sleep 30"));
        }
        project.getBuildersList().add(new PerfSigStopRecording(dynatraceConfigurations.get(0).name));*/
        List<Metric> metrics = new ArrayList<>();
        metrics.add(new Metric("com.dynatrace.builtin:host.cpu.user"));

        DynatraceRecorder recorder = new DynatraceRecorder(dynatraceConfigurations.get(0).name, metrics);
        project.getPublishersList().add(recorder);
        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        String s = FileUtils.readFileToString(build.getLogFile());
        System.out.println(s);
        assertTrue(s.contains("getting metric data from Dynatrace Server"));
        //assertTrue(s.contains("getting PDF report: Singlereport")); //no Comparisonreport available
        //assertTrue(s.contains("session successfully downloaded"));

        PerfSigBuildAction buildAction = build.getAction(PerfSigBuildAction.class);
        assertNotNull(buildAction);
        assertNotNull(buildAction.getDashboardReports());
        DashboardReport dashboardReport = buildAction.getDashboardReports().get(0);
        assertNotNull(dashboardReport);
        assertNotNull(dashboardReport.getChartDashlets());
        assertEquals(1, dashboardReport.getChartDashlets().size());
    }

    @Test
    public void testEmptyConfiguration() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getPublishersList().add(new DynatraceRecorder("", null));

        FreeStyleBuild build = j.assertBuildStatus(Result.FAILURE, project.scheduleBuild2(0));

        String s = FileUtils.readFileToString(build.getLogFile());
        assertTrue(s.contains("failed to lookup Dynatrace server configuration"));
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
