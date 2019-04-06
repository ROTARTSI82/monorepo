package com.rotartsi.jgame.testListeners;

import org.testng.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.rotartsi.jgame.Constants.JGameStr;
import static com.rotartsi.jgame.util.StringManager.fmt;

public class InvokedMethodListener implements IInvokedMethodListener {
    private Logger logger;

    public InvokedMethodListener() {
        super();
        logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        // logger.finer(fmt("Invoking method %s: %s", method, testResult.getName()));
    }

    private void log(Level lvl, String msg) {
        logger.log(lvl, msg);
        Reporter.log(msg + "\n");
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        /*
        int CREATED = -1;
        int SUCCESS = 1;
        int FAILURE = 2;
        int SKIP = 3;
        int SUCCESS_PERCENTAGE_FAILURE = 4;
        int STARTED = 16;
         **/
        switch (testResult.getStatus()) {
            case ITestResult.CREATED:
            case ITestResult.STARTED: {
                break;
            }
            case ITestResult.SUCCESS: {
                log(Level.INFO, fmt(JGameStr.getString("testing.pass"), method,
                        testResult.getEndMillis() - testResult.getStartMillis()));
                break;
            }
            case ITestResult.FAILURE: {
                log(Level.SEVERE, fmt(JGameStr.getString("testing.fail"), method,
                        testResult.getEndMillis() - testResult.getStartMillis()));
                break;
            }
            case ITestResult.SKIP: {
                log(Level.SEVERE, fmt(JGameStr.getString("testing.skip"), method,
                        testResult.getEndMillis() - testResult.getStartMillis()));
                break;
            }
            case ITestResult.SUCCESS_PERCENTAGE_FAILURE: {
                log(Level.SEVERE, fmt(JGameStr.getString("testing.passPercentFail"), method,
                        testResult.getEndMillis() - testResult.getStartMillis()));
                break;
            }
        }
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
