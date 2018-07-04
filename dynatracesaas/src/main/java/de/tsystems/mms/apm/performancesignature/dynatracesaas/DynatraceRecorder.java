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
        DynatraceServerConnection serverConnection = DynatraceUtils.createJenkinsServerConnection(envId);
        logger.log("getting metric data from Dynatrace Server");
        List<Result> results = new ArrayList<>();

        Date start = DateUtils.addHours(new Date(), -2);
        Date end = new Date();

        results.add(serverConnection.getTimeseriesData("com.dynatrace.builtin:host.cpu.user", start, end, Timeseries.AggregationEnum.AVG));
        for (Metric metric : metrics) {
            serverConnection.getTimeseriesData(metric.getMetricId(), start, end, Timeseries.AggregationEnum.AVG);
        }
        DashboardReport dashboardReport = new DashboardReport("loadtest");
        for (Result result : results) {
            ChartDashlet chartDashlet = new ChartDashlet();
            chartDashlet.setName(result.getTimeseriesId());
            for (Map.Entry<String, Map<Long, Double>> entity : result.getDataPoints().entrySet()) {
                System.out.println(entity.getKey());
                Measure measure = new Measure(entity.getKey());
                for (Map.Entry<Long, Double> entry : entity.getValue().entrySet()) {
                    System.out.println(entry);
                    if (entry != null && entry.getValue() != null) {
                        measure.getMeasurements().add(new Measurement(entry));
                    }
                }
                chartDashlet.getMeasures().add(measure);
            }
            dashboardReport.addChartDashlet(chartDashlet);
        }

        PerfSigBuildAction action = new PerfSigBuildAction(Collections.singletonList(dashboardReport));
        run.addAction(action);
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

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.DynatraceRecorder_DisplayName();
        }
    }
}
