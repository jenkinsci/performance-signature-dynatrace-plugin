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

import de.tsystems.mms.apm.performancesignature.dynatrace.model.TestRun;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class PerfSigStopRecording extends Builder implements SimpleBuildStep {
    private final String dynatraceProfile;

    @DataBoundConstructor
    public PerfSigStopRecording(final String dynatraceProfile) {
        this.dynatraceProfile = dynatraceProfile;
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws IOException {
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceProfile);
        final List<PerfSigEnvInvisAction> actions = run.getActions(PerfSigEnvInvisAction.class);

        PerfSigEnvInvisAction perfSigAction = null;
        String sessionId;
        String testRunId = null;
        Date timeframeStop = new Date();
        PluginLogger logger = PerfSigUIUtils.createLogger(listener.getLogger());

        logger.log(Messages.PerfSigStopRecording_StoppingSessionRecording());
        if (!actions.isEmpty()) {
            perfSigAction = actions.get(actions.size() - 1); //get the last PerfSigAction
            perfSigAction.setTimeframeStop(timeframeStop);
            testRunId = perfSigAction.getTestRunId();
        }

        if (testRunId != null) {
            TestRun testRun = connection.finishTestRun(testRunId);
            logger.log("finished test run " + testRun.getId());
        }

        if (perfSigAction != null && perfSigAction.isContinuousRecording()) {
            Date timeframeStart = perfSigAction.getTimeframeStart();
            logger.log(Messages.PerfSigStopRecording_TimeframeStart(timeframeStart));
            logger.log(Messages.PerfSigStopRecording_TimeframeStop(timeframeStop));
            sessionId = connection.storeSession(perfSigAction.getSessionName(), timeframeStart, timeframeStop,
                    PerfSigStartRecording.DescriptorImpl.defaultRecordingOption, PerfSigStartRecording.DescriptorImpl.defaultLockSession, false);
            perfSigAction.setSessionId(sessionId);
        } else {
            sessionId = connection.stopRecording();
        }

        if (StringUtils.isBlank(sessionId)) {
            throw new RESTErrorException(Messages.PerfSigStopRecording_InternalError());
        }
        logger.log(Messages.PerfSigStopRecording_StoppedSessionRecording(connection.getCredProfilePair().getProfile(),
                perfSigAction != null ? perfSigAction.getSessionName() : sessionId));
    }

    public String getDynatraceProfile() {
        return dynatraceProfile;
    }

    @Symbol("stopSession")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillDynatraceProfileItems(@AncestorInPath Item item) {
            if (!item.hasPermission(Item.CONFIGURE) && item.hasPermission(Item.EXTENDED_READ)) {
                return new ListBoxModel();
            }
            return PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.PerfSigStopRecording_DisplayName();
        }
    }
}
