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

package de.tsystems.mms.apm.performancesignature.dynatrace;

import de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert.SeverityEnum;
import de.tsystems.mms.apm.performancesignature.dynatrace.model.Alert.StateEnum;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.DeploymentEvent;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.EventUpdate;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.dynatrace.util.PerfSigUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PluginLogger;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.BodyExecution;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection.BUILD_URL_ENV_PROPERTY;

public class CreateDeploymentStepExecution extends StepExecution {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CreateDeploymentStepExecution.class.getName());
    private final transient CreateDeploymentStep step;
    private BodyExecution body;
    private String eventId;

    CreateDeploymentStepExecution(CreateDeploymentStep createDeploymentStep, StepContext context) {
        super(context);
        this.step = createDeploymentStep;
    }

    @Override
    public boolean start() throws Exception {
        StepContext context = getContext();
        EnvVars envVars = getContext().get(EnvVars.class);
        PluginLogger logger = PerfSigUIUtils.createLogger(listener().getLogger());

        DTServerConnection connection = PerfSigUtils.createDTServerConnection(step.getDynatraceProfile());

        String buildUrl = envVars != null ? envVars.get(BUILD_URL_ENV_PROPERTY) : "";
        DeploymentEvent event = new DeploymentEvent(connection.getCredProfilePair().getProfile(), "ongoing Deployment")
                .setSeverity(SeverityEnum.WARNING)
                .setState(StateEnum.CREATED)
                .setDescription("deployment event created by Jenkins: " + buildUrl)
                .setStart(new Date());
        eventId = connection.createDeploymentEvent(event);
        if (eventId == null) {
            throw new AbortException("could not create deployment event");
        }
        logger.log("successfully created deployment event " + eventId);

        if (context.hasBody()) {
            body = context.newBodyInvoker()
                    .withCallback(new Callback())
                    .start();
        }
        return false;
    }

    @Override
    public void stop(@Nonnull Throwable cause) throws Exception {
        updateEvent();
        if (body != null) {
            body.cancel(cause);
        }
    }

    private void updateEvent() throws AbortException, RESTErrorException {
        if (eventId != null) {
            DTServerConnection connection = PerfSigUtils.createDTServerConnection(step.getDynatraceProfile());
            connection.updateDeploymentEvent(eventId, new EventUpdate(new Date()));
            PluginLogger logger = PerfSigUIUtils.createLogger(listener().getLogger());
            logger.log("successfully updated deployment event " + eventId);
        }
    }

    private TaskListener listener() {
        try {
            return getContext().get(TaskListener.class);
        } catch (Exception x) {
            LOGGER.log(Level.WARNING, null, x);
            return TaskListener.NULL;
        }
    }

    private class Callback extends BodyExecutionCallback.TailCall {
        private static final long serialVersionUID = 1L;

        @Override
        protected void finished(StepContext context) throws Exception {
            updateEvent();
        }
    }
}
