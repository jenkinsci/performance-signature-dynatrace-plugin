package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.*;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.Specification;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.AggregationTypeEnum;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.TimeseriesDataPointQueryResult;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.TimeseriesDefinition;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.UnitEnum;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.MissingContextVariableException;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
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
    private FilePath ws;

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
        ws = getContext().get(FilePath.class);

        if (run == null || listener == null) {
            throw new IllegalStateException("pipeline step was called without run or task listener in context");
        }
        if (StringUtils.isNotBlank(step.getSpecFile()) && CollectionUtils.isNotEmpty(step.getMetrics())) {
            throw new IllegalArgumentException("At most one of file or text must be provided to " + step.getDescriptor().getFunctionName());
        }
        if (ws == null && StringUtils.isNotBlank(step.getSpecFile())) {
            throw new MissingContextVariableException(FilePath.class);
        }

        DynatraceServerConnection serverConnection = DynatraceUtils.createDynatraceServerConnection(step.getEnvId(), true);
        println("getting metric data from Dynatrace Server");

        Map<String, TimeseriesDefinition> timeseries = serverConnection.getTimeseries()
                .parallelStream().collect(Collectors.toMap(TimeseriesDefinition::getTimeseriesId, item -> item));

        final List<DynatraceEnvInvisAction> envVars = run.getActions(DynatraceEnvInvisAction.class);
        final List<DashboardReport> dashboardReports = new ArrayList<>();
        List<Specification> specifications = getSpecifications();
        envVars.forEach(dynatraceAction -> {
            Long start = dynatraceAction.getTimeframeStart() - 7200000; //ToDo: remove this magic number before release
            Long end = dynatraceAction.getTimeframeStop();
            DashboardReport dashboardReport = new DashboardReport(dynatraceAction.getTestCase());

            specifications.forEach(spec -> {
                TimeseriesDefinition tm = timeseries.get(spec.getTimeseriesId());
                Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> aggregations = tm.getAggregationTypes().parallelStream()
                        .collect(Collectors.toMap(Function.identity(),
                                aggregation -> serverConnection.getTimeseriesData(spec.getTimeseriesId(), start, end, aggregation),
                                (a, b) -> b, LinkedHashMap::new));

                TimeseriesDataPointQueryResult baseResult = aggregations.get(AggregationTypeEnum.AVG);
                Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> totalValues = tm.getAggregationTypes().parallelStream()
                        .collect(Collectors.toMap(Function.identity(),
                                aggregation -> serverConnection.getTotalTimeseriesData(spec.getTimeseriesId(), start, end, aggregation),
                                (a, b) -> b, LinkedHashMap::new));

                dashboardReport.getIncidents().addAll(evaluateSpecification(spec, totalValues, timeseries, dynatraceAction));
                ChartDashlet chartDashlet = new ChartDashlet();
                chartDashlet.setName(tm.getDetailedSource() + " - " + tm.getDisplayName());

                if (baseResult != null) {
                    baseResult.getDataPoints().forEach((key, value) -> {
                        Map<AggregationTypeEnum, Double> totalValuesPerDataPoint = tm.getAggregationTypes().stream()
                                .collect(Collectors.toMap(Function.identity(),
                                        aggregation -> totalValues.get(aggregation).getDataPoints().get(key).entrySet().iterator().next().getValue(),
                                        (a, b) -> b, LinkedHashMap::new));

                        Measure measure = new Measure(baseResult.getEntities().get(key));
                        measure.setAggregation(baseResult.getAggregationType().getValue().toLowerCase());
                        measure.setUnit(caluclateUnit(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.MAX)));
                        measure.setColor("#006bba");

                        measure.setAvg(getTotalValues(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.AVG)));
                        measure.setMin(getTotalValues(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.MIN)));
                        measure.setMax(getTotalValues(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.MAX)));
                        measure.setSum(getTotalValues(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.SUM)));
                        measure.setCount(getTotalValues(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.COUNT)));

                        value.entrySet().parallelStream()
                                .filter(entry -> entry != null && entry.getValue() != null)
                                .forEach(entry -> {
                                    Measurement m = new Measurement(entry.getKey(),
                                            getAggregationValue(baseResult, aggregations.get(AggregationTypeEnum.AVG), key, entry.getKey()),
                                            getAggregationValue(baseResult, aggregations.get(AggregationTypeEnum.MIN), key, entry.getKey()),
                                            getAggregationValue(baseResult, aggregations.get(AggregationTypeEnum.MAX), key, entry.getKey()),
                                            getAggregationValue(baseResult, aggregations.get(AggregationTypeEnum.SUM), key, entry.getKey()),
                                            getAggregationValue(baseResult, aggregations.get(AggregationTypeEnum.COUNT), key, entry.getKey()));

                                    measure.getMeasurements().add(m);
                                });
                        chartDashlet.getMeasures().add(measure);
                    });
                }
                dashboardReport.addChartDashlet(chartDashlet);
            });
            dashboardReports.add(dashboardReport);

            PerfSigUIUtils.handleIncidents(run, dashboardReport.getIncidents(),
                    PerfSigUIUtils.createLogger(DynatraceUtils.getTaskListener(getContext()).getLogger()), 0);
        });
        println("created " + dashboardReports.size() + " DashboardReports");

        PerfSigBuildAction action = new PerfSigBuildAction(dashboardReports);
        run.addAction(action);
        return null;
    }

    private List<Alert> evaluateSpecification(Specification spec, Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> totalValues, Map<String,
            TimeseriesDefinition> timeseries, DynatraceEnvInvisAction dynatraceAction) {
        List<Alert> alerts = new ArrayList<>();
        TimeseriesDataPointQueryResult result = totalValues.get(spec.getAggregation());
        if (spec.getAggregation() == null || spec.getThreshold() == null || result == null) return alerts;

        result.getDataPoints().forEach((entity, value) -> {
            Double actualValue = value.entrySet().iterator().next().getValue();
            if (spec.getAggregation() != AggregationTypeEnum.MIN && actualValue > spec.getThreshold()) {
                String rule = timeseries.get(result.getTimeseriesId()).getDetailedSource() + " - " + timeseries.get(result.getTimeseriesId()).getDisplayName();
                alerts.add(new Alert(Alert.SeverityEnum.WARNING,
                        "SpecFile threshold violation: " + rule + " upper bound exceeded",
                        String.format("%s: Measured peak value: %.2f %s, Upper Bound: %.2f", rule, actualValue, result.getUnit(), spec.getThreshold()),
                        dynatraceAction.getTimeframeStart(), dynatraceAction.getTimeframeStop(), rule));
            }
        });
        return alerts;
    }

    private List<Specification> getSpecifications() throws IOException, InterruptedException {
        List<Specification> specs = new ArrayList<>();
        if (ws != null && StringUtils.isNotBlank(step.getSpecFile())) {
            FilePath f = ws.child(step.getSpecFile());
            if (f.exists() && !f.isDirectory()) {
                try (InputStream is = f.read()) {
                    Type type = new TypeToken<List<Specification>>() {
                    }.getType();
                    specs = new Gson().fromJson(IOUtils.toString(is, StandardCharsets.UTF_8), type);
                }
            } else if (f.isDirectory()) {
                throw new IllegalArgumentException(f.getRemote() + "  is a directory ...");
            } else if (!f.exists()) {
                throw new FileNotFoundException(f.getRemote() + " does not exist ...");
            }
        } else {
            specs = step.getMetrics().stream().map(metric -> new Specification(metric.getMetricId())).collect(Collectors.toList());
        }
        return specs;
    }

    private String caluclateUnit(TimeseriesDataPointQueryResult baseResult, Double maxValue) {
        if (baseResult.getUnit() == UnitEnum.BYTEPERMINUTE) {
            String tmp = DynatraceUtils.humanReadableByteCount(maxValue, false);
            return tmp.replaceAll("[^a-zA-Z]", "");
        }
        return baseResult.getUnit().getValue();
    }

    private Number getTotalValues(TimeseriesDataPointQueryResult baseResult, Double value) {
        if (value == null) return 0;
        if (baseResult.getUnit() == UnitEnum.BYTEPERMINUTE) {
            String tmp = DynatraceUtils.humanReadableByteCount(value, false);
            return Double.valueOf(tmp.replaceAll("[^0-9.]", ""));
        } else {
            return value;
        }
    }

    private Number getAggregationValue(TimeseriesDataPointQueryResult result, TimeseriesDataPointQueryResult value,
                                       String key, Long timestamp) {
        if (value == null) return 0;
        return getTotalValues(result, value.getDataPoints().get(key).get(timestamp));
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
