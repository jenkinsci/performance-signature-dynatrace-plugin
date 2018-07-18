package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.ChartDashlet;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.DashboardReport;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measure;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Measurement;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.AggregationTypeEnum;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.TimeseriesDataPointQueryResult;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.TimeseriesDefinition;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.UnitEnum;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DynatraceReportStepExecution extends SynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DynatraceReportStepExecution.class.getName());
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

        DynatraceServerConnection serverConnection = DynatraceUtils.createDynatraceServerConnection(step.getEnvId(), true);
        println("getting metric data from Dynatrace Server");

        Map<String, TimeseriesDefinition> timeseries = serverConnection.getTimeseries()
                .parallelStream().collect(Collectors.toMap(TimeseriesDefinition::getTimeseriesId, item -> item));

        final List<DynatraceEnvInvisAction> envVars = run.getActions(DynatraceEnvInvisAction.class);
        final List<DashboardReport> dashboardReports = new ArrayList<>();
        envVars.forEach(dynatraceAction -> {
            //ToDo: remove this magic number before release
            Long start = dynatraceAction.getTimeframeStart() - 7200000;
            Long end = dynatraceAction.getTimeframeStop();

            DashboardReport dashboardReport = new DashboardReport(dynatraceAction.getTestCase());
            step.getMetrics().forEach(metric -> {
                TimeseriesDefinition tm = timeseries.get(metric.getMetricId());
                Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> aggregations = tm.getAggregationTypes().parallelStream()
                        .collect(Collectors.toMap(aggregation -> aggregation,
                                aggregation -> serverConnection.getTimeseriesData(metric.getMetricId(), start, end, aggregation),
                                (a, b) -> b, LinkedHashMap::new));
                TimeseriesDataPointQueryResult baseResult = aggregations.get(AggregationTypeEnum.AVG);
                ChartDashlet chartDashlet = new ChartDashlet();
                chartDashlet.setName(tm.getDetailedSource() + " - " + tm.getDisplayName());
                if (baseResult != null) {
                    baseResult.getDataPoints().forEach((key, value) -> {
                        Map<AggregationTypeEnum, Number> values = tm.getAggregationTypes().parallelStream()
                                .collect(Collectors.toMap(Function.identity(),
                                        aggregation -> serverConnection.getTotalTimeseriesData(metric.getMetricId(), start, end, aggregation)
                                                .getDataPoints().get(key).entrySet().iterator().next().getValue(),
                                        (a, b) -> b, LinkedHashMap::new));

                        Measure measure = new Measure(baseResult.getEntities().get(key));
                        measure.setAggregation(baseResult.getAggregationType().getValue().toLowerCase());
                        measure.setUnit(caluclateUnit(baseResult, values));

                        measure.setAvg(getTotalValues(baseResult, values, AggregationTypeEnum.AVG));
                        measure.setMin(getTotalValues(baseResult, values, AggregationTypeEnum.MIN));
                        measure.setMax(getTotalValues(baseResult, values, AggregationTypeEnum.MAX));
                        measure.setSum(getTotalValues(baseResult, values, AggregationTypeEnum.SUM));
                        measure.setCount(getTotalValues(baseResult, values, AggregationTypeEnum.COUNT));

                        value.entrySet().stream()
                                .filter(entry -> entry != null && entry.getValue() != null)
                                .forEach(entry -> {
                                    long timestamp = entry.getKey();
                                    Measurement m = new Measurement(timestamp,
                                            getAggregationValue(baseResult, aggregations, AggregationTypeEnum.AVG, key, timestamp),
                                            getAggregationValue(baseResult, aggregations, AggregationTypeEnum.MIN, key, timestamp),
                                            getAggregationValue(baseResult, aggregations, AggregationTypeEnum.MAX, key, timestamp),
                                            getAggregationValue(baseResult, aggregations, AggregationTypeEnum.SUM, key, timestamp),
                                            getAggregationValue(baseResult, aggregations, AggregationTypeEnum.COUNT, key, timestamp));

                                    measure.getMeasurements().add(m);
                                });
                        chartDashlet.getMeasures().add(measure);
                    });
                }
                dashboardReport.addChartDashlet(chartDashlet);
            });
            dashboardReports.add(dashboardReport);
        });
        println("created " + dashboardReports.size() + " DashboardReports");

        PerfSigBuildAction action = new PerfSigBuildAction(dashboardReports);
        run.addAction(action);
        return null;
    }

    private String caluclateUnit(TimeseriesDataPointQueryResult baseResult, Map<AggregationTypeEnum, Number> values) {
        if (baseResult.getUnit() == UnitEnum.BYTEPERMINUTE) {
            String tmp = DynatraceUtils.humanReadableByteCount(values.get(AggregationTypeEnum.MAX).doubleValue(), false);
            return tmp.replaceAll("[^a-zA-Z]", "");
        }
        return baseResult.getUnit().getValue();
    }

    private Number getTotalValues(TimeseriesDataPointQueryResult baseResult, Map<AggregationTypeEnum, Number> values, AggregationTypeEnum aggregation) {
        if (values.get(aggregation) == null) return 0;
        if (baseResult.getUnit() == UnitEnum.BYTEPERMINUTE) {
            String tmp = DynatraceUtils.humanReadableByteCount(values.get(aggregation).doubleValue(), false);
            return Double.valueOf(tmp.replaceAll("[^0-9.]", ""));
        } else {
            return values.get(aggregation).doubleValue();
        }
    }

    private Number getAggregationValue(TimeseriesDataPointQueryResult result, Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> aggregations, AggregationTypeEnum aggregation, String key, long timestamp) {
        if (aggregations.get(aggregation) == null) return 0;
        if (result.getUnit() == UnitEnum.BYTEPERMINUTE) {
            String tmp = DynatraceUtils.humanReadableByteCount(aggregations.get(aggregation).getDataPoints().get(key).get(timestamp), false);
            return Double.valueOf(tmp.substring(0, tmp.length() - 3));
        } else {
            return aggregations.get(aggregation).getDataPoints().get(key).get(timestamp);
        }
    }

    private void println(String message) {
        TaskListener listener = DynatraceUtils.getTaskListener(getContext());
        if (listener == null) {
            LOGGER.log(Level.FINE, "failed to print message {0} due to null TaskListener", message);
        } else {
            PerfSigUIUtils.createLogger(listener.getLogger()).log(message);
        }
    }
}
