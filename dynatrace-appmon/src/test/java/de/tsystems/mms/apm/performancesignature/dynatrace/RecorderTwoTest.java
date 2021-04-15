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
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.GenericTestCase;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiClient;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.Agent;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils;
import hudson.AbortException;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.util.ListBoxModel;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import static de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils.getOptions;
import static org.junit.Assert.*;

public class RecorderTwoTest {

    @ClassRule
    public static final JenkinsRule j = new JenkinsRule();
    @ClassRule
    public static WireMockClassRule wireMockRule = new WireMockClassRule(getOptions());

    private static ListBoxModel dynatraceConfigurations;
    private final DTServerConnection connection;

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

        String s = String.join("", build.getLog(1000));
        assertTrue(s.contains("failed to lookup Dynatrace server configuration"));
    }

    @Test
    public void testConfigurationValidation() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        ConfigurationTestCase configurationTestCase = new GenericTestCase("", null, null, "");

        project.getPublishersList().add(new PerfSigRecorder(dynatraceConfigurations.get(0).name, Collections.singletonList(configurationTestCase)));
        FreeStyleBuild build = j.assertBuildStatus(Result.FAILURE, project.scheduleBuild2(0));

        String s = String.join("", build.getLog(1000));
        assertTrue(s.contains("TestCase can not be validated"));
    }

    @Test
    public void testMissingEnvInvisAction() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        ConfigurationTestCase configurationTestCase = new GenericTestCase("Loadtest", null, null,
                "PerformanceSignature_xml");

        project.getPublishersList().add(new PerfSigRecorder(dynatraceConfigurations.get(0).name, Collections.singletonList(configurationTestCase)));
        FreeStyleBuild build = j.assertBuildStatus(Result.FAILURE, project.scheduleBuild2(0));

        String s = String.join("", build.getLog(1000));
        assertTrue(s.contains("no sessionname found, aborting ..."));
    }

    @Test
    public void testGetDashboardViaRest() {
        List<de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.Dashboard> dashboardList = connection.getDashboards();
        assertFalse(dashboardList.isEmpty());
    }

    @Test
    public void testGetAgentsViaRest() {
        List<Agent> agentList = connection.getAllAgents();
        assertFalse(agentList.isEmpty());
    }

    @Test
    public void testHotSensorPlacementViaRest() {
        for (Agent agent : connection.getAgents()) {
            if (agent.getName().startsWith("CustomerFrontend")) {
                assertTrue(connection.hotSensorPlacement(agent.getAgentId()));
            }
        }
    }

    @Test
    public void testIncidentsViaRest() throws Exception {
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceConfigurations.get(0).name);
        SimpleDateFormat df = new SimpleDateFormat(ApiClient.REST_DF);
        List<Alert> alerts = connection.getIncidents(df.parse("2020-01-14T02:34:24.464+01:00"), df.parse("2020-01-14T14:34:24.464+01:00"));

        assertNotNull(alerts);
    }
}
