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
import com.google.gson.GsonBuilder;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.*;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.axis.AxisLabel;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.axis.CategoryAxis;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.axis.TimeAxis;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.axis.ValueAxis;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.code.*;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.feature.MagicType;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.series.Bar;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.series.Line;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.style.BackgroundStyle;
import de.tsystems.mms.apm.performancesignature.ui.util.Echarts.style.TextStyle;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.FilePath;
import hudson.model.Api;
import hudson.model.Item;
import hudson.model.ModelObject;
import hudson.model.Run;
import hudson.util.Graph;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import sun.font.FontFamily;

import javax.servlet.ServletException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ExportedBean
public class PerfSigBuildActionResultsDisplay implements ModelObject {
    private static final Logger LOGGER = Logger.getLogger(PerfSigBuildActionResultsDisplay.class.getName());
    private final transient PerfSigBuildAction buildAction;
    private final transient List<DashboardReport> currentDashboardReports;
    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization()
            .setPrettyPrinting().create();

    public PerfSigBuildActionResultsDisplay(final PerfSigBuildAction buildAction) {
        this.buildAction = buildAction;
        this.currentDashboardReports = this.buildAction.getDashboardReports();
    }

    @Override
    public String getDisplayName() {
        return Messages.PerfSigBuildActionResultsDisplay_DisplayName();
    }

    @SuppressWarnings("WeakerAccess")
    public Class getPerfSigUIUtils() {
        return PerfSigUIUtils.class;
    }

    @SuppressWarnings("WeakerAccess")
    public Run<?, ?> getBuild() {
        return this.buildAction.getBuild();
    }

    /**
     * Exposes this object to the remote API.
     */
    public Api getApi() {
        return new Api(this);
    }

    @SuppressWarnings("WeakerAccess")
    @Exported(name = "dashboardReports")
    public List<DashboardReport> getCurrentDashboardReports() {
        return this.currentDashboardReports;
    }

    @SuppressWarnings("WeakerAccess")
    public DashboardReport getPreviousDashboardReport(final String dashboard) {
        Run<?, ?> previousBuild = getBuild().getPreviousNotFailedBuild();
        if (previousBuild == null) {
            return null;
        }
        PerfSigBuildAction prevBuildAction = previousBuild.getAction(PerfSigBuildAction.class);
        if (prevBuildAction == null) {
            return null;
        }
        PerfSigBuildActionResultsDisplay previousBuildActionResults = prevBuildAction.getBuildActionResultsDisplay();
        return previousBuildActionResults.getDashBoardReport(dashboard);
    }

    @SuppressWarnings("WeakerAccess")
    public DashboardReport getDashBoardReport(final String reportName) {
        if (currentDashboardReports == null) {
            return null;
        }
        return currentDashboardReports.stream()
                .filter(dashboardReport -> dashboardReport.getName().equals(reportName))
                .findFirst().orElse(null);
    }

    @Restricted(NoExternalUse.class)
    public void doSummarizerGraph(final StaplerRequest request, final StaplerResponse response) throws IOException {
        checkPermission();
        final Graph graph = new GraphImpl(request, getBuild().getTimestamp().getTimeInMillis()) {
            @Override
            protected TimeSeriesCollection createDataSet() {
                String measure = request.getParameter("measure");
                String chartDashlet = request.getParameter("chartdashlet");
                String testCase = request.getParameter("testcase");
                TimeSeries timeSeries = new TimeSeries(chartDashlet, Second.class);

                DashboardReport dashboardReport = getDashBoardReport(testCase);
                Measure m = dashboardReport.getMeasure(chartDashlet, measure);
                if (m == null || m.getMeasurements() == null) {
                    return null;
                }

                 m.getMeasurements().stream().filter(Objects::nonNull).forEach(measurement ->
                        timeSeries.add(
                                new Second(new Date(measurement.getTimestamp())),
                                measurement.getMetricValue(m.getAggregation())));
                return new TimeSeriesCollection(timeSeries);
            }
        };
        graph.doPng(request, response);
    }

