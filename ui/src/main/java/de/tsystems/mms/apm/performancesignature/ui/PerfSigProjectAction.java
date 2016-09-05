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

package de.tsystems.mms.apm.performancesignature.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.model.JSONDashlet;
import de.tsystems.mms.apm.performancesignature.model.MeasureNameHelper;
import de.tsystems.mms.apm.performancesignature.model.PerfSigTestDataWrapper;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUIUtils;
import hudson.FilePath;
import hudson.model.Job;
import hudson.model.ProminentProjectAction;
import hudson.model.Run;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.TestResultProjectAction;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PerfSigProjectAction extends PerfSigBaseAction implements ProminentProjectAction {
    private final static String JSON_FILENAME = "gridconfig.json";
    private final Job<?, ?> job;
    private Map<String, JSONDashlet> jsonDashletMap;

    public PerfSigProjectAction(final Job<?, ?> job) {
        this.job = job;
        this.jsonDashletMap = new ConcurrentHashMap<String, JSONDashlet>();
    }

    @Override
    protected String getTitle() {
        return job.getDisplayName() + " PerfSig";
    }

    public Job<?, ?> getJob() {
        return job;
    }

    public TestResultProjectAction getTestResultProjectAction() {
        return job.getAction(TestResultProjectAction.class);
    }

    public Class getPerfSigUIUtils() {
        return PerfSigUIUtils.class;
    }

    public void doSummarizerGraph(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        if (ChartUtil.awtProblemCause != null) {
            // not available. send out error message
            response.sendRedirect2(request.getContextPath() + "/images/headless.png");
            return;
        }

        String id = request.getParameter("id");

        if (request.getParameter("customName") == null && request.getParameter("customBuildCount") == null) { //dashlet from stored configuration
            JSONDashlet jsonDashlet = getJsonDashletMap().get(id);
            ChartUtil.generateGraph(request, response, createChart(jsonDashlet, buildDataSet(jsonDashlet)), PerfSigUIUtils.calcDefaultSize());
        } else { //new dashlet
            JSONDashlet jsonDashlet = createJSONConfiguration().get(id);
            ChartUtil.generateGraph(request, response, createChart(jsonDashlet, buildDataSet(jsonDashlet)), PerfSigUIUtils.calcDefaultSize());
        }
    }

    private CategoryDataset buildDataSet(final JSONDashlet jsonDashlet) throws IOException {
        String dashboard = jsonDashlet.getDashboard();
        String chartDashlet = jsonDashlet.getChartDashlet();
        String measure = jsonDashlet.getMeasure();
        int customBuildCount = jsonDashlet.getCustomBuildCount();
        String aggregation = jsonDashlet.getAggregation();
        int i = 0;

        Map<Run<?, ?>, DashboardReport> dashboardReports = getDashboardReports(dashboard);
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();

        for (Map.Entry<Run<?, ?>, DashboardReport> dashboardReport : dashboardReports.entrySet()) {
            double metricValue = 0;
            if (dashboardReport.getValue().getChartDashlets() != null) {
                Measure m = dashboardReport.getValue().getMeasure(chartDashlet, measure);
                if (m != null) metricValue = StringUtils.isBlank(aggregation) ? m.getMetricValue() : m.getMetricValue(aggregation);
            }
            i++;
            dsb.add(metricValue, chartDashlet, new ChartUtil.NumberOnlyBuildLabel(dashboardReport.getKey()));
            if (customBuildCount != 0 && i == customBuildCount) break;
        }
        return dsb.build();
    }

    private JFreeChart createChart(final JSONDashlet jsonDashlet, final CategoryDataset dataset) throws UnsupportedEncodingException {
        final String measure = jsonDashlet.getMeasure();
        final String chartDashlet = jsonDashlet.getChartDashlet();
        final String dashboard = jsonDashlet.getDashboard();
        final String customMeasureName = jsonDashlet.getCustomName();
        final String aggregation = jsonDashlet.getAggregation();

        String unit = "", color = Messages.PerfSigProjectAction_DefaultColor();

        for (DashboardReport dr : getLastDashboardReports()) {
            if (dr.getName().equals(dashboard)) {
                final Measure m = dr.getMeasure(chartDashlet, measure);
                if (m != null) {
                    unit = aggregation.equalsIgnoreCase("Count") ? "num" : m.getUnit();
                    color = m.getColor();
                }
                break;
            }
        }

        String title = StringUtils.isBlank(customMeasureName) ? PerfSigUIUtils.generateTitle(measure, chartDashlet) : customMeasureName;

        final JFreeChart chart = ChartFactory.createBarChart(title, // title
                "build", // category axis label
                unit, // value axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                false, // include legend
                false, // tooltips
                false // urls
        );

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setForegroundAlpha(0.8f);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        final CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        //domainAxis.setLowerMargin(0.0);
        //domainAxis.setUpperMargin(0.0);
        //domainAxis.setCategoryMargin(0.0);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        final BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        renderer.setSeriesPaint(0, Color.decode(color));

        return chart;
    }

    public void doTestRunGraph(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        if (ChartUtil.awtProblemCause != null) {
            // not available. send out error message
            response.sendRedirect2(request.getContextPath() + "/images/headless.png");
            return;
        }

        //get customName and customBuildCount from persisted json
        if (request.getParameter("customName") == null && request.getParameter("customBuildCount") == null) {
            JSONDashlet jsonDashlet = getJsonDashletMap().get("unittest_overview");
            if (jsonDashlet != null) {
                ChartUtil.generateGraph(request, response, createTestRunChart(buildTestRunDataSet(String.valueOf(jsonDashlet.getCustomBuildCount())),
                        jsonDashlet.getCustomName()), PerfSigUIUtils.calcDefaultSize());
            }
        } else { //generate test run graph with GET parameters
            ChartUtil.generateGraph(request, response, createTestRunChart(buildTestRunDataSet(request.getParameter("customBuildCount")),
                    request.getParameter("customName")), PerfSigUIUtils.calcDefaultSize());
        }
    }

    private CategoryDataset buildTestRunDataSet(final String customBuildCount) {
        final DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
        int buildCount = 0, i = 0;
        if (StringUtils.isNotBlank(customBuildCount))
            buildCount = Integer.parseInt(customBuildCount);

        for (Run<?, ?> run : job.getBuilds()) {
            PerfSigTestDataWrapper testDataWrapper = run.getAction(PerfSigTestDataWrapper.class);
            if (testDataWrapper != null && testDataWrapper.getTestRuns() != null) {
                TestRun testRun = TestRun.mergeTestRuns(testDataWrapper.getTestRuns());
                if (testRun != null) {
                    dsb.add(testRun.getNumFailed(), "failed", new ChartUtil.NumberOnlyBuildLabel(run));
                    dsb.add(testRun.getNumDegraded(), "degraded", new ChartUtil.NumberOnlyBuildLabel(run));
                    dsb.add(testRun.getNumImproved(), "improved", new ChartUtil.NumberOnlyBuildLabel(run));
                    dsb.add(testRun.getNumPassed(), "passed", new ChartUtil.NumberOnlyBuildLabel(run));
                    dsb.add(testRun.getNumVolatile(), "volatile", new ChartUtil.NumberOnlyBuildLabel(run));
                    dsb.add(testRun.getNumInvalidated(), "invalidated", new ChartUtil.NumberOnlyBuildLabel(run));
                }
            }
            i++;
            if (buildCount != 0 && i == buildCount) break;
        }
        return dsb.build();
    }

    private JFreeChart createTestRunChart(final CategoryDataset dataset, final String customName) {
        String title = StringUtils.isNotBlank(customName) ? customName : "UnitTest overview";

        final JFreeChart chart = ChartFactory.createBarChart(title, // title
                "build", // category axis label
                "num", // value axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setForegroundAlpha(0.8f);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        final CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        //domainAxis.setLowerMargin(0.0);
        //domainAxis.setUpperMargin(0.0);
        //domainAxis.setCategoryMargin(0.0);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        final StackedBarRenderer br = new StackedBarRenderer();
        plot.setRenderer(br);
        br.setSeriesPaint(0, new Color(0xFF, 0x99, 0x99)); // degraded
        br.setSeriesPaint(1, ColorPalette.RED); // failed
        br.setSeriesPaint(2, new Color(0x00, 0xFF, 0x00)); // improved
        br.setSeriesPaint(3, ColorPalette.GREY); // invalidated
        br.setSeriesPaint(4, ColorPalette.BLUE); // passed
        br.setSeriesPaint(5, ColorPalette.YELLOW); // volatile

        return chart;
    }

    public List<DashboardReport> getLastDashboardReports() {
        final Run<?, ?> tb = job.getLastSuccessfulBuild();

        Run<?, ?> b = job.getLastBuild();
        while (b != null) {
            PerfSigBuildAction a = b.getAction(PerfSigBuildAction.class);
            if (a != null && (!b.isBuilding())) return a.getDashboardReports();
            if (b == tb)
                return new ArrayList<DashboardReport>();
            b = b.getPreviousBuild();
        }
        return new ArrayList<DashboardReport>();
    }

    public TestRun getTestRun(final Run<?, ?> run) {
        if (run != null) {
            PerfSigTestDataWrapper testDataWrapper = run.getAction(PerfSigTestDataWrapper.class);
            if (testDataWrapper != null) {
                return TestRun.mergeTestRuns(testDataWrapper.getTestRuns());
            }
        }
        return null;
    }

    public TestResult getTestAction(final Run<?, ?> run) {
        if (run != null) {
            TestResultAction testResultAction = run.getAction(TestResultAction.class);
            if (testResultAction != null) {
                return testResultAction.getResult();
            }
        }
        return null;
    }

    public Map<Run<?, ?>, DashboardReport> getDashboardReports(final String name) {
        final Map<Run<?, ?>, DashboardReport> dashboardReports = new HashMap<Run<?, ?>, DashboardReport>();
        if (job == null) {
            return dashboardReports;
        }
        for (Run<?, ?> currentRun : job.getBuilds()) {
            final PerfSigBuildAction perfSigBuildAction = currentRun.getAction(PerfSigBuildAction.class);
            if (perfSigBuildAction != null) {
                DashboardReport dashboardReport = perfSigBuildAction.getBuildActionResultsDisplay().getDashBoardReport(name);
                if (dashboardReport == null) {
                    dashboardReport = new DashboardReport(name);
                }
                dashboardReports.put(currentRun, dashboardReport);
            }
        }
        return dashboardReports;
    }

    public Map<String, JSONDashlet> getJsonDashletMap() throws IOException, InterruptedException {
        FilePath input = new FilePath(new File(getJsonConfigFilePath() + File.separator + JSON_FILENAME));
        if (jsonDashletMap.isEmpty() && input.exists()) {
            Type type = new TypeToken<Map<String, JSONDashlet>>() {
            }.getType();
            Map<String, JSONDashlet> dashlets = new Gson().fromJson(input.readToString(), type);
            jsonDashletMap.putAll(dashlets);
        } else if (jsonDashletMap.isEmpty() && !input.exists()) {
            jsonDashletMap.putAll(createJSONConfiguration());
            writeConfiguration(jsonDashletMap);
        }
        return jsonDashletMap;
    }

    private FilePath getJsonConfigFilePath() {
        final FilePath configPath = new FilePath(job.getConfigFile().getFile());
        return configPath.getParent();
    }

    @JavaScriptMethod
    public synchronized String getDashboardConfiguration(final String dashboard) throws IOException, InterruptedException {
        List<JSONDashlet> jsonDashletList = new ArrayList<JSONDashlet>();
        for (JSONDashlet jsonDashlet : getJsonDashletMap().values()) {
            if (jsonDashlet.getDashboard().equals(dashboard)) {
                jsonDashletList.add(jsonDashlet);
            }
        }

        return new Gson().toJson(jsonDashletList);
    }

    private Map<String, JSONDashlet> createJSONConfiguration() {
        int col = 1, row = 1;
        Map<String, JSONDashlet> jsonDashletMap = new HashMap<String, JSONDashlet>();
        for (DashboardReport dashboardReport : getLastDashboardReports()) {
            if (dashboardReport.isUnitTest()) {
                //ToDo: use measure name instead of id
                JSONDashlet dashlet = new JSONDashlet(col++, row, "unittest_overview", dashboardReport.getName());
                jsonDashletMap.put("unittest_overview", dashlet);
            }
            for (ChartDashlet chartDashlet : dashboardReport.getChartDashlets()) {
                for (Measure measure : chartDashlet.getMeasures()) {
                    String id = DigestUtils.md5Hex(dashboardReport.getName() + chartDashlet.getName() + measure.getName());
                    JSONDashlet dashlet = new JSONDashlet(col++, row, id, dashboardReport.getName(), chartDashlet.getName(), measure.getName(),
                            measure.getAggregation(), chartDashlet.getDescription());

                    jsonDashletMap.put(id, dashlet);

                    if (col > 3) {
                        col = 1;
                        row++;
                    }
                }
            }
        }
        return jsonDashletMap;
    }

    @JavaScriptMethod
    //ToDo: rewrite setDashboardConfiguration
    public void setDashboardConfiguration(final String dashboard, final String data) {
        final Map<String, MeasureNameHelper> map = new HashMap<String, MeasureNameHelper>();
        //generate a map to link between id and chartname
        for (DashboardReport dashboardReport : getLastDashboardReports())
            if (dashboardReport.getName().equals(dashboard))
                for (ChartDashlet chartDashlet : dashboardReport.getChartDashlets())
                    for (Measure measure : chartDashlet.getMeasures())
                        map.put(DigestUtils.md5Hex(dashboardReport.getName() + chartDashlet.getName() + measure.getName()),
                                new MeasureNameHelper(chartDashlet.getName(), measure.getName(), chartDashlet.getDescription()));

        String json = StringEscapeUtils.unescapeJava(data);
        if (!json.startsWith("[")) json = json.substring(1, json.length() - 1);

        try {
            final JSONArray gridConfiguration = JSONArray.fromObject(json);
            final JSONArray dashboardConfiguration = JSONArray.fromObject(getDashboardConfiguration(dashboard));
            //go through all configured grid items
            for (int i = 0; i < gridConfiguration.size(); i++) {
                final JSONObject obj = gridConfiguration.getJSONObject(i);
                final MeasureNameHelper tmp = map.get(obj.getString("id"));
                if (tmp != null) { //item needs some more information
                    obj.put("id", DigestUtils.md5Hex(dashboard + tmp.getChartDashlet() + tmp.getMeasure() + obj.getString("customName") + obj.getString("aggregation")));
                    obj.put("chartDashlet", tmp.getChartDashlet());
                    obj.put("measure", tmp.getMeasure());
                    obj.put("description", tmp.getDescription());
                } else {
                    //items with custom names need some more information
                    for (int j = 0; j < dashboardConfiguration.size(); j++) {
                        final JSONObject jsonObject = dashboardConfiguration.getJSONObject(j);
                        if (jsonObject.get("id").equals(obj.get("id"))) {
                            if (!obj.get("id").equals("unittest_overview")) {
                                obj.put("dashboard", jsonObject.get("dashboard"));
                                obj.put("chartDashlet", jsonObject.get("chartDashlet"));
                                obj.put("measure", jsonObject.get("measure"));
                                obj.put("description", jsonObject.get("description"));
                                obj.put("aggregation", jsonObject.get("aggregation"));
                            }
                            if (StringUtils.isNotBlank(jsonObject.getString("customName")))
                                obj.put("customName", jsonObject.get("customName"));
                            if (StringUtils.isNotBlank(jsonObject.getString("customBuildCount")))
                                obj.put("customBuildCount", jsonObject.get("customBuildCount"));
                            break;
                        }
                    }
                }
            }

            //writeConfiguration(dashboard, gridConfiguration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavaScriptMethod
    public Map<String, String> getAvailableMeasures(final String dashboard, final String dashlet) throws IOException {
        final Map<String, String> availableMeasures = new HashMap<String, String>();
        for (DashboardReport dashboardReport : getLastDashboardReports()) {
            if (dashboardReport.getName().equals(dashboard)) {
                for (ChartDashlet chartDashlet : dashboardReport.getChartDashlets()) {
                    if (chartDashlet.getName().equals(dashlet)) {
                        for (Measure measure : chartDashlet.getMeasures())
                            availableMeasures.put(DigestUtils.md5Hex(dashboardReport.getName() + chartDashlet.getName() + measure.getName()), measure.getName());
                        return availableMeasures;
                    }
                }
            }
        }
        return availableMeasures;
    }

    @JavaScriptMethod
    public String getAggregationFromMeasure(final String dashboard, final String dashlet, final String measure) throws IOException {
        for (DashboardReport dashboardReport : getLastDashboardReports()) {
            if (dashboardReport.getName().equals(dashboard)) {
                Measure m = dashboardReport.getMeasure(dashlet, measure);
                if (m != null) {
                    return m.getAggregation();
                }
            }
        }
        return "";
    }

    public List<ChartDashlet> getFilteredChartDashlets(final DashboardReport dashboardReport) throws IOException, InterruptedException {
        final List<ChartDashlet> filteredChartDashlets = new ArrayList<ChartDashlet>();
        if (dashboardReport.getChartDashlets() == null) {
            return filteredChartDashlets;
        }

        for (JSONDashlet jsonDashlet : getJsonDashletMap().values()) {
            if (!jsonDashlet.getDashboard().equals(dashboardReport.getName())) continue;
            boolean chartDashletFound = false;

            for (ChartDashlet dashlet : dashboardReport.getChartDashlets()) {
                if (dashlet.getName().equals(jsonDashlet.getChartDashlet())) {
                    for (Measure m : dashlet.getMeasures()) {
                        if (m.getName().equals(jsonDashlet.getMeasure())) {
                            ChartDashlet d;
                            String customName = jsonDashlet.getCustomName();
                            if (StringUtils.isBlank(customName)) {
                                d = new ChartDashlet(PerfSigUIUtils.generateTitle(m.getName(), dashlet.getName()));
                            } else {
                                d = new ChartDashlet(customName);
                            }
                            d.addMeasure(m);
                            filteredChartDashlets.add(d);
                            chartDashletFound = true;
                            break;
                        }
                    }
                }
            }
            if (!chartDashletFound) {
                ChartDashlet d = new ChartDashlet(jsonDashlet.getChartDashlet());
                d.addMeasure(new Measure(null));
                filteredChartDashlets.add(d);
            }
        }
        return filteredChartDashlets;
    }

    private synchronized void writeConfiguration(final Map<String, JSONDashlet> jsonDashletMap) throws IOException, InterruptedException {
        FilePath filePath = new FilePath(new File(getJsonConfigFilePath() + File.separator + JSON_FILENAME));
        filePath.write(new Gson().toJson(jsonDashletMap), null);
    }
}
