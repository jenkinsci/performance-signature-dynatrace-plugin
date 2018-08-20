package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.*;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.Specification;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.SpecificationTM;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.*;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.PerfSigBuildAction;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.EnvVars;
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
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.tsystems.mms.apm.performancesignature.dynatracesaas.CreateDeploymentStepExecution.BUILD_VAR_KEY_DEPLOYMENT_PROJECT;
import static de.tsystems.mms.apm.performancesignature.dynatracesaas.CreateDeploymentStepExecution.BUILD_VAR_KEY_DEPLOYMENT_VERSION;
import static de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection.BUILD_URL_ENV_PROPERTY;

public class DynatraceReportStepExecution extends SynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DynatraceReportStepExecution.class.getName());
    private static final String DEFAULT_COLOR = "#006bba";
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
        EnvVars envVars = getContext().get(EnvVars.class);
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

        println("creating Performance Signature custom event");
        EventPushMessage event = new EventPushMessage(EventTypeEnum.CUSTOM_INFO, null)
                .setSource("Jenkins")
                .setTitle("Performance Signature was executed");
        if (envVars != null) {
            event.setDeploymentName(envVars.get("JOB_NAME"))
                    .setDeploymentVersion(Optional.ofNullable(envVars.get(BUILD_VAR_KEY_DEPLOYMENT_VERSION)).orElse(" "))
                    .setDeploymentProject(envVars.get(BUILD_VAR_KEY_DEPLOYMENT_PROJECT))
                    .setDescription(envVars.get(BUILD_URL_ENV_PROPERTY))
                    .setCiBackLink(envVars.get(BUILD_URL_ENV_PROPERTY))
                    .addCustomProperties("Jenkins Build Number", envVars.get("BUILD_ID"))
                    .addCustomProperties("Git Commit", envVars.get("GIT_COMMIT"));
        }
        //serverConnection.createEvent(event);
        println("getting metric data from Dynatrace Server");

        Map<String, TimeseriesDefinition> timeseries = serverConnection.getTimeseries()
                .parallelStream().collect(Collectors.toMap(TimeseriesDefinition::getTimeseriesId, item -> item));

        final List<DynatraceEnvInvisAction> envInvisActions = run.getActions(DynatraceEnvInvisAction.class);
        final List<DashboardReport> dashboardReports = new ArrayList<>();

        List<SpecificationTM> specifications = getSpecifications().getTimeseries();
        envInvisActions.forEach(dynatraceAction -> {
            Long start = dynatraceAction.getTimeframeStart() - 7200000; //ToDo: remove this magic number before release
            Long end = dynatraceAction.getTimeframeStop();
            DashboardReport dashboardReport = new DashboardReport(dynatraceAction.getTestCase());

            //set url for Dynatrace dashboard
            DynatraceServerConfiguration configuration = serverConnection.getConfiguration();
            dashboardReport.setClientUrl(String.format("%s/#dashboard;gtf=c_%d_%d", configuration.getServerUrl(), start, end));

            //iterate over specified timeseries ids
            specifications.forEach(spec -> {
                TimeseriesDefinition tm = timeseries.get(spec.getTimeseriesId());
                //get data points for every possible aggregation
                Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> aggregations = tm.getAggregationTypes().parallelStream()
                        .collect(Collectors.toMap(Function.identity(),
                                aggregation -> serverConnection.getTimeseriesData(spec.getTimeseriesId(), start, end, aggregation),
                                (a, b) -> b, LinkedHashMap::new));

                TimeseriesDataPointQueryResult baseResult = aggregations.get(AggregationTypeEnum.AVG);

                //get a scalar value for every possible aggregation
                Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> totalValues = tm.getAggregationTypes().parallelStream()
                        .collect(Collectors.toMap(Function.identity(),
                                aggregation -> serverConnection.getTotalTimeseriesData(spec.getTimeseriesId(), start, end, aggregation),
                                (a, b) -> b, LinkedHashMap::new));

                //evaluate possible incidents
                dashboardReport.getIncidents().addAll(evaluateSpecification(spec, totalValues, timeseries, dynatraceAction));
                ChartDashlet chartDashlet = new ChartDashlet();
                chartDashlet.setName(tm.getDetailedSource() + " - " + tm.getDisplayName());

                if (baseResult != null && baseResult.getDataPoints() != null && !baseResult.getDataPoints().isEmpty()) {
                    //create aggregated overall measure
                    Measure overallMeasure = new Measure("overall");
                    overallMeasure.setAggregation(translateAggregation(spec.getAggregation()));
                    overallMeasure.setColor(DEFAULT_COLOR);

                    //calculate aggregated values from totalValues
                    overallMeasure.setAvg(getScalarValue(totalValues.get(AggregationTypeEnum.AVG)));
                    overallMeasure.setMin(getScalarValue(totalValues.get(AggregationTypeEnum.MIN)));
                    overallMeasure.setMax(getScalarValue(totalValues.get(AggregationTypeEnum.MAX)));
                    overallMeasure.setSum(getScalarValue(totalValues.get(AggregationTypeEnum.SUM)));
                    overallMeasure.setCount(getScalarValue(totalValues.get(AggregationTypeEnum.COUNT)));
                    overallMeasure.setUnit(caluclateUnit(baseResult, overallMeasure.getMax()));

                    //calculate aggregated values from seriesValues
                    Map<AggregationTypeEnum, Map<Long, Double>> scalarValues = getScalarValues(aggregations);
                    baseResult.getDataPoints().values().iterator().next().keySet().forEach(entry -> {
                        Measurement m = new Measurement(entry,
                                scalarValues.getOrDefault(AggregationTypeEnum.AVG, new LinkedHashMap<>()).getOrDefault(entry, 0D),
                                scalarValues.getOrDefault(AggregationTypeEnum.MIN, new LinkedHashMap<>()).getOrDefault(entry, 0D),
                                scalarValues.getOrDefault(AggregationTypeEnum.MAX, new LinkedHashMap<>()).getOrDefault(entry, 0D),
                                scalarValues.getOrDefault(AggregationTypeEnum.SUM, new LinkedHashMap<>()).getOrDefault(entry, 0D),
                                scalarValues.getOrDefault(AggregationTypeEnum.COUNT, new LinkedHashMap<>()).getOrDefault(entry, 0D)
                        );
                        overallMeasure.getMeasurements().add(m);
                    });
                    chartDashlet.getMeasures().add(overallMeasure);

                    //iterate over every entityId
                    baseResult.getDataPoints().forEach((key, value) -> {
                        Map<AggregationTypeEnum, Double> totalValuesPerDataPoint = tm.getAggregationTypes().stream()
                                .collect(Collectors.toMap(Function.identity(),
                                        aggregation -> totalValues.get(aggregation).getDataPoints().get(key).entrySet().iterator().next().getValue(),
                                        (a, b) -> b, LinkedHashMap::new));

                        Measure measure = new Measure(baseResult.getEntities().get(key));
                        measure.setAggregation(translateAggregation(spec.getAggregation()));
                        measure.setUnit(caluclateUnit(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.MAX)));
                        measure.setColor(DEFAULT_COLOR);

                        measure.setAvg(getTotalValues(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.AVG)));
                        measure.setMin(getTotalValues(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.MIN)));
                        measure.setMax(getTotalValues(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.MAX)));
                        measure.setSum(getTotalValues(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.SUM)));
                        measure.setCount(getTotalValues(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.COUNT)));

                        value.entrySet().parallelStream()
                                .filter(entry -> entry.getValue() != null)
                                .forEach(entry -> {
                                    Measurement m = new Measurement(entry.getKey(),
                                            getAggregationValue(baseResult, aggregations.get(AggregationTypeEnum.AVG), key, entry.getKey()),
                                            getAggregationValue(baseResult, aggregations.get(AggregationTypeEnum.MIN), key, entry.getKey()),
                                            getAggregationValue(baseResult, aggregations.get(AggregationTypeEnum.MAX), key, entry.getKey()),
                                            getAggregationValue(baseResult, aggregations.get(AggregationTypeEnum.SUM), key, entry.getKey()),
                                            getAggregationValue(baseResult, aggregations.get(AggregationTypeEnum.COUNT), key, entry.getKey())
                                    );
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

    private Map<AggregationTypeEnum, Map<Long, Double>> getScalarValues(Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> dataPointQueryResultMap) {
        Map<AggregationTypeEnum, Map<Long, Double>> hashMap = new LinkedHashMap<>();

        dataPointQueryResultMap.forEach((key, value) -> {
            Map<String, Map<Long, Double>> dataPoints = value.getDataPoints();
            if (dataPoints != null) {
                Map<Long, Double> aggregatedValues = dataPoints.values().stream()
                        .flatMap(test -> test.entrySet().stream())
                        .filter(longDoubleEntry -> longDoubleEntry.getValue() != null)
                        .collect(
                                Collectors.groupingBy(
                                        Map.Entry::getKey,
                                        Collectors.averagingDouble(Map.Entry::getValue)
                                )
                        );
                hashMap.put(key, aggregatedValues);
            }
        });
        return hashMap;
    }

    private double getScalarValue(TimeseriesDataPointQueryResult dataPointQueryResult) {
        if (dataPointQueryResult != null && dataPointQueryResult.getDataPoints() != null) {
            OptionalDouble average = dataPointQueryResult.getDataPoints().values().stream()
                    .flatMap(values -> values.values().stream())
                    .mapToDouble(a -> a).average();
            if (average.isPresent()) return average.getAsDouble();
        }
        return 0;
    }

    private String translateAggregation(AggregationTypeEnum aggregation) {
        switch (aggregation) {
            case MIN:
                return "minimum";
            case MAX:
                return "maximum";
            case AVG:
                return "average";
            default:
                return aggregation.getValue().toLowerCase();
        }
    }

    private List<Alert> evaluateSpecification(SpecificationTM spec, Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> totalValues, Map<String,
            TimeseriesDefinition> timeseries, DynatraceEnvInvisAction dynatraceAction) {
        List<Alert> alerts = new ArrayList<>();
        TimeseriesDataPointQueryResult result = totalValues.get(spec.getAggregation());
        if (spec.getAggregation() == null || result == null) return alerts;

        result.getDataPoints().forEach((entity, value) -> {
            Double actualValue = value.entrySet().iterator().next().getValue();
            if (spec.getAggregation() != AggregationTypeEnum.MIN && actualValue > spec.getTolerateBound()) {
                String rule = timeseries.get(result.getTimeseriesId()).getDetailedSource() + " - " + timeseries.get(result.getTimeseriesId()).getDisplayName();
                alerts.add(new Alert(Alert.SeverityEnum.WARNING,
                        "SpecFile threshold violation: " + rule + " upper bound exceeded",
                        String.format("%s: Measured peak value: %.2f %s, Upper Bound: %.2f", rule, actualValue, result.getUnit(), spec.getTolerateBound()),
                        dynatraceAction.getTimeframeStart(), dynatraceAction.getTimeframeStop(), rule));
            }
        });
        return alerts;
    }

    private Specification getSpecifications() throws IOException, InterruptedException {
        Specification specification = new Specification();
        if (ws != null && StringUtils.isNotBlank(step.getSpecFile())) {
            FilePath f = ws.child(step.getSpecFile());
            if (f.exists() && !f.isDirectory()) {
                try (InputStream is = f.read()) {
                    Type type = new TypeToken<Specification>() {
                    }.getType();
                    return new Gson().fromJson(IOUtils.toString(is, StandardCharsets.UTF_8), type);
                }
            } else if (f.isDirectory()) {
                throw new IllegalArgumentException(f.getRemote() + "  is a directory ...");
            } else if (!f.exists()) {
                throw new FileNotFoundException(f.getRemote() + " does not exist ...");
            }
            return specification;
        } else {
            specification.setTimeseries(step.getMetrics().stream().map(metric -> new SpecificationTM(metric.getMetricId())).collect(Collectors.toList()));
            return specification;
        }
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
