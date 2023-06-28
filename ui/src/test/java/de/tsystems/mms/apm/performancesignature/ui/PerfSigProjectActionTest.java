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

package de.tsystems.mms.apm.performancesignature.ui;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.html.HtmlPage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.ui.model.JSONDashlet;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.TestUtils;
import hudson.model.Project;
import hudson.model.Run;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;

import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PerfSigProjectActionTest {

    @Rule
    public final JenkinsRule j = new JenkinsRule();
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private final String TEST_PROJECT_WITH_HISTORY = "projectAction";

    @LocalData
    @Test
    public void testProjectActionChartsFloatingBox() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        JenkinsRule.WebClient wc = j.createWebClient();

        HtmlPage projectPage = wc.getPage(proj);
        j.assertAllImageLoadSuccessfully(projectPage);
        assertEquals(2, projectPage.getByXPath("//*[@id=\"tabList\"]/li/a").size()); //no AJAX available :(

        PerfSigProjectAction projectAction = new PerfSigProjectAction(proj);
        List<JSONDashlet> configuration = new Gson().fromJson(projectAction.getDashboardConfiguration("PerfTest"), new TypeToken<List<JSONDashlet>>() {
        }.getType());
        assertEquals(11, configuration.size());
        assertTrue(containsDashlet(configuration, "Database - DB Count (Count)"));
        assertTrue(containsDashlet(configuration, "WebServiceTime - Time (Average)"));
    }

    @LocalData
    @Test
    public void testProjectActionDataTable() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assertNotNull("We should have a project named " + TEST_PROJECT_WITH_HISTORY, proj);

        JenkinsRule.WebClient wc = j.createWebClient();
        wc.setJavaScriptEnabled(false);
        HtmlPage projectPage = wc.getPage(proj, "performance-signature");

        j.assertAllImageLoadSuccessfully(projectPage);
        List<?> list = projectPage.getByXPath("//*[@id=\"PerfTest\"]//table/thead/tr/th/text()[1]");
        assertTrue(TestUtils.containsMeasure(list, "GC Utilization - Total GC Utilization (Average)"));
        assertTrue(TestUtils.containsMeasure(list, "WebServiceTime - WebService Count (Count)"));
        j.assertXPath(projectPage, "//*[@id=\"UnitTest\"]/div/table/tbody/tr/td[2]/a/*[name()='svg']"); //PDF symbol should be visible
        Run<?, ?> build = proj.getBuildByNumber(10147);
        Page comparisonReportDownload = wc.goTo(build.getUrl() + "/performance-signature/" +
                "getComparisonReport?testCase=UnitTest&number=0", "application/octet-stream");
        j.assertGoodStatus(comparisonReportDownload);
    }

    @LocalData
    @Test
    public void testSummerizerGraph() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assertNotNull("We should have a project named " + TEST_PROJECT_WITH_HISTORY, proj);

        PerfSigProjectAction projectAction = new PerfSigProjectAction(proj);
        List<JSONDashlet> configuration = new Gson().fromJson(projectAction.getDashboardConfiguration("PerfTest"), new TypeToken<List<JSONDashlet>>() {
        }.getType());

        JenkinsRule.WebClient wc = j.createWebClient();
        for (JSONDashlet dashlet : configuration) {
            System.out.println(dashlet.generateDashletName() + " : " + dashlet.getId());
            Page graph = wc.goTo(proj.getUrl() + "performance-signature/summarizerGraph?id=" + dashlet.getId(), "image/png");
            j.assertGoodStatus(graph);
        }

        Assert.assertThrows(FailingHttpStatusCodeException.class, () ->
                wc.goTo(proj.getUrl() + "performance-signature/summarizerGraph?id=20571aabda401cc01546d7ebd62e0e58", ""));
    }

    @LocalData
    @Test
    public void testWebMethods() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.setJavaScriptEnabled(false);

        assert proj != null;
        HtmlPage projectPage = wc.getPage(proj, "performance-signature");

        PerfSigProjectAction projectAction = new PerfSigProjectAction(proj);
        assertEquals(PerfSigUIUtils.class, projectAction.getPerfSigUIUtils());

        j.assertXPathValue(projectPage, "//*[@id=\"measureGroup\"]/option[5]", "Synthetic WebRequest Time");

        Map<String, String> jsonDashletIDs = projectAction.getAvailableMeasures("PerfTest", "Errors");
        assertTrue(jsonDashletIDs.containsKey("f1cb5c773c9c5cf98d81827513ad9e46"));
        assertEquals("Failed Transaction Count", jsonDashletIDs.get("f1cb5c773c9c5cf98d81827513ad9e46"));

        jsonDashletIDs.putAll(projectAction.getAvailableMeasures("UnitTest", "WebRequestTime"));
        for (Map.Entry<String, String> id : jsonDashletIDs.entrySet()) {
            Page graph = wc.goTo(proj.getUrl() + "performance-signature/summarizerGraph?id=" + id.getKey() + "&customName=" + id.getValue(), "image/png");
            j.assertGoodStatus(graph);
        }

        assertEquals("Count", projectAction.getAggregationFromMeasure("PerfTest", "Database", "DB Count"));
        assertEquals("Average", projectAction.getAggregationFromMeasure("PerfTest", "Synthetic WebRequest Time", "Synthetic Web Requests by Timer Name - PurePath Response Time"));
        assertEquals("", projectAction.getAggregationFromMeasure("PerfTest", "Synthetic WebRequest Time", "empty"));
    }

    @LocalData
    @Test
    public void testGridConfiguration() {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);

        PerfSigProjectAction projectAction = new PerfSigProjectAction(proj);
        String json = projectAction.getDashboardConfiguration("PerfTest");

        assertNotNull(json);
        List<JSONDashlet> jsonDashletList = new Gson().fromJson(json, new TypeToken<List<JSONDashlet>>() {
        }.getType());
        assertEquals(11, jsonDashletList.size());

        projectAction.setDashboardConfiguration("PerfTest", json);
        assertEquals(11, jsonDashletList.size());
    }

    private boolean containsDashlet(List<JSONDashlet> list, String search) {
        return list.stream().anyMatch(jsonDashlet -> jsonDashlet.generateDashletName().equals(search));
    }
}
