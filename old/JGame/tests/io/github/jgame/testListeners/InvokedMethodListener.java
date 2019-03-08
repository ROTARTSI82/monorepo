package io.github.jgame.testListeners;

import org.testng.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.util.StringManager.fmt;

public class InvokedMethodListener implements IInvokedMethodListener {
    private Logger logger;

    public InvokedMethodListener() {
        super();
        logger = Logger.getLogger(this.getClass().getName());
    }

    private void log(String msg, Level lvl) {
        logger.log(lvl, msg);
        Reporter.log(msg, lvl.intValue());
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        // logger.finer(fmt("Invoking method %s: %s", method, testResult.getName()));
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        log(fmt("Invoked method %s in %sms", method,
                testResult.getEndMillis() - testResult.getStartMillis()), Level.FINER);
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        // logger.finer(fmt("Invoking method %s: %s (context=%s)", method, testResult.getName()));
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        // logger.info(fmt("Invoked method %s: %s (context=%s)", method, testResult.getName(), context.getName()));
    }
}
