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

package de.tsystems.mms.apm.performancesignature.dynatrace;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.PerfSigTestData;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.tasks.junit.TestDataPublisher;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;
import hudson.util.ListBoxModel;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PerfSigTestDataPublisher extends TestDataPublisher {
    private final String dynatraceProfile;

    @DataBoundConstructor
    public PerfSigTestDataPublisher(final String dynatraceProfile) {
        this.dynatraceProfile = dynatraceProfile;
    }

    @Override
    public TestResultAction.Data contributeTestData(final Run<?, ?> run, @Nonnull final FilePath workspace, final Launcher launcher,
                                                    final TaskListener listener, final TestResult testResult) throws AbortException, RESTErrorException {
        PluginLogger logger = PerfSigUIUtils.createLogger(listener.getLogger());
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceProfile);

        List<TestRun> testRuns = new ArrayList<>();
        List<PerfSigEnvInvisAction> envVars = run.getActions(PerfSigEnvInvisAction.class);
        for (PerfSigEnvInvisAction registerEnvVars : envVars) {
            if (StringUtils.isNotBlank(registerEnvVars.getTestRunId())) {
                TestRun testRun = connection.getTestRun(registerEnvVars.getTestRunId());
                if (testRun == null || testRun.getTestResults() == null || testRun.getTestResults().isEmpty()) {
                    throw new RESTErrorException(Messages.PerfSigRecorder_XMLReportError());
                } else {
                    testRuns.add(testRun);
                    logger.log(Messages.PerfSigTestDataPublisher_XMLReportResults(testRun.getTestResults().size(), " " + testRun.getId()));
                }
            }
        }

        return new PerfSigTestData(testRuns);
    }

    public String getDynatraceProfile() {
        return dynatraceProfile;
    }

    @Extension
    public static final class PerfSigTestDataPublisherDescriptor extends Descriptor<TestDataPublisher> {

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillDynatraceProfileItems(@AncestorInPath Item item) {
            if (PerfSigUIUtils.checkForMissingPermission(item)) {
                return new ListBoxModel();
            }
            return PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.PerfSigTestDataPublisher_DisplayName();
        }
    }

}
