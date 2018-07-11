package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import de.tsystems.mms.apm.performancesignature.ui.util.PerfSigUIUtils;
import hudson.model.TaskListener;
import hudson.plugins.analysis.util.PluginLogger;
import org.jenkinsci.plugins.workflow.steps.BodyExecution;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

import javax.annotation.Nonnull;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DynatraceSessionStepExecution extends StepExecution {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DynatraceSessionStep.class.getName());
    private final transient DynatraceSessionStep step;
    private BodyExecution body;

    public DynatraceSessionStepExecution(DynatraceSessionStep step, StepContext context) {
        super(context);
        this.step = step;
    }

    @Override
    public boolean start() throws Exception {
        StepContext context = getContext();
        PluginLogger logger = PerfSigUIUtils.createLogger(listener().getLogger());

        logger.log("getting build details ...");

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
        listener().getLogger().println("stopped");
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
            listener().getLogger().println("finished");
        }
    }
}