    @Restricted(NoExternalUse.class)
    public void doGenerateGraph(final StaplerRequest request, final StaplerResponse response) throws IOException {
        checkPermission();

        String measure = request.getParameter("measure");
        String chartDashlet = request.getParameter("chartdashlet");
        String testCase = request.getParameter("testcase");
        TimeSeries timeSeries = new TimeSeries(chartDashlet,Second.class);


        DashboardReport dashboardReport = getDashBoardReport(testCase);
        Measure m = dashboardReport.getMeasure(chartDashlet, measure);
        String unit = m.getUnit();
        String color=m.getColor();
//        if (m == null || m.getMeasurements() == null) {
//            return null;
//        }

//
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzzz yyyy",Locale.ENGLISH);
        List<Object> series=new ArrayList<>();
        List<Object> data=new ArrayList<>();
        m.getMeasurements().stream().filter(Objects::nonNull).forEach(measurement ->{
            Date date=new Date(measurement.getTimestamp());
            series.add(LocalDateTime.parse(date.toString(),formatter).toString().replace("T", "\n"));
            data.add(measurement.getMetricValue(m.getAggregation()));

        });

        if ("num".equalsIgnoreCase(unit)) {


            Option option=new Option();
            CategoryAxis categoryAxis=new CategoryAxis();
            Bar bar=new Bar();
            TextStyle textStyle=new TextStyle();
            textStyle.setFontStyle(FontStyle.normal);
            textStyle.setFontFamily("sans-serif");
            textStyle.setFontSize(15);
            textStyle.setWidth(250);
            textStyle.setOverflow("break");
            textStyle.setFontWeight(FontWeight.bold);
            Tooltip tooltip=new Tooltip();
            tooltip.setTrigger(Trigger.axis);
            option.setTooltip(tooltip);;
            BackgroundStyle backgroundStyle=new BackgroundStyle();
            backgroundStyle.setColor("rgba(180, 180, 180, 0.2)");
            bar.setBackgroundStyle(backgroundStyle);
            bar.setColor(color);
            bar.setShowBackground(true);
            Title title=new Title();
            title.setText(PerfSigUIUtils.generateTitle(measure, chartDashlet, m.getAggregation()));
            title.left(X.center);
            title.textStyle(textStyle);
            option.yAxis(new ValueAxis());
            if(data.isEmpty())
            {
                TextStyle subtextstyle=new TextStyle();
                subtextstyle.setLineHeight(50);
                subtextstyle.setFontStyle(FontStyle.normal);
                subtextstyle.setFontFamily("sans-serif");
                subtextstyle.setFontSize(15);
                subtextstyle.setFontWeight(FontWeight.normal);
                title.setSubtext("No Data Available");
                title.setSubtextStyle(subtextstyle);

            }
            option.title(title);
            AxisLabel axisLabel=new AxisLabel();
            axisLabel.setInterval("auto");
            categoryAxis.axisLabel(axisLabel);
            option.yAxis(new ValueAxis());
            categoryAxis.setData(series);
            option.xAxis(categoryAxis);
            bar.setData(data);
            option.series(bar);
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try { out.print(GSON.toJson(option));
                out.flush();
            } catch (Exception e) { e.printStackTrace();
                }
        }
        else {


            Option option=new Option();
            Line line=new Line();
            option.yAxis(new ValueAxis());
            TextStyle textStyle=new TextStyle();
            textStyle.setFontStyle(FontStyle.normal);
            textStyle.setFontFamily("sans-serif");
            textStyle.setFontSize(15);
            textStyle.setWidth(250);
            textStyle.setOverflow("break");
            textStyle.setFontWeight(FontWeight.bold);
            Tooltip tooltip=new Tooltip();
            tooltip.setTrigger(Trigger.axis);
            option.setTooltip(tooltip);;
            BackgroundStyle backgroundStyle=new BackgroundStyle();
            backgroundStyle.setColor("rgba(180, 180, 180, 0.2)");
            line.setBackgroundStyle(backgroundStyle);
            line.setColor(color);
            line.setShowBackground(true);
            Title title=new Title();
            title.setText(PerfSigUIUtils.generateTitle(measure, chartDashlet, m.getAggregation()));
            title.left(X.center);
            title.textStyle(textStyle);
            option.yAxis(new ValueAxis());
            if(data.isEmpty())
            {
                TextStyle subtextstyle=new TextStyle();
                subtextstyle.setLineHeight(50);
                subtextstyle.setFontStyle(FontStyle.normal);
                subtextstyle.setFontFamily("sans-serif");
                subtextstyle.setFontSize(15);
                subtextstyle.setFontWeight(FontWeight.normal);
                title.subtextStyle(subtextstyle);
                title.setSubtext("No Data Available");


            }
            option.title(title);

            CategoryAxis categoryAxis=new CategoryAxis();
            categoryAxis.setData(series);
            option.xAxis(categoryAxis);
            line.setData(data);
            option.series(line);
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                out.print(GSON.toJson(option));
                out.flush();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    private void checkPermission() {
        buildAction.getBuild().checkPermission(Item.READ);
    }

    @Restricted(NoExternalUse.class)
    public void doGetSingleReport(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        checkPermission();
        serveFile("Singlereport", request, response);
    }

    @Restricted(NoExternalUse.class)
    public void doGetComparisonReport(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        checkPermission();
        serveFile("Comparisonreport", request, response);
    }

    @Restricted(NoExternalUse.class)
    public void doGetSession(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        checkPermission();
        serveFile("", request, response);
    }

    @Restricted(NoExternalUse.class)
    public void doGetSingleReportList(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        checkPermission();
        getReportList("Singlereport", request, response);
    }

    @Restricted(NoExternalUse.class)
    public void doGetComparisonReportList(final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        checkPermission();
        getReportList("Comparisonreport", request, response);
    }

    private void getReportList(final String type, final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        String testCase = request.getParameter("testCase");
        if (StringUtils.isBlank(testCase)) {
            testCase = "";
        }

        FilePath reportDir = PerfSigUIUtils.getReportDirectory(getBuild());
        List<FilePath> files = reportDir.list(new RegexFileFilter(String.format("%s.*%s.*.pdf", type, testCase)));
        List<String> fileNames = files.stream().map(fp -> PerfSigUIUtils.removeExtension(fp.getName())).collect(Collectors.toList());
        String output = new Gson().toJson(fileNames);
        IOUtils.write(output, response.getOutputStream(), StandardCharsets.UTF_8);
    }

    private void serveFile(final String type, final StaplerRequest request, final StaplerResponse response) throws IOException, InterruptedException {
        String testCase = request.getParameter("testCase");
        if (StringUtils.isBlank(testCase)) {
            testCase = "";
        }

        String numberString = request.getParameter("number");
        int number;
        try {
            number = Integer.parseInt(numberString);
        } catch (NumberFormatException ignored) {
            number = 0;
        }

        FilePath filePath = PerfSigUIUtils.getReportDirectory(getBuild());
        String extension = StringUtils.isBlank(type) ? ".dts" : ".pdf";
        List<FilePath> files = filePath.list(new RegexFileFilter(String.format("%s.*%s.*%s", type, testCase, extension)));
        if (files.isEmpty()) {
            response.sendError(404, "requested resource not found");
            return;
        }

        FilePath requestedFile = number > 0 ? files.get(number) : files.get(0);
        if (requestedFile == null) {
            response.sendError(404, "requested resource not found");
            return;
        }
        InputStream inStream = requestedFile.read();
        // gets MIME type of the file
        String mimeType = "pdf".equals(extension) ? "application/pdf" : "application/octet-stream";// set to binary type if MIME mapping not found

        try {
            // forces download
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", requestedFile.getName());
            response.setHeader(headerKey, headerValue);
            response.serveFile(request, inStream, requestedFile.lastModified(), requestedFile.length(), "mime-type:" + mimeType);
        } catch (ServletException e) {
            LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }

    private abstract class GraphImpl extends Graph {
        private final StaplerRequest request;

        GraphImpl(final StaplerRequest request, final long timestamp) {
            super(timestamp, 600, 300);
            this.request = request;
        }

        protected abstract TimeSeriesCollection createDataSet();

        protected JFreeChart createGraph() {
            String measure = request.getParameter("measure");
            String chartDashlet = request.getParameter("chartdashlet");
            String testCase = request.getParameter("testcase");

            final DashboardReport dashboardReport = getDashBoardReport(testCase);
            final Measure m = dashboardReport.getMeasure(chartDashlet, measure);
            if (m == null) {
                return null;
            }

            String color = m.getColor();
            String unit = m.getUnit();

            JFreeChart chart;
            if ("num".equalsIgnoreCase(unit)) {
                chart = ChartFactory.createXYBarChart(PerfSigUIUtils.generateTitle(measure, chartDashlet, m.getAggregation()), // title
                        "time", // domain axis label
                        true,
                        unit,
                        createDataSet(), // data
                        PlotOrientation.VERTICAL, // orientation
                        false, // include legend
                        false, // tooltips
                        false // urls
                );
            } else {
                chart = ChartFactory.createTimeSeriesChart(PerfSigUIUtils.generateTitle(measure, chartDashlet, m.getAggregation()), // title
                        "time", // domain axis label
                        unit,
                        createDataSet(), // data
                        false, // include legend
                        false, // tooltips
                        false // urls
                );
            }

            XYPlot xyPlot = chart.getXYPlot();
            xyPlot.setForegroundAlpha(0.8f);
            xyPlot.setRangeGridlinesVisible(true);
            xyPlot.setRangeGridlinePaint(Color.black);
            xyPlot.setOutlinePaint(null);

            XYItemRenderer xyitemrenderer = xyPlot.getRenderer();
            if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
                XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
                xylineandshaperenderer.setBaseShapesVisible(true);
                xylineandshaperenderer.setBaseShapesFilled(true);
            }
            DateAxis dateAxis = (DateAxis) xyPlot.getDomainAxis();
            dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
            dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
            xyitemrenderer.setSeriesPaint(0, Color.decode(Optional.ofNullable(color).orElse("#FF0000")));
            xyitemrenderer.setSeriesStroke(0, new BasicStroke(2));

            chart.setBackgroundPaint(Color.white);
            return chart;
        }
    }
}
