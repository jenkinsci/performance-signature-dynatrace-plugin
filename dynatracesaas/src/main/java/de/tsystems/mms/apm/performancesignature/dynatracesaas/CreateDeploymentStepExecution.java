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

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.*;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection.BUILD_URL_ENV_PROPERTY;

public class CreateDeploymentStepExecution extends StepExecution {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CreateDeploymentStepExecution.class.getName());
    private static final String BUILD_VAR_KEY_DEPLOYMENT_VERSION = "dtDeploymentVersion";
    private static final String BUILD_VAR_KEY_DEPLOYMENT_PROJECT = "dtDeploymentProject";
    private final transient CreateDeploymentStep step;

    CreateDeploymentStepExecution(CreateDeploymentStep createDeploymentStep, StepContext context) {
        super(context);
        this.step = createDeploymentStep;
    }

    @Override
    public boolean start() throws Exception {
        StepContext context = getContext();
        EnvVars envVars = getContext().get(EnvVars.class);

        DynatraceServerConnection connection = DynatraceUtils.createDynatraceServerConnection(step.getEnvId(), true);

        PushEventAttachRules attachRules = new PushEventAttachRules();
        attachRules.setEntityIds(Arrays.stream(step.getEntityIds().split(",")).map(String::trim).collect(Collectors.toList()));
        attachRules.addTagRuleItem(new TagMatchRule());

        EventPushMessage event = new EventPushMessage(EventTypeEnum.CUSTOM_DEPLOYMENT, attachRules)
                .setSource("Jenkins");
        if (envVars != null) {
            event.setDeploymentName(envVars.get("JOB_NAME"))
                    .setDeploymentVersion(envVars.get(BUILD_VAR_KEY_DEPLOYMENT_VERSION))
                    .setDeploymentProject(envVars.get(BUILD_VAR_KEY_DEPLOYMENT_PROJECT))
                    .setCiBackLink(envVars.get(BUILD_URL_ENV_PROPERTY))
                    .addCustomProperties("Jenkins Build Number", envVars.get("BUILD_ID"))
                    .addCustomProperties("Git Commit", envVars.get("GIT_COMMIT"));
        }

        EventStoreResult eventId = connection.createDeploymentEvent(event);
        if (eventId == null) {
            throw new AbortException("could not create deployment event");
        }
        println("successfully created deployment event " + eventId);

        if (context.hasBody()) {
            context.newBodyInvoker().start();
        }
        return false;
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
