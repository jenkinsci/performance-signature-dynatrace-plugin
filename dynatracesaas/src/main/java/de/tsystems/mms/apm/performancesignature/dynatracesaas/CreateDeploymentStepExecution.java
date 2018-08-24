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

import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.EntityId;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.EventPushMessage;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.EventStoreResult;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.EventTypeEnum;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.PushEventAttachRules;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.BodyExecution;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection.BUILD_URL_ENV_PROPERTY;

public class CreateDeploymentStepExecution extends StepExecution {
    static final String BUILD_VAR_KEY_DEPLOYMENT_VERSION = "dtDeploymentVersion";
    static final String BUILD_VAR_KEY_DEPLOYMENT_PROJECT = "dtDeploymentProject";
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CreateDeploymentStepExecution.class.getName());
    private final transient CreateDeploymentStep step;
    private BodyExecution body;
    private long startTimestamp;

    CreateDeploymentStepExecution(CreateDeploymentStep createDeploymentStep, StepContext context) {
        super(context);
        this.step = createDeploymentStep;
    }

    @Override
    public boolean start() throws Exception {
        StepContext context = getContext();
        startTimestamp = System.currentTimeMillis();

        if (context.hasBody()) {
            body = context.newBodyInvoker()
                    .withCallback(new Callback())
                    .start();
        }
        return false;
    }

    @Override
    public void stop(@Nonnull Throwable cause) throws Exception {
        println("stopping deployment event");
        if (body != null) {
            body.cancel(cause);
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

    private class Callback extends BodyExecutionCallback.TailCall {
        private static final long serialVersionUID = 1L;

        @Override
        protected void finished(StepContext context) throws Exception {
            EnvVars envVars = getContext().get(EnvVars.class);

            DynatraceServerConnection connection = DynatraceUtils.createDynatraceServerConnection(step.getEnvId(), true);

            PushEventAttachRules attachRules = new PushEventAttachRules();
            attachRules.setEntityIds(step.getEntityIds().stream().map(EntityId::getEntityId).collect(Collectors.toList()));
            attachRules.setTagRule(step.getTagMatchRules());

            long endTimestamp = System.currentTimeMillis();
            EventPushMessage event = new EventPushMessage(EventTypeEnum.CUSTOM_DEPLOYMENT, attachRules)
                    .setStartTime(startTimestamp)
                    .setEndTime(endTimestamp)
                    .setSource("Jenkins");
            if (envVars != null) {
                event.setDeploymentName(envVars.get("JOB_NAME"))
                        .setDeploymentVersion(Optional.ofNullable(envVars.get(BUILD_VAR_KEY_DEPLOYMENT_VERSION)).orElse(" "))
                        .setDeploymentProject(envVars.get(BUILD_VAR_KEY_DEPLOYMENT_PROJECT))
                        .setCiBackLink(envVars.get(BUILD_URL_ENV_PROPERTY))
                        .addCustomProperties("Jenkins Build Number", envVars.get("BUILD_ID"))
                        .addCustomProperties("Git Commit", envVars.get("GIT_COMMIT"));
            }

            EventStoreResult eventStoreResult = connection.createEvent(event);
            if (eventStoreResult == null) {
                throw new AbortException("could not create deployment event");
            }
            println("successfully created deployment event " + eventStoreResult);
        }
    }
}
