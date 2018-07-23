package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.BodyExecution;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        action.setTimeframeStop(Instant.now().toEpochMilli());

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
            println("stopping session recording ...");
            action.setTimeframeStop(Instant.now().toEpochMilli());
        }
    }
}
