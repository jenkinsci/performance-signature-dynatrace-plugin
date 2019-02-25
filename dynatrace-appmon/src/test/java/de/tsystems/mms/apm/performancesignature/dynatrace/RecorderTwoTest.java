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

import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.ConfigurationTestCase;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.GenericTestCase;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.Agent;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RecorderTwoTest {

    @ClassRule
    public static final JenkinsRule j = new JenkinsRule();
    private static ListBoxModel dynatraceConfigurations;
    private DTServerConnection connection;

    public RecorderTwoTest() throws AbortException, RESTErrorException {
        connection = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(0).name);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        dynatraceConfigurations = TestUtils.prepareDTConfigurations();
    }

    @Test
    public void testEmptyConfiguration() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getPublishersList().add(new PerfSigRecorder("", null));

        FreeStyleBuild build = j.assertBuildStatus(Result.FAILURE, project.scheduleBuild2(0));

        String s = FileUtils.readFileToString(build.getLogFile());
        assertTrue(s.contains("failed to lookup Dynatrace server configuration"));
    }

    @Test
    public void testConfigurationValidation() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        ConfigurationTestCase configurationTestCase = new GenericTestCase("", null, null, "");

        project.getPublishersList().add(new PerfSigRecorder(dynatraceConfigurations.get(0).name, Collections.singletonList(configurationTestCase)));
        FreeStyleBuild build = j.assertBuildStatus(Result.FAILURE, project.scheduleBuild2(0));

        String s = FileUtils.readFileToString(build.getLogFile());
        assertTrue(s.contains("TestCase can not be validated"));
    }

    @Test
    public void testMissingEnvInvisAction() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        ConfigurationTestCase configurationTestCase = new GenericTestCase("Loadtest", null, null,
                "PerformanceSignature_xml");

        project.getPublishersList().add(new PerfSigRecorder(dynatraceConfigurations.get(0).name, Collections.singletonList(configurationTestCase)));
        FreeStyleBuild build = j.assertBuildStatus(Result.FAILURE, project.scheduleBuild2(0));

        String s = FileUtils.readFileToString(build.getLogFile());
        assertTrue(s.contains("no sessionname found, aborting ..."));
    }

    @Test
    public void testGetDashboardViaRest() {
        List<de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.Dashboard> dashboardList = connection.getDashboards();
        assertTrue(!dashboardList.isEmpty());
    }

    @Test
    public void testGetAgentsViaRest() {
        List<Agent> agentList = connection.getAllAgents();
        assertTrue(!agentList.isEmpty());
    }

    @Test
    public void testHotSensorPlacementViaRest() {
        for (Agent agent : connection.getAgents()) {
            if ("java".equalsIgnoreCase(agent.getTechnologyType())) {
                assertTrue(connection.hotSensorPlacement(agent.getAgentId()));
            }
        }
    }

    @Test
    public void testIncidentsViaRest() throws Exception {
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(0).name);
        Date now = new Date();
        now.setTime(now.getTime() - 43200000L);
        List<Alert> alerts = connection.getIncidents(now, new Date());

        assertNotNull(alerts);
    }

    /*@Test
    public void testXMLFile() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();

        FreeStyleBuild build = j.assertBuildStatus(Result.SUCCESS, project.scheduleBuild2(0));
        File file = new File("src/test/resources/test.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(DashboardReport.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        DashboardReport dashboardReport = (DashboardReport) jaxbUnmarshaller.unmarshal(file);
        dashboardReport.setName("BA_test");

        PerfSigBuildAction action = new PerfSigBuildAction(Collections.singletonList(dashboardReport));
        build.addAction(action);
        Thread.sleep(2000000000);
    }*/
}
