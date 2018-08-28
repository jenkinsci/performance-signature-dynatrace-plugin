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

package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.*;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.Specification;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.SpecificationTM;
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
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.MissingContextVariableException;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DynatraceReportStepExecution extends SynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DynatraceReportStepExecution.class.getName());
    private static final String DEFAULT_COLOR = "#006bba";
    private static final List<UnitEnum> BYTE_UNITS = Arrays.asList(
            UnitEnum.BYTEPERSECOND,
            UnitEnum.BYTEPERMINUTE,
            UnitEnum.BYTE);
    private static final List<UnitEnum> KILOBYTE_UNITS = Arrays.asList(
            UnitEnum.KILOBYTEPERSECOND,
            UnitEnum.KILOBYTEPERMINUTE,
            UnitEnum.KILOBYTE);
    private static final List<UnitEnum> TIME_UNITS = Arrays.asList(
            UnitEnum.NANOSECOND,
            UnitEnum.MICROSECOND);
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

        final List<DynatraceEnvInvisAction> envInvisActions = run.getActions(DynatraceEnvInvisAction.class);
        final List<DashboardReport> dashboardReports = new ArrayList<>();
        Specification spec = getSpecifications();

        envInvisActions.forEach(dynatraceAction -> {
            Long start = dynatraceAction.getTimeframeStart();
            Long end = dynatraceAction.getTimeframeStop();
            DashboardReport dashboardReport = new DashboardReport(dynatraceAction.getTestCase());

            //set url for Dynatrace dashboard
            DynatraceServerConfiguration configuration = serverConnection.getConfiguration();
            dashboardReport.setClientUrl(String.format("%s/#dashboard;gtf=c_%d_%d", configuration.getServerUrl(), start, end));

            //iterate over specified timeseries ids
            spec.getTimeseries().forEach(specTM -> {
                TimeseriesDefinition tm = timeseries.get(specTM.getTimeseriesId());
                //get data points for every possible aggregation
                Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> aggregations = tm.getAggregationTypes().parallelStream()
                        .collect(Collectors.toMap(
                                Function.identity(),
                                aggregation -> serverConnection.getTimeseriesData(specTM.getTimeseriesId(), start, end, aggregation, specTM.getEntityIds(), specTM.getTags()),
                                (a, b) -> b, LinkedHashMap::new)
                        );

                TimeseriesDataPointQueryResult baseResult = aggregations.get(AggregationTypeEnum.AVG);
                if (baseResult != null && MapUtils.isNotEmpty(baseResult.getDataPoints())) {
                    //get a scalar value for every possible aggregation
                    Map<AggregationTypeEnum, TimeseriesDataPointQueryResult> totalValues = tm.getAggregationTypes().parallelStream()
                            .collect(Collectors.toMap(Function.identity(),
                                    aggregation -> serverConnection.getTotalTimeseriesData(specTM.getTimeseriesId(), start, end, aggregation, specTM.getEntityIds(), specTM.getTags()),
                                    (a, b) -> b, LinkedHashMap::new));

                    //evaluate possible incidents
                    dashboardReport.getIncidents().addAll(evaluateSpecification(spec.getTolerateBound(), spec.getFrustrateBound(),
                            specTM, aggregations, timeseries));

                    ChartDashlet chartDashlet = new ChartDashlet();
                    chartDashlet.setName(tm.getDetailedSource() + " - " + tm.getDisplayName());

                    //create aggregated overall measure
                    Measure overallMeasure = new Measure("overall");
                    overallMeasure.setAggregation(translateAggregation(specTM.getAggregation()));
                    overallMeasure.setUnit(calculateUnitString(baseResult, getScalarValue(totalValues.get(AggregationTypeEnum.AVG))));
                    overallMeasure.setColor(DEFAULT_COLOR);

                    //calculate aggregated values from totalValues
                    overallMeasure.setAvg(getScalarValue(totalValues.get(AggregationTypeEnum.AVG)));
                    overallMeasure.setMin(getScalarValue(totalValues.get(AggregationTypeEnum.MIN)));
                    overallMeasure.setMax(getScalarValue(totalValues.get(AggregationTypeEnum.MAX)));
                    overallMeasure.setSum(getScalarValue(totalValues.get(AggregationTypeEnum.SUM)));
                    overallMeasure.setCount(getScalarValue(totalValues.get(AggregationTypeEnum.COUNT)));

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

                        Measure measure = new Measure(handleEntityIdString(baseResult.getEntities(), key));
                        measure.setAggregation(translateAggregation(specTM.getAggregation()));
                        measure.setUnit(calculateUnitString(baseResult, totalValuesPerDataPoint.get(AggregationTypeEnum.MAX)));
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
                    dashboardReport.addChartDashlet(chartDashlet);
                } else {
                    println(String.format("Timeseries %s has no data points", tm.getTimeseriesId()));
                }
            });
            dashboardReports.add(dashboardReport);

            PrintStream stream = Optional.ofNullable(DynatraceUtils.getTaskListener(getContext())).map(TaskListener::getLogger).orElseGet(() -> new PrintStream(System.out));
            PerfSigUIUtils.handleIncidents(run, dashboardReport.getIncidents(), PerfSigUIUtils.createLogger(stream), step.getNonFunctionalFailure());
        });
        println("created " + dashboardReports.size() + " DashboardReports");

        PerfSigBuildAction action = new PerfSigBuildAction(dashboardReports);
        run.addAction(action);
        return null;
    }

    private String handleEntityIdString(Map<String, String> entities, String entityId) {
        if (StringUtils.isBlank(entityId) || MapUtils.isEmpty(entities)) return null;

        String cleanedEntityId = entityId.split(",")[0];
        return entities.get(cleanedEntityId);
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
        if (dataPointQueryResult != null && MapUtils.isNotEmpty(dataPointQueryResult.getDataPoints())) {
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
                return "Minimum";
            case MAX:
                return "Maximum";
            case AVG:
                return "Average";
            default:
                return StringUtils.capitalize(aggregation.getValue().toLowerCase());
        }
    }

    private List<Alert> evaluateSpecification(double globalTolerateBound, double globalFrustrateBound, SpecificationTM specTM, Map<AggregationTypeEnum,
            TimeseriesDataPointQueryResult> aggregations, Map<String, TimeseriesDefinition> timeseries) {

        List<Alert> alerts = new ArrayList<>();
        double tolerateBound = Optional.ofNullable(specTM.getTolerateBound()).orElse(globalTolerateBound);
        double frustrateBound = Optional.ofNullable(specTM.getFrustrateBound()).orElse(globalFrustrateBound);
        TimeseriesDataPointQueryResult result = aggregations.get(specTM.getAggregation());
        if (specTM.getAggregation() == null || result == null) return alerts;

        for (Map.Entry<String, Map<Long, Double>> entry : result.getDataPoints().entrySet()) {
            String entity = handleEntityIdString(result.getEntities(), entry.getKey());

            for (Map.Entry<Long, Double> e : entry.getValue().entrySet()) {
                Long timestamp = e.getKey();
                Double value = e.getValue();
                if (value != null) {
                    if (tolerateBound < frustrateBound) {
                        if (value > tolerateBound && value < frustrateBound) {
                            String rule = timeseries.get(result.getTimeseriesId()).getDetailedSource() + " - " + timeseries.get(result.getTimeseriesId()).getDisplayName();
                            alerts.add(new Alert(Alert.SeverityEnum.WARNING,
                                    String.format("SpecFile threshold violation: %s upper tolerate bound exceeded", rule),
                                    String.format("%s: Measured peak value: %.2f %s on Entity: %s, Upper Bound: %.2f %s",
                                            rule, value, result.getUnit(), entity, tolerateBound, result.getUnit()),
                                    timestamp, rule));
                        } else if (value > frustrateBound) {
                            String rule = timeseries.get(result.getTimeseriesId()).getDetailedSource() + " - " + timeseries.get(result.getTimeseriesId()).getDisplayName();
                            alerts.add(new Alert(Alert.SeverityEnum.SEVERE,
                                    String.format("SpecFile threshold violation: %s upper frustrate bound exceeded", rule),
                                    String.format("%s: Measured peak value: %.2f %s on Entity: %s, Upper Bound: %.2f %s",
                                            rule, value, result.getUnit(), entity, frustrateBound, result.getUnit()),
                                    timestamp, rule));
                        }
                    } else {
                        if (value < tolerateBound && value > frustrateBound) {
                            String rule = timeseries.get(result.getTimeseriesId()).getDetailedSource() + " - " + timeseries.get(result.getTimeseriesId()).getDisplayName();
                            alerts.add(new Alert(Alert.SeverityEnum.WARNING,
                                    String.format("SpecFile threshold violation: %s lower tolerate bound exceeded", rule),
                                    String.format("%s: Measured peak value: %.2f %s on Entity: %s, Lower Bound: %.2f %s",
                                            rule, value, result.getUnit(), entity, tolerateBound, result.getUnit()),
                                    timestamp, rule));
                        } else {
                            if (value < frustrateBound) {
                                String rule = timeseries.get(result.getTimeseriesId()).getDetailedSource() + " - " + timeseries.get(result.getTimeseriesId()).getDisplayName();
                                alerts.add(new Alert(Alert.SeverityEnum.SEVERE,
                                        String.format("SpecFile threshold violation: %s lower frustrate bound exceeded", rule),
                                        String.format("%s: Measured peak value: %.2f %s on Entity: %s, Lower Bound: %.2f %s",
                                                rule, value, result.getUnit(), entity, frustrateBound, result.getUnit()),
                                        timestamp, rule));
                            }
                        }
                    }
                }
            }
        }
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

    private String calculateUnitString(TimeseriesDataPointQueryResult baseResult, Double maxValue) {
        if (BYTE_UNITS.contains(baseResult.getUnit())) {
            String tmp = DynatraceUtils.humanReadableByteCount(maxValue, false);
            return tmp.replaceAll("[^a-zA-Z]", "");
        } else if (KILOBYTE_UNITS.contains(baseResult.getUnit())) {
            String tmp = DynatraceUtils.humanReadableByteCount(maxValue * 1000, false);
            return tmp.replaceAll("[^a-zA-Z]", "");
        }
        return baseResult.getUnit().getValue();
    }

    private Number getTotalValues(TimeseriesDataPointQueryResult baseResult, Double value) {
        if (value == null) return 0;
        if (BYTE_UNITS.contains(baseResult.getUnit())) {
            String tmp = DynatraceUtils.humanReadableByteCount(value, false);
            return Double.valueOf(tmp.replaceAll("[^0-9.]", ""));
        } else if (KILOBYTE_UNITS.contains(baseResult.getUnit())) {
            String tmp = DynatraceUtils.humanReadableByteCount(value * 1000, false);
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
