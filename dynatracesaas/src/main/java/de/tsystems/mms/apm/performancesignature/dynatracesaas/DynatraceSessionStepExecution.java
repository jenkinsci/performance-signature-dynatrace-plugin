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

import de.tsystems.mms.apm.performancesignature.dynatracesaas.model.EntityId;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.EventPushMessage;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.EventTypeEnum;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.model.PushEventAttachRules;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.EnvVars;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.collections.CollectionUtils;
import org.jenkinsci.plugins.workflow.steps.BodyExecution;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static de.tsystems.mms.apm.performancesignature.dynatracesaas.CreateDeploymentStepExecution.BUILD_VAR_KEY_DEPLOYMENT_PROJECT;
import static de.tsystems.mms.apm.performancesignature.dynatracesaas.CreateDeploymentStepExecution.BUILD_VAR_KEY_DEPLOYMENT_VERSION;
import static de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.DynatraceServerConnection.BUILD_URL_ENV_PROPERTY;

public class DynatraceSessionStepExecution extends StepExecution {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DynatraceSessionStep.class.getName());
    private final transient DynatraceSessionStep step;
    private final transient Run<?, ?> run;
    private final transient DynatraceEnvInvisAction action;
    private BodyExecution body;

    public DynatraceSessionStepExecution(DynatraceSessionStep step, StepContext context) throws Exception {
        super(context);
        this.step = step;
        this.run = context.get(Run.class);
        this.action = new DynatraceEnvInvisAction(step.getTestCase(), Instant.now().toEpochMilli());
    }

    @Override
    public boolean start() throws Exception {
        StepContext context = getContext();

        println("getting build details ...");
        run.addAction(action);

        if (context.hasBody()) {
            body = context.newBodyInvoker()
                    .withCallback(new Callback())
                    .start();
        }
        return false;
    }

    @Override
    public void stop(@Nonnull Throwable cause) throws Exception {
        println("stopping session recording ...");
        action.setTimeframeStop(System.currentTimeMillis());

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
            println("stopping session recording ...");
            action.setTimeframeStop(System.currentTimeMillis());

            if (CollectionUtils.isNotEmpty(step.getEntityIds()) || CollectionUtils.isNotEmpty(step.getTagMatchRules())) {
                DynatraceServerConnection serverConnection = DynatraceUtils.createDynatraceServerConnection(step.getEnvId(), true);

                PushEventAttachRules attachRules = new PushEventAttachRules();
                if (CollectionUtils.isNotEmpty(step.getEntityIds()))
                    attachRules.setEntityIds(step.getEntityIds().stream().map(EntityId::getEntityId).collect(Collectors.toList()));
                attachRules.setTagRule(step.getTagMatchRules());

                println("creating Performance Signature custom event");
                EventPushMessage event = new EventPushMessage(EventTypeEnum.CUSTOM_INFO, attachRules)
                        .setSource("Jenkins")
                        .setTitle("Performance Signature was executed")
                        .setStartTime(action.getTimeframeStart())
                        .setEndTime(action.getTimeframeStop());
                if (envVars != null) {
                    event.setDescription("Performance Signature was executed in a Jenkins Pipeline")
                            .addCustomProperties("Jenkins Build Number", envVars.get("BUILD_ID"))
                            .addCustomProperties("Git Commit", envVars.get("GIT_COMMIT"))
                            .addCustomProperties("Deployment Version", envVars.get(BUILD_VAR_KEY_DEPLOYMENT_VERSION))
                            .addCustomProperties("Deployment Project", envVars.get(BUILD_VAR_KEY_DEPLOYMENT_PROJECT))
                            .addCustomProperties("Deployment Name", envVars.get("JOB_NAME"))
                            .addCustomProperties("CiBackLink", envVars.get(BUILD_URL_ENV_PROPERTY));
                }
                serverConnection.createEvent(event);
                println("created Performance Signature event");
            } else {
                println("skipped creating Performance Signature event");
            }
        }
    }
}
