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

package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measurement;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.Metric;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Result;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Timeseries;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.time.DateUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Timeseries.AggregationEnum;

public class DynatraceRecorder extends Recorder implements SimpleBuildStep {
    private final String envId;
    private List<Metric> metrics;

    @DataBoundConstructor
    public DynatraceRecorder(final String envId, final List<Metric> metrics) {
        this.envId = envId;
        this.metrics = metrics;
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws IOException {
        PluginLogger logger = PerfSigUIUtils.createLogger(listener.getLogger());
        DynatraceServerConnection serverConnection = DynatraceUtils.createDynatraceServerConnection(envId);
        logger.log("getting metric data from Dynatrace Server");

        Date start = DateUtils.addHours(new Date(), -2);
        Date end = new Date();

        Map<String, Timeseries> timeseries = serverConnection.getTimeseries()
                .parallelStream().collect(Collectors.toMap(Timeseries::getTimeseriesId, item -> item));

        DashboardReport dashboardReport = new DashboardReport("loadtest");
        for (Metric metric : metrics) {
            Timeseries tm = timeseries.get(metric.getMetricId());
            Map<AggregationEnum, Result> aggregations = tm.getAggregationTypes().stream()
                    .collect(Collectors.toMap(aggregation -> aggregation,
                            aggregation -> serverConnection.getTimeseriesData(metric.getMetricId(), start, end, aggregation),
                            (a, b) -> b, LinkedHashMap::new));

            Result baseResult = aggregations.get(AggregationEnum.AVG);
            ChartDashlet chartDashlet = new ChartDashlet();
            chartDashlet.setName(tm.getDetailedSource() + " - " + tm.getDisplayName());

            if (baseResult != null) {
                baseResult.getDataPoints().forEach((key, value) -> {
                    Map<AggregationEnum, Number> values = tm.getAggregationTypes().stream()
                            .collect(Collectors.toMap(Function.identity(),
                                    aggregation -> serverConnection.getTotalTimeseriesData(metric.getMetricId(), start, end, aggregation)
                                            .getDataPoints().get(key).entrySet().iterator().next().getValue(),
                                    (a, b) -> b, LinkedHashMap::new));

                    Measure measure = new Measure(baseResult.getEntities().get(key));
                    measure.setAggregation(baseResult.getAggregationType().getValue().toLowerCase());
                    measure.setUnit(caluclateUnit(baseResult, values));

                    measure.setAvg(getTotalValues(baseResult, values, AggregationEnum.AVG));
                    measure.setMin(getTotalValues(baseResult, values, AggregationEnum.MIN));
                    measure.setMax(getTotalValues(baseResult, values, AggregationEnum.MAX));
                    measure.setSum(getTotalValues(baseResult, values, AggregationEnum.SUM));
                    measure.setCount(getTotalValues(baseResult, values, AggregationEnum.COUNT));

                    value.entrySet().stream()
                            .filter(entry -> entry != null && entry.getValue() != null)
                            .forEach(entry -> {
                                long timestamp = entry.getKey();
                                Measurement m = new Measurement(timestamp,
                                        getAggregationValue(baseResult, aggregations, AggregationEnum.AVG, key, timestamp),
                                        getAggregationValue(baseResult, aggregations, AggregationEnum.MIN, key, timestamp),
                                        getAggregationValue(baseResult, aggregations, AggregationEnum.MAX, key, timestamp),
                                        getAggregationValue(baseResult, aggregations, AggregationEnum.SUM, key, timestamp),
                                        getAggregationValue(baseResult, aggregations, AggregationEnum.COUNT, key, timestamp));

                                measure.getMeasurements().add(m);
                            });
                    chartDashlet.getMeasures().add(measure);
                });
            }
            dashboardReport.addChartDashlet(chartDashlet);
        }
        PerfSigBuildAction action = new PerfSigBuildAction(Collections.singletonList(dashboardReport));
        run.addAction(action);
    }

    private String caluclateUnit(Result baseResult, Map<AggregationEnum, Number> values) {
        if (baseResult.getUnit().equals("Byte (B)")) {
            String tmp = DynatraceUtils.humanReadableByteCount(values.get(AggregationEnum.MAX).doubleValue(), false);
            return tmp.substring(tmp.length() - 3, tmp.length());
        }
        return baseResult.getUnit();
    }

    private Number getTotalValues(Result baseResult, Map<AggregationEnum, Number> values, AggregationEnum aggregation) {
        if (values.get(aggregation) == null) return 0;
        if (baseResult.getUnit().equals("Byte (B)")) {
            String tmp = DynatraceUtils.humanReadableByteCount(values.get(aggregation).doubleValue(), false);
            return Double.valueOf(tmp.substring(0, tmp.length() - 3));
        } else {
            return values.get(aggregation).doubleValue();
        }
    }

    private Number getAggregationValue(Result result, Map<AggregationEnum, Result> aggregations, AggregationEnum aggregation, String key, long timestamp) {
        if (aggregations.get(aggregation) == null) return 0;
        if (result.getUnit().equals("Byte (B)")) {
            String tmp = DynatraceUtils.humanReadableByteCount(aggregations.get(aggregation).getDataPoints().get(key).get(timestamp), false);
            return Double.valueOf(tmp.substring(0, tmp.length() - 3));
        } else {
            return aggregations.get(aggregation).getDataPoints().get(key).get(timestamp);
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public String getEnvId() {
        return envId;
    }

    public List<Metric> getMetrics() {
        if (metrics == null) {
            metrics = new ArrayList<>();
        }
        return metrics;
    }

    @Symbol("pullPerfSigReports")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            load();
        }

        public ListBoxModel doFillEnvIdItems() {
            return DynatraceUtils.listToListBoxModel(DynatraceUtils.getDynatraceConfigurations());
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.DynatraceRecorder_DisplayName();
        }
    }
}
