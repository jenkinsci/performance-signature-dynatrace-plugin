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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.ui.model.JSONDashlet;
import de.tsystems.mms.apm.performancesignature.ui.model.JSONDashletComparator;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.Option;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.Title;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.Tooltip;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.axis.AxisLabel;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.axis.CategoryAxis;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.axis.ValueAxis;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.code.FontStyle;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.code.FontWeight;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.code.NameLocation;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.code.Trigger;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.series.Bar;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.style.BackgroundStyle;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.style.NameTextStyle;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.style.TextStyle;
import de.tsystems.mms.apm.performancesignature.ui.util.NumberOnlyBuildLabel;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.XmlFile;
import hudson.model.Job;
import hudson.model.ProminentProjectAction;
import hudson.model.Run;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import hudson.tasks.test.TestResultProjectAction;
import hudson.util.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.nullness.Opt;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
////import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import javax.print.attribute.TextSyntax;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PerfSigProjectAction extends PerfSigBaseAction implements ProminentProjectAction {
    static final String UNITTEST_DASHLETNAME = "unittest_overview";
    private static final String JSON_FILENAME = "gridconfig.xml";
    private static final Logger logger = Logger.getLogger(PerfSigProjectAction.class.getName());
    private static final XStream2 XSTREAM2 = new XStream2();
    private static final Gson GSON = new Gson();
    private final Job<?, ?> job;
    private transient Map<String, JSONDashlet> jsonDashletMap;

    static {
        XSTREAM2.addCompatibilityAlias("de.tsystems.mms.apm.performancesignature.model.JSONDashlet", JSONDashlet.class);
    }

    public PerfSigProjectAction(final Job<?, ?> job) {
        this.job = job;
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

    @SuppressWarnings("WeakerAccess")
    public Class<PerfSigUIUtils> getPerfSigUIUtils() {
        return PerfSigUIUtils.class;
    }

    private synchronized Map<String, JSONDashlet> getJsonDashletMap() {
        if (this.jsonDashletMap == null) {
            this.jsonDashletMap = new ConcurrentHashMap<>();
            this.jsonDashletMap.putAll(readConfiguration());
        }
        return this.jsonDashletMap;
    }

    public void doSummarizerGraph(final StaplerRequest request, final StaplerResponse response) throws IOException {
        checkPermission();
        String id = request.getParameter("id");
        JSONDashlet knownJsonDashlet = getJsonDashletMap().get(id);
        final JSONDashlet jsonDashletToRender;

        if (knownJsonDashlet != null) { //dashlet from stored configuration
            jsonDashletToRender = knownJsonDashlet;
        } else {
            JSONDashlet newJsonDashlet = createJSONConfiguration(false).get(id);
            if (newJsonDashlet != null) { //new dashlet
                if (StringUtils.isNotBlank(request.getParameter("aggregation"))) {
                    newJsonDashlet.setAggregation(request.getParameter("aggregation"));
                }
                newJsonDashlet.setCustomName(request.getParameter("customName"));
                newJsonDashlet.setCustomBuildCount(request.getParameter("customBuildCount"));

                jsonDashletToRender = newJsonDashlet;
            } else {
                response.sendError(404, "no chartdashlet found for id " + id);
                return;
            }
        }

        final Graph graph = new PerfGraphImpl(jsonDashletToRender) {
            @Override
            protected DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> createDataSet(){
                String dashboard = jsonDashletToRender.getDashboard();
                String chartDashlet = jsonDashletToRender.getChartDashlet();
                String measure = jsonDashletToRender.getMeasure();
                String buildCount = jsonDashletToRender.getCustomBuildCount();
                String aggregation = jsonDashletToRender.getAggregation();
                int customBuildCount = 0;
                int i = 0;

                if (StringUtils.isNotBlank(buildCount)) {
                    customBuildCount = Integer.parseInt(buildCount);
                }

                Map<Run<?, ?>, DashboardReport> dashboardReports = getDashboardReports(dashboard);
                DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<>();

                for (Map.Entry<Run<?, ?>, DashboardReport> dashboardReport : dashboardReports.entrySet()) {
                    double metricValue = 0;
                    if (dashboardReport.getValue().getChartDashlets() != null) {
                        Measure m = dashboardReport.getValue().getMeasure(chartDashlet, measure);
                        if (m != null) {
                            metricValue = StringUtils.isBlank(aggregation) ? m.getMetricValue() : m.getMetricValue(aggregation);
                        }
                    }
                    i++;
                    dsb.add(metricValue, chartDashlet, new ChartUtil.NumberOnlyBuildLabel(dashboardReport.getKey()));
                    if (customBuildCount != 0 && i == customBuildCount) {
                        break;
                    }
                }
                return dsb;
            }
        };
        graph.doPng(request, response);
    }

    public void doGenerateGraph(final StaplerRequest request, final StaplerResponse response) throws IOException
    {
        checkPermission();
        String id = request.getParameter("id");
        JSONDashlet knownJsonDashlet = getJsonDashletMap().get(id);
        final JSONDashlet jsonDashletToRender;

        if (knownJsonDashlet != null) { //dashlet from stored configuration
            jsonDashletToRender = knownJsonDashlet;
        } else {
            JSONDashlet newJsonDashlet = createJSONConfiguration(false).get(id);
            if (newJsonDashlet != null) { //new dashlet
                if (StringUtils.isNotBlank(request.getParameter("aggregation"))) {
                    newJsonDashlet.setAggregation(request.getParameter("aggregation"));
                }
                newJsonDashlet.setCustomName(request.getParameter("customName"));
                newJsonDashlet.setCustomBuildCount(request.getParameter("customBuildCount"));

                jsonDashletToRender = newJsonDashlet;
            } else {
                response.sendError(404, "no chartdashlet found for id " + id);
                return;
            }
        }
        String dashboard = jsonDashletToRender.getDashboard();
        String chartDashlet = jsonDashletToRender.getChartDashlet();
        String measure = jsonDashletToRender.getMeasure();
        String buildCount = jsonDashletToRender.getCustomBuildCount();
        String aggregation = jsonDashletToRender.getAggregation();
        final String customMeasureName = jsonDashletToRender.getCustomName();
        int customBuildCount = 0;
        int i = 0;

        if (StringUtils.isNotBlank(buildCount)) {
            customBuildCount = Integer.parseInt(buildCount);
        }
        List<Double> series=new ArrayList<>();
        List<String>  data=new ArrayList<>();
        Map<Run<?, ?>, DashboardReport> dashboardReports = getDashboardReports(dashboard);

        for (Map.Entry<Run<?, ?>, DashboardReport> dashboardReport : dashboardReports.entrySet()) {
            double metricValue = 0;
            if (dashboardReport.getValue().getChartDashlets() != null) {
                Measure m = dashboardReport.getValue().getMeasure(chartDashlet, measure);
                if (m != null) {
                    metricValue = StringUtils.isBlank(aggregation) ? m.getMetricValue() : m.getMetricValue(aggregation);
                }
            }
            i++;
            series.add(metricValue);
            data.add(new ChartUtil.NumberOnlyBuildLabel(dashboardReport.getKey()).toString());
            if (customBuildCount != 0 && i == customBuildCount) {
                break;
            }
        }

        String unit = "";
        String color = "#FF5555";

        for (DashboardReport dr : getLastDashboardReports()) {
            if (dr.getName().equals(dashboard)) {
                final Measure m = dr.getMeasure(chartDashlet, measure);
                if (m != null) {
                    unit = m.getUnit(aggregation);
                    color = m.getColor() != null ? m.getColor() : color;
                }
                break;
            }
        }
        String chartTitle = StringUtils.isBlank(customMeasureName) ? PerfSigUIUtils.generateTitle(measure, chartDashlet, aggregation) : customMeasureName;
        Bar bar=new Bar();
        CategoryAxis categoryAxis=new CategoryAxis();
        categoryAxis.interval(3);
        Option option=new Option();
        Tooltip toolTip=new Tooltip();
        toolTip.setTrigger(Trigger.axis);
        de.tsystems.mms.apm.performancesignature.ui.util.Echarts.style.TextStyle textStyle=new TextStyle();
        textStyle.setFontStyle(FontStyle.normal);
        textStyle.setFontFamily("sans-serif");
        textStyle.setFontSize(15);
        textStyle.setFontWeight(FontWeight.bold);
        textStyle.setWidth(300);
        textStyle.setOverflow("break");


        BackgroundStyle backgroundStyle=new BackgroundStyle();
        backgroundStyle.setColor("rgba(180, 180, 180, 0.2)");
        bar.setColor(color);
        bar.setShowBackground(true);
        bar.setAnimation(true);
        NameTextStyle nameTextStyle=new NameTextStyle();
        nameTextStyle.setFontWeight(FontWeight.bolder);

        AxisLabel axisLabel=new AxisLabel();
        axisLabel.setInterval(2);
        axisLabel.setRotate(90);
        axisLabel.setFontWeight("bold");

        ValueAxis valueAxis=new ValueAxis();
        valueAxis.setName(unit);
        valueAxis.setNameLocation(NameLocation.middle);
        valueAxis.setNameGap(40);
        option.yAxis(valueAxis);
        categoryAxis.setData(data);
        categoryAxis.axisLabel(axisLabel);
        categoryAxis.minInterval(5);
        option.xAxis(categoryAxis);
        Title title=new Title();
        title.textStyle(textStyle);
        title.setText(chartTitle);
        title.setLeft("center");
        option.setTitle(title);
        option.tooltip(toolTip);
        bar.setData(series);
        option.series(bar);
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(GSON.toJson(option));
        out.flush();
    }
    public void doTestRunGraph(final StaplerRequest request, final StaplerResponse response) throws IOException {
        checkPermission();
        final String customName, customBuildCount;

        JSONDashlet jsonDashlet = getJsonDashletMap().get(UNITTEST_DASHLETNAME);
        if (jsonDashlet != null) {
            customName = jsonDashlet.getCustomName();
            customBuildCount = String.valueOf(jsonDashlet.getCustomBuildCount());
        } else { //generate test run graph with GET parameters
            customName = request.getParameter("customName");
            customBuildCount = request.getParameter("customBuildCount");
        }

        final Graph graph = new TestRunGraphImpl(customName) {
            @Override
            protected DataSetBuilder<String, NumberOnlyBuildLabel> createDataSet() {
                final DataSetBuilder<String, NumberOnlyBuildLabel> dsb = new DataSetBuilder<>();
                int buildCount = 0, i = 0;
                if (StringUtils.isNotBlank(customBuildCount)) {
                    buildCount = Integer.parseInt(customBuildCount);
                }

                for (Run<?, ?> run : job.getBuilds()) {
                    TestRun testRun = getTestRun(run);
                    if (testRun != null) {
                        dsb.add(testRun.getNumFailed(), "failed", new NumberOnlyBuildLabel(run));
                        dsb.add(testRun.getNumDegraded(), "degraded", new NumberOnlyBuildLabel(run));
                        dsb.add(testRun.getNumImproved(), "improved", new NumberOnlyBuildLabel(run));
                        dsb.add(testRun.getNumPassed(), "passed", new NumberOnlyBuildLabel(run));
                        dsb.add(testRun.getNumVolatile(), "volatile", new NumberOnlyBuildLabel(run));
                        dsb.add(testRun.getNumInvalidated(), "invalidated", new NumberOnlyBuildLabel(run));
                    }
                    i++;
                    if (buildCount != 0 && i == buildCount) {
                        break;
                    }
                }
                return dsb;
            }
        };
        graph.doPng(request, response);
    }

    private void checkPermission() {
        job.checkPermission(Job.READ);
    }

    @SuppressWarnings("WeakerAccess")
    public List<DashboardReport> getLastDashboardReports() {
        final Run<?, ?> tb = job.getLastSuccessfulBuild();

        Run<?, ?> b = job.getLastBuild();
        while (b != null) {
            PerfSigBuildAction a = b.getAction(PerfSigBuildAction.class);
            if (a != null && (!b.isBuilding())) {
                return a.getDashboardReports();
            }
            if (b == tb) {
                return new ArrayList<>();
            }
            b = b.getPreviousBuild();
        }
        return new ArrayList<>();
    }

    public TestRun getTestRun(final Run<?, ?> run) {
        TestResult testResult = getTestResult(run);
        if (testResult != null) {
            PerfSigTestAction testAction = testResult.getTestAction(PerfSigTestAction.class);
            if (testAction != null) {
                return TestRun.mergeTestRuns(testAction.getTestData().getTestRuns());
            }
        }
        return null;
    }

    public TestResult getTestResult(final Run<?, ?> run) {
        if (run != null) {
            TestResultAction testResultAction = run.getAction(TestResultAction.class);
            if (testResultAction != null) {
                return testResultAction.getResult();
            }
        }
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    public Map<Run<?, ?>, DashboardReport> getDashboardReports(final String name) {
        final Map<Run<?, ?>, DashboardReport> dashboardReports = new HashMap<>();
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

    @JavaScriptMethod
    public String getDashboardConfiguration(final String dashboard) {
        List<JSONDashlet> jsonDashletList = getJsonDashletMap().values().stream()
                .filter(jsonDashlet -> jsonDashlet.getDashboard().equals(dashboard))
                .sorted(new JSONDashletComparator()).collect(Collectors.toList());
        return GSON.toJson(jsonDashletList);
    }

    private Map<String, JSONDashlet> createJSONConfiguration(final boolean useRandomId) {
        int col = 1;
        int row = 1;

        logger.fine(addTimeStampToLog("grid configuration generation started"));
        Map<String, JSONDashlet> newJsonDashletMap = new HashMap<>();
        for (DashboardReport dashboardReport : getLastDashboardReports()) {
            if (dashboardReport.isUnitTest()) {
                JSONDashlet dashlet = new JSONDashlet(col++, row, UNITTEST_DASHLETNAME, dashboardReport.getName());
                newJsonDashletMap.put(UNITTEST_DASHLETNAME, dashlet);
            }
            for (ChartDashlet chartDashlet : dashboardReport.getChartDashlets()) {
                for (Measure measure : chartDashlet.getMeasures()) {
                    JSONDashlet dashlet = new JSONDashlet(col++, row, dashboardReport.getName(), chartDashlet.getName(), measure.getName(),
                            measure.getAggregation(), chartDashlet.getDescription());
                    if (useRandomId) {
                        dashlet.setId(dashlet.generateID());
                    }

                    newJsonDashletMap.put(dashlet.getId(), dashlet);

                    if (col > 3) {
                        col = 1;
                        row++;
                    }
                }
            }
        }
        logger.fine(addTimeStampToLog("grid configuration generation finished"));
        return newJsonDashletMap;
    }

    /*
    only for debug purposes
     */
    private String addTimeStampToLog(final String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date()) + ": " + this.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this)) +
                ", threadId:" + Thread.currentThread().getId() + " " + message;
    }

    @SuppressWarnings("WeakerAccess")
    @JavaScriptMethod
    public void setDashboardConfiguration(final String dashboard, final String data) {
        Map<String, JSONDashlet> defaultConfiguration = createJSONConfiguration(false);
        HashSet<String> idsFromJson;

        String json = StringEscapeUtils.unescapeJava(data);
        if (!json.startsWith("[")) {
            json = json.substring(1, json.length() - 1);
        }

        List<JSONDashlet> jsonDashletList = GSON.fromJson(json, new TypeToken<List<JSONDashlet>>() {
        }.getType());
        idsFromJson = jsonDashletList.stream().map(JSONDashlet::getId).collect(Collectors.toCollection(HashSet::new));

        //filter out dashlets from other dashboards
        //remove dashlet, if it's not present in gridConfiguration
        getJsonDashletMap().values().stream()
                .filter(jsonDashlet -> jsonDashlet.getDashboard().equals(dashboard) && !idsFromJson.contains(jsonDashlet.getId()))
                .forEach(jsonDashlet -> getJsonDashletMap().remove(jsonDashlet.getId()));

        jsonDashletList.forEach(modifiedDashlet -> {
            JSONDashlet unmodifiedDashlet = defaultConfiguration.get(modifiedDashlet.getId());
            JSONDashlet originalDashlet = getJsonDashletMap().get(modifiedDashlet.getId());
            if (modifiedDashlet.getId().equals(UNITTEST_DASHLETNAME)) {
                if (originalDashlet != null) {
                    modifiedDashlet.setCustomBuildCount(originalDashlet.getCustomBuildCount());
                    modifiedDashlet.setCustomName(originalDashlet.getCustomName());
                }
                getJsonDashletMap().put(modifiedDashlet.getId(), modifiedDashlet);
            } else if (unmodifiedDashlet != null) { //newly added dashlets
                modifiedDashlet.setDashboard(unmodifiedDashlet.getDashboard());
                modifiedDashlet.setChartDashlet(unmodifiedDashlet.getChartDashlet());
                modifiedDashlet.setMeasure(unmodifiedDashlet.getMeasure());
                modifiedDashlet.setDescription(unmodifiedDashlet.getDescription());
                modifiedDashlet.setId(modifiedDashlet.generateID());

                getJsonDashletMap().put(modifiedDashlet.getId(), modifiedDashlet);
            } else if (originalDashlet != null) { //old dashlets
                modifiedDashlet.setDashboard(originalDashlet.getDashboard());
                modifiedDashlet.setChartDashlet(originalDashlet.getChartDashlet());
                modifiedDashlet.setMeasure(originalDashlet.getMeasure());
                modifiedDashlet.setDescription(originalDashlet.getDescription());
                modifiedDashlet.setAggregation(originalDashlet.getAggregation());
                modifiedDashlet.setCustomBuildCount(originalDashlet.getCustomBuildCount());
                modifiedDashlet.setCustomName(originalDashlet.getCustomName());

                modifiedDashlet.setId(modifiedDashlet.generateID());
                getJsonDashletMap().remove(originalDashlet.getId());
                getJsonDashletMap().put(modifiedDashlet.getId(), modifiedDashlet);
            }
        });
        writeConfiguration(getJsonDashletMap());
    }

    @SuppressWarnings("WeakerAccess")
    @JavaScriptMethod
    public Map<String, String> getAvailableMeasures(final String dashboard, final String dashlet) {
        Collection<JSONDashlet> jsonDashlets = createJSONConfiguration(false).values();
        return jsonDashlets.stream()
                .filter(jsonDashlet -> jsonDashlet.getDashboard().equals(dashboard) && jsonDashlet.getChartDashlet().equals(dashlet))
                .sorted()
                .collect(Collectors.toMap(JSONDashlet::getId, JSONDashlet::getMeasure, (a, b) -> b, LinkedHashMap::new));
    }

    @SuppressWarnings("WeakerAccess")
    @JavaScriptMethod
    public String getAggregationFromMeasure(final String dashboard, final String dashlet, final String measure) {
        return getLastDashboardReports().stream()
                .filter(dashboardReport -> dashboardReport.getName().equals(dashboard))
                .map(dashboardReport -> dashboardReport.getMeasure(dashlet, measure))
                .filter(Objects::nonNull).findFirst()
                .map(Measure::getAggregation).orElse("");
    }

    @SuppressWarnings("unused")
    public Map<JSONDashlet, Measure> getFilteredChartDashlets(final DashboardReport dashboardReport) {
        Map<JSONDashlet, Measure> filteredChartDashlets = new TreeMap<>(new JSONDashletComparator());

        for (JSONDashlet jsonDashlet : getJsonDashletMap().values()) {
            if (!jsonDashlet.getDashboard().equals(dashboardReport.getName())) {
                continue;
            }
            boolean chartDashletFound = false;

            for (ChartDashlet dashlet : dashboardReport.getChartDashlets()) {
                if (dashlet.getName().equals(jsonDashlet.getChartDashlet())) {
                    for (Measure m : dashlet.getMeasures()) {
                        if (m.getName().equals(jsonDashlet.getMeasure())) {
                            filteredChartDashlets.put(jsonDashlet, m);
                            chartDashletFound = true;
                            break;
                        }
                    }
                }
            }
            if (!chartDashletFound && !jsonDashlet.getId().equals(UNITTEST_DASHLETNAME)) {
                filteredChartDashlets.put(jsonDashlet, new Measure());
            }
        }
        return filteredChartDashlets;
    }

    private synchronized XmlFile getConfigFile() {
        return new XmlFile(XSTREAM2, new File(job.getConfigFile().getFile().getParent(), JSON_FILENAME));
    }

    @SuppressWarnings("unchecked")
    private Map<String, JSONDashlet> readConfiguration() {
        logger.fine(addTimeStampToLog("grid configuration read started"));
        try {
            if (getConfigFile().exists()) {
                Map<String, JSONDashlet> configuration = (Map<String, JSONDashlet>) getConfigFile().read();
                logger.fine(addTimeStampToLog("grid configuration read finished (config file exists)"));
                return configuration;
            } else {
                Map<String, JSONDashlet> newConfiguration = createJSONConfiguration(true);
                writeConfiguration(newConfiguration);
                logger.fine(addTimeStampToLog("grid configuration read finished (config file created)"));
                return newConfiguration;
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, Messages.PerfSigProjectAction_FailedToLoadConfigFile(getConfigFile()), e);
        }
        return new HashMap<>();
    }

    private void writeConfiguration(final Map<String, JSONDashlet> jsonDashletMap) {
        try {
            logger.fine(addTimeStampToLog("grid configuration write started"));
            getConfigFile().write(jsonDashletMap);
            logger.fine(addTimeStampToLog("grid configuration write finished"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, Messages.PerfSigProjectAction_FailedToSaveGrid(), e);
        }
    }

    private abstract class PerfGraphImpl extends Graph {
        private final JSONDashlet jsonDashlet;

        PerfGraphImpl(final JSONDashlet jsonDashlet) {
            super(-1, 600, 300);
            this.jsonDashlet = jsonDashlet;
        }

        protected abstract DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> createDataSet();

        protected JFreeChart createGraph() {
            final String measure = jsonDashlet.getMeasure();
            final String chartDashlet = jsonDashlet.getChartDashlet();
            final String dashboard = jsonDashlet.getDashboard();
            final String customMeasureName = jsonDashlet.getCustomName();
            final String aggregation = jsonDashlet.getAggregation();

            String unit = "";
            String color = "#FF5555";

            for (DashboardReport dr : getLastDashboardReports()) {
                if (dr.getName().equals(dashboard)) {
                    final Measure m = dr.getMeasure(chartDashlet, measure);
                    if (m != null) {
                        unit = m.getUnit(aggregation);
                        color = m.getColor() != null ? m.getColor() : color;
                    }
                    break;
                }
            }

            String title = StringUtils.isBlank(customMeasureName) ? PerfSigUIUtils.generateTitle(measure, chartDashlet, aggregation) : customMeasureName;

            final JFreeChart chart = ChartFactory.createBarChart(title, // title
                    Messages.PerfSigProjectAction_Build(), // category axis label
                    unit, // value axis label
                    createDataSet().build(), // data
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

            final org.jfree.chart.axis.CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
            plot.setDomainAxis(domainAxis);
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
            domainAxis.setLowerMargin(0.0);
            domainAxis.setUpperMargin(0.0);
            domainAxis.setCategoryMargin(0.0);

            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            final BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
            renderer.setSeriesPaint(0, Color.decode(color));

            return chart;
        }
    }

    private abstract class TestRunGraphImpl extends Graph {
        private final String customName;

        TestRunGraphImpl(final String customName) {
            super(-1, 600, 300);
            this.customName = customName;
        }

        protected abstract DataSetBuilder<String, NumberOnlyBuildLabel> createDataSet();

        protected JFreeChart createGraph() {
            String title = StringUtils.isNotBlank(customName) ? customName : "UnitTest overview";

            final JFreeChart chart = ChartFactory.createBarChart(title, // title
                    "build", // category axis label
                    "num", // value axis label
                    createDataSet().build(), // data
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

            final org.jfree.chart.axis.CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
            plot.setDomainAxis(domainAxis);
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
            domainAxis.setLowerMargin(0.0);
            domainAxis.setUpperMargin(0.0);
            domainAxis.setCategoryMargin(0.0);

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
    }
}
