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

import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.ApiException;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.rest.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.AbortException;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.BodyExecution;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import javax.annotation.Nonnull;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateDeploymentStepExecution extends AbstractStepExecutionImpl {
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
        PluginLogger logger = PerfSigUIUtils.createLogger(listener().getLogger());

        /*DTServerConnection connection = PerfSigUtils.createDTServerConnection(step.getDynatraceProfile());
        AlertsIncidentsAndEventsApi api = new AlertsIncidentsAndEventsApi(connection.getApiClient());

        DeploymentEvent event = new DeploymentEvent(null, null, "ongoing Deployment",
                "deployment event created by Jenkins", new Date(), null, connection.getCredProfilePair().getProfile(), null);
        eventId = api.createDeploymentEvent(event);
        if (eventId == null) {
            throw new AbortException("could not create deployment event");
        }*/
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
        if (body != null) {
            body.cancel(cause);
        }
        updateEvent();
    }

    private void updateEvent() throws ApiException, AbortException, RESTErrorException {
        if (eventId != null) {
            /*DTServerConnection connection = PerfSigUtils.createDTServerConnection(step.getDynatraceProfile());
            AlertsIncidentsAndEventsApi api = new AlertsIncidentsAndEventsApi(connection.getApiClient());
            EventUpdate event = new EventUpdate(new Date());
            api.updateDeploymentEvent(eventId, event);*/
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
