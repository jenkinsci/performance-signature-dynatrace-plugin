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

import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.CredProfilePair;
import de.tsystems.mms.apm.performancesignature.dynatrace.configuration.DynatraceServerConfiguration;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.model.Agent;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;

public class PerfSigActivateConfiguration extends Builder implements SimpleBuildStep {
    private final String dynatraceProfile;
    private final String configuration;

    @DataBoundConstructor
    public PerfSigActivateConfiguration(final String dynatraceProfile, final String configuration) {
        this.dynatraceProfile = dynatraceProfile;
        this.configuration = configuration;
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace, @Nonnull final Launcher launcher, @Nonnull final TaskListener listener)
            throws IOException {
        PluginLogger logger = PerfSigUIUtils.createLogger(listener.getLogger());
        DTServerConnection connection = PerfSigUtils.createDTServerConnection(dynatraceProfile);

        logger.log(Messages.PerfSigActivateConfiguration_ActivatingProfileConfiguration());
        connection.activateConfiguration(configuration);
        logger.log(Messages.PerfSigActivateConfiguration_SuccessfullyActivated(dynatraceProfile));

        for (Agent agent : connection.getAgents()) {
            boolean hotSensorPlacement = connection.hotSensorPlacement(agent.getAgentId());
            if (hotSensorPlacement) {
                logger.log(Messages.PerfSigActivateConfiguration_HotSensorPlacementDone(agent.getName()));
            } else {
                logger.log(Messages.PerfSigActivateConfiguration_FailureActivation(agent.getName()));
            }
        }
    }

    public String getDynatraceProfile() {
        return dynatraceProfile;
    }

    public String getConfiguration() {
        return configuration;
    }

    @Symbol("activateDTConfiguration")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Restricted(NoExternalUse.class)
        public FormValidation doCheckConfiguration(@QueryParameter final String configuration) {
            FormValidation validationResult;
            if (StringUtils.isNotBlank(configuration)) {
                validationResult = FormValidation.ok();
            } else {
                validationResult = FormValidation.error(Messages.PerfSigActivateConfiguration_ConfigurationNotValid());
            }
            return validationResult;
        }

        public ListBoxModel doFillDynatraceProfileItems() {
            return PerfSigUtils.listToListBoxModel(PerfSigUtils.getDTConfigurations());
        }

        @Restricted(NoExternalUse.class)
        @Nonnull
        public ListBoxModel doFillConfigurationItems(@QueryParameter final String dynatraceProfile) {
            DynatraceServerConfiguration serverConfiguration = PerfSigUtils.getServerConfiguration(dynatraceProfile);
            if (serverConfiguration != null) {
                CredProfilePair pair = serverConfiguration.getCredProfilePair(dynatraceProfile);
                if (pair != null) {
                    DTServerConnection connection = new DTServerConnection(serverConfiguration, pair);
                    return PerfSigUtils.listToListBoxModel(connection.getProfileConfigurations());
                }
            }
            return new ListBoxModel();
        }

        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return Messages.PerfSigActivateConfiguration_DisplayName();
        }
    }
}
