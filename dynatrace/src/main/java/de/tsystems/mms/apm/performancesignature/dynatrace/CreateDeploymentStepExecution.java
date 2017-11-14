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

import de.tsystems.mms.apm.performancesignature.dynatrace.rest.DTServerConnection;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.ApiException;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.api.AlertsIncidentsAndEventsApi;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.DeploymentEvent;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.json.model.EventUpdate;
import de.tsystems.mms.apm.performancesignature.dynatrace.rest.xml.RESTErrorException;
import de.tsystems.mms.apm.performancesignature.util.PerfSigUtils;
import hudson.AbortException;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.BodyExecution;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateDeploymentStepExecution extends StepExecution {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CreateDeploymentStepExecution.class.getName());
    private final transient CreateDeploymentStep step;
    private BodyExecution body;
    private String eventId;

    CreateDeploymentStepExecution(CreateDeploymentStep createDeploymentStep, StepContext context) throws IOException, InterruptedException {
        super(context);
        this.step = createDeploymentStep;
    }

    @Override
    public boolean start() throws Exception {
        StepContext context = getContext();

        DTServerConnection connection = PerfSigUtils.createDTServerConnection(step.getDynatraceProfile());
        AlertsIncidentsAndEventsApi api = new AlertsIncidentsAndEventsApi(connection.getApiClient());

        DeploymentEvent event = new DeploymentEvent(null, null, "ongoing Deployment",
                "deployment event created by Jenkins", new Date(), null, connection.getCredProfilePair().getProfile(), null);
        eventId = api.createDeploymentEvent(event);
        if (eventId == null) {
            throw new AbortException("could not create deployment event");
        }
        listener().getLogger().println("successfully created deployment event " + eventId);

        body = context.newBodyInvoker()
                .withCallback(new Callback())
                .start();
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
            DTServerConnection connection = PerfSigUtils.createDTServerConnection(step.getDynatraceProfile());
            AlertsIncidentsAndEventsApi api = new AlertsIncidentsAndEventsApi(connection.getApiClient());
            EventUpdate event = new EventUpdate(new Date());
            api.updateDeploymentEvent(eventId, event);
            listener().getLogger().println("successfully updated deployment event " + eventId);
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
