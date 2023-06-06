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
import org.htmlunit.xml.XmlPage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.model.AbstractBuild;
import hudson.model.Project;
import hudson.model.Run;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static de.tsystems.mms.apm.performancesignature.ui.util.TestUtils.containsMeasure;
import static org.junit.Assert.*;

public class PerfSigBuildActionResultsDisplayTest {

    @Rule
    public final JenkinsRule j = new JenkinsRule();
    private final String TEST_PROJECT_WITH_HISTORY = "projectAction";

    @LocalData
    @Test
    public void testBuildActionCharts() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assertNotNull("We should have a project named " + TEST_PROJECT_WITH_HISTORY, proj);

        Run<?, ?> build = proj.getBuildByNumber(11157);
        assertNotNull("We should have a build with number 11157", build);
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.setJavaScriptEnabled(false);

        wc.getPage(proj);
        wc.getPage(build);
        HtmlPage page = wc.getPage(build, "performance-signature");
        j.assertAllImageLoadSuccessfully(page);
        assertEquals(11, page.getByXPath("//*[@id=\"UnitTest\"]/div/p/img").size());
        assertEquals(11, page.getByXPath("//*[@id=\"PerfTest\"]/div/p/img").size());
        List<?> list = page.getByXPath("//*[@id=\"PerfTest\"]/div/p/table/tbody/tr/td[1]/b/text()");
        assertTrue(containsMeasure(list, "Total GC Utilization (Average) (%)"));
        assertTrue(containsMeasure(list, "WebService Count (Count) (num)"));
    }

    @LocalData
    @Test
    public void testSummerizerGraph() throws SAXException, IOException {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assert proj != null;
        AbstractBuild<?, ?> build = proj.getBuildByNumber(11156);
        JenkinsRule.WebClient wc = j.createWebClient();

        PerfSigBuildAction action = build.getAction(PerfSigBuildAction.class);
        PerfSigBuildActionResultsDisplay buildActionResultsDisplay = new PerfSigBuildActionResultsDisplay(action);

        assertNotNull(buildActionResultsDisplay.getCurrentDashboardReports());
        assertEquals(PerfSigUIUtils.class, buildActionResultsDisplay.getPerfSigUIUtils());

        Page webRequestCount = wc.goTo(proj.getUrl() + "/lastBuild/performance-signature/" +
                "summarizerGraph?measure=Number%20of%20Requests&testcase=UnitTest&chartdashlet=WebRequestTime", "image/png");
        Page webRequestTime = wc.goTo(proj.getUrl() + "/lastBuild/performance-signature/" +
                "summarizerGraph?measure=Time&testcase=UnitTest&chartdashlet=WebRequestTime", "image/png");
        j.assertGoodStatus(webRequestCount);
        j.assertGoodStatus(webRequestTime);

        Page webRequestCount2 = wc.goTo(proj.getUrl() + "/lastBuild/performance-signature/" +
                "summarizerGraph?measure=Number%20of%20Requests&testcase=PerfTest&chartdashlet=WebRequestTime", "image/png");
        Page webRequestTime2 = wc.goTo(proj.getUrl() + "/lastBuild/performance-signature/" +
                "summarizerGraph?measure=Time&testcase=PerfTest&chartdashlet=WebRequestTime", "image/png");
        j.assertGoodStatus(webRequestCount2);
        j.assertGoodStatus(webRequestTime2);

        Assert.assertThrows(FailingHttpStatusCodeException.class, () ->
                wc.goTo(proj.getUrl() + "/lastBuild/performance-signature/" +
                        "summarizerGraph?measure=Time&testcase=TestNotFound&chartdashlet=WebRequestTime", "image/png"));
    }

    @LocalData
    @Test
    public void testPreviousDashboardReport() {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assert proj != null;
        Run<?, ?> build = proj.getBuildByNumber(11156);

        PerfSigBuildAction action = build.getAction(PerfSigBuildAction.class);
        PerfSigBuildActionResultsDisplay buildActionResultsDisplay = new PerfSigBuildActionResultsDisplay(action);

        DashboardReport dashboardReport = buildActionResultsDisplay.getDashBoardReport("PerfTest");
        assertNotNull(dashboardReport);
        assertNotNull(dashboardReport.getMeasure("GC Utilization", "Total GC Utilization"));
        assertNotNull(dashboardReport.getMeasure("WebServiceTime", "WebService Count"));

        DashboardReport previousDashboardReport = buildActionResultsDisplay.getPreviousDashboardReport("PerfTest");
        assertNotNull(previousDashboardReport);
        assertNotNull(previousDashboardReport.getMeasure("GC Utilization", "Total GC Utilization"));
        assertNotNull(previousDashboardReport.getMeasure("WebServiceTime", "WebService Count"));

        Run<?, ?> previousBuild = proj.getBuildByNumber(11155);
        PerfSigBuildAction previousAction = previousBuild.getAction(PerfSigBuildAction.class);
        PerfSigBuildActionResultsDisplay previousBuildActionResultsDisplay = new PerfSigBuildActionResultsDisplay(previousAction);
        assertNotNull(previousBuildActionResultsDisplay);
        assertNull(previousBuildActionResultsDisplay.getPreviousDashboardReport("PerfTest"));
    }

    @LocalData
    @Test
    public void testXMLApi() throws IOException, SAXException {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        JenkinsRule.WebClient wc = j.createWebClient();

        assert proj != null;
        XmlPage xmlProjectPage = wc.goToXml(proj.getUrl() + "/lastBuild/performance-signature/api/xml?depth=10");
        j.assertXPath(xmlProjectPage, "/perfSigBuildActionResultsDisplay");
        j.assertXPath(xmlProjectPage, "/perfSigBuildActionResultsDisplay/dashboardReport");
        j.assertXPath(xmlProjectPage, "/perfSigBuildActionResultsDisplay/dashboardReport/chartDashlet");
        j.assertXPathValue(xmlProjectPage, "/perfSigBuildActionResultsDisplay/dashboardReport/chartDashlet/measure/measure/text()", "Number of Requests");
        j.assertXPathValue(xmlProjectPage, "/perfSigBuildActionResultsDisplay/dashboardReport/chartDashlet/measure/count", "1485");
        assertEquals(14.0, xmlProjectPage.getFirstByXPath("count(/perfSigBuildActionResultsDisplay/dashboardReport/chartDashlet)"), 0);

        Run<?, ?> build = proj.getBuildByNumber(11157);
        Assert.assertThrows(FailingHttpStatusCodeException.class, () ->
                wc.goTo(build.getUrl() + "/performance-signature/" +
                        "getSingleReport?testCase=nothing&number=0", "application/octet-stream"));
    }

    @LocalData
    @Test
    public void testDownloadMethods() throws Exception {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assert proj != null;
        Run<?, ?> build = proj.getBuildByNumber(11157);

        JenkinsRule.WebClient wc = j.createWebClient();
        wc.setJavaScriptEnabled(false);

        Page singleReportDownload = wc.goTo(build.getUrl() + "/performance-signature/" +
                "getSingleReport?testCase=UnitTest&number=0", "application/octet-stream");
        Page sessionDownload = wc.goTo(build.getUrl() + "/performance-signature/" +
                "getSession?testCase=PerfTest", "application/octet-stream");
        j.assertGoodStatus(singleReportDownload);
        j.assertGoodStatus(sessionDownload);

        Assert.assertThrows(FailingHttpStatusCodeException.class, () ->
                wc.goTo(build.getUrl() + "/performance-signature/" +
                        "getSingleReport?testCase=UnitTest&number=1", "application/octet-stream"));
    }

    @LocalData
    @Test
    public void testGetReportList() throws IOException, SAXException {
        Project proj = (Project) j.jenkins.getItem(TEST_PROJECT_WITH_HISTORY);
        assert proj != null;
        Run<?, ?> build = proj.getBuildByNumber(11157);

        JenkinsRule.WebClient wc = j.createWebClient();
        HtmlPage buildPage = wc.getPage(build);
        Gson gson = new Gson();

        for (String type : Arrays.asList("Single", "Comparison")) {
            URL url = new URL(buildPage.getUrl() + "performance-signature/get" + type + "ReportList");
            List<String> obj = gson.fromJson(IOUtils.toString(url, StandardCharsets.UTF_8), new TypeToken<List<String>>() {
            }.getType());
            assertFalse(obj.isEmpty());
            assertEquals(2, obj.size());
        }
    }
}
