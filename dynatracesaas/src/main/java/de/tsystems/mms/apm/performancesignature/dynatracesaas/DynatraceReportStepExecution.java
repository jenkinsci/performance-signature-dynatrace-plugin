package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measurement;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Result;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.Timeseries;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import org.apache.commons.lang.time.DateUtils;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DynatraceReportStepExecution extends SynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 1L;
    private final transient DynatraceReportStep step;

    public DynatraceReportStepExecution(DynatraceReportStep dynatraceReportStep, StepContext context) {
        super(context);
        this.step = dynatraceReportStep;
    }

    public DynatraceReportStep getStep() {
        return step;
    }

    @Override
    protected Void run() throws Exception {
        Run<?, ?> run = getContext().get(Run.class);
        TaskListener listener = getContext().get(TaskListener.class);
        if (run == null || listener == null) {
            throw new IllegalStateException("pipeline step was called without run or task listener in context");
        }

        PluginLogger logger = PerfSigUIUtils.createLogger(listener.getLogger());
        DynatraceServerConnection serverConnection = DynatraceUtils.createDynatraceServerConnection(step.getEnvId(), true);
        logger.log("getting metric data from Dynatrace Server");

        Date start = DateUtils.addHours(new Date(), -2);
        Date end = new Date();

        Map<String, Timeseries> timeseries = serverConnection.getTimeseries()
                .parallelStream().collect(Collectors.toMap(Timeseries::getTimeseriesId, item -> item));

        DashboardReport dashboardReport = new DashboardReport("loadtest");
        step.getMetrics().forEach(metric -> {
            Timeseries tm = timeseries.get(metric.getMetricId());
            Map<Timeseries.AggregationEnum, Result> aggregations = tm.getAggregationTypes().stream()
                    .collect(Collectors.toMap(aggregation -> aggregation,
                            aggregation -> serverConnection.getTimeseriesData(metric.getMetricId(), start, end, aggregation),
                            (a, b) -> b, LinkedHashMap::new));
            Result baseResult = aggregations.get(Timeseries.AggregationEnum.AVG);
            ChartDashlet chartDashlet = new ChartDashlet();
            chartDashlet.setName(tm.getDetailedSource() + " - " + tm.getDisplayName());
            if (baseResult != null) {
                baseResult.getDataPoints().forEach((key, value) -> {
                    Map<Timeseries.AggregationEnum, Number> values = tm.getAggregationTypes().stream()
                            .collect(Collectors.toMap(Function.identity(),
                                    aggregation -> serverConnection.getTotalTimeseriesData(metric.getMetricId(), start, end, aggregation)
                                            .getDataPoints().get(key).entrySet().iterator().next().getValue(),
                                    (a, b) -> b, LinkedHashMap::new));

                    Measure measure = new Measure(baseResult.getEntities().get(key));
                    measure.setAggregation(baseResult.getAggregationType().getValue().toLowerCase());
                    measure.setUnit(caluclateUnit(baseResult, values));

                    measure.setAvg(getTotalValues(baseResult, values, Timeseries.AggregationEnum.AVG));
                    measure.setMin(getTotalValues(baseResult, values, Timeseries.AggregationEnum.MIN));
                    measure.setMax(getTotalValues(baseResult, values, Timeseries.AggregationEnum.MAX));
                    measure.setSum(getTotalValues(baseResult, values, Timeseries.AggregationEnum.SUM));
                    measure.setCount(getTotalValues(baseResult, values, Timeseries.AggregationEnum.COUNT));

                    value.entrySet().stream()
                            .filter(entry -> entry != null && entry.getValue() != null)
                            .forEach(entry -> {
                                long timestamp = entry.getKey();
                                Measurement m = new Measurement(timestamp,
                                        getAggregationValue(baseResult, aggregations, Timeseries.AggregationEnum.AVG, key, timestamp),
                                        getAggregationValue(baseResult, aggregations, Timeseries.AggregationEnum.MIN, key, timestamp),
                                        getAggregationValue(baseResult, aggregations, Timeseries.AggregationEnum.MAX, key, timestamp),
                                        getAggregationValue(baseResult, aggregations, Timeseries.AggregationEnum.SUM, key, timestamp),
                                        getAggregationValue(baseResult, aggregations, Timeseries.AggregationEnum.COUNT, key, timestamp));

                                measure.getMeasurements().add(m);
                            });
                    chartDashlet.getMeasures().add(measure);
                });
            }
            dashboardReport.addChartDashlet(chartDashlet);
        });
        PerfSigBuildAction action = new PerfSigBuildAction(Collections.singletonList(dashboardReport));
        run.addAction(action);
        return null;
    }

    private String caluclateUnit(Result baseResult, Map<Timeseries.AggregationEnum, Number> values) {
        if (baseResult.getUnit().equals("Byte (B)")) {
            String tmp = DynatraceUtils.humanReadableByteCount(values.get(Timeseries.AggregationEnum.MAX).doubleValue(), false);
            return tmp.replaceAll("[^a-zA-Z]", "");
        }
        return baseResult.getUnit();
    }

    private Number getTotalValues(Result baseResult, Map<Timeseries.AggregationEnum, Number> values, Timeseries.AggregationEnum aggregation) {
        if (values.get(aggregation) == null) return 0;
        if (baseResult.getUnit().equals("Byte (B)")) {
            String tmp = DynatraceUtils.humanReadableByteCount(values.get(aggregation).doubleValue(), false);
            return Double.valueOf(tmp.replaceAll("[^0-9.]", ""));
        } else {
            return values.get(aggregation).doubleValue();
        }
    }

    private Number getAggregationValue(Result result, Map<Timeseries.AggregationEnum, Result> aggregations, Timeseries.AggregationEnum aggregation, String key, long timestamp) {
        if (aggregations.get(aggregation) == null) return 0;
        if (result.getUnit().equals("Byte (B)")) {
            String tmp = DynatraceUtils.humanReadableByteCount(aggregations.get(aggregation).getDataPoints().get(key).get(timestamp), false);
            return Double.valueOf(tmp.substring(0, tmp.length() - 3));
        } else {
            return aggregations.get(aggregation).getDataPoints().get(key).get(timestamp);
        }
    }
}
