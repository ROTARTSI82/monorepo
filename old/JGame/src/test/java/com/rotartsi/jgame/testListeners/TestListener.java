package com.rotartsi.jgame.testListeners;

import org.testng.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.rotartsi.jgame.Constants.JGameStr;
import static com.rotartsi.jgame.Constants.settings;
import static com.rotartsi.jgame.util.StringManager.fmt;

public class TestListener implements ITestListener {
    private Logger logger;

    public TestListener() {
        super();
        logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        // logger.fine(fmt("Started test '%s'", result.getName()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        /*log(fmt("Finished test '%s' in %sms (SUCCESS)", result.getName(),
                result.getEndMillis() - result.getStartMillis()), Level.WARNING);*/
    }

    @Override
    public void onTestFailure(ITestResult result) {
        /*log(fmt("TEST FAILED: '%s' in %sms", result.getName(),
                result.getEndMillis() - result.getStartMillis()), Level.SEVERE);*/
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        /*log(fmt("TEST SKIPPED: '%s' in %sms", result.getName(),
                result.getEndMillis() - result.getStartMillis()), Level.SEVERE);*/
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        /*log(fmt("TEST FAILED (in % range): '%s' in %sms", result.getName(),
                result.getEndMillis() - result.getStartMillis()), Level.SEVERE);*/
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        /*log(fmt("TEST TIMED OUT: %s' in %sms", result.getName(),
                result.getEndMillis() - result.getStartMillis()), Level.SEVERE);*/
    }

    @Override
    public void onStart(ITestContext context) {
        log(fmt(JGameStr.getString("testing.startingTest"), context.getName()));
        TestRunner runner = (TestRunner) context;
        runner.setOutputDirectory(settings.get("tests.testOut"));
    }

    private void log(String msg) {
        logger.log(Level.INFO, msg);
        Reporter.log(msg + "\n");
    }

    @Override
    public void onFinish(ITestContext context) {
        log(fmt(JGameStr.getString("testing.finishedTest"), context.getName()));
    }
}
