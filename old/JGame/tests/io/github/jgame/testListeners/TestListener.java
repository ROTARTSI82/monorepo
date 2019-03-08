package io.github.jgame.testListeners;

import org.testng.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.settings;

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
        log(fmt("Finished test '%s' in %sms (SUCCESS)", result.getName(),
                result.getEndMillis() - result.getStartMillis()), Level.WARNING);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log(fmt("TEST FAILED: '%s' in %sms", result.getName(),
                result.getEndMillis() - result.getStartMillis()), Level.WARNING);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log(fmt("TEST SKIPPED: '%s' in %sms", result.getName(),
                result.getEndMillis() - result.getStartMillis()), Level.WARNING);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log(fmt("TEST FAILED (in % range): '%s' in %sms", result.getName(),
                result.getEndMillis() - result.getStartMillis()), Level.WARNING);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        log(fmt("TEST TIMED OUT: %s' in %sms", result.getName(),
                result.getEndMillis() - result.getStartMillis()), Level.WARNING);
    }

    @Override
    public void onStart(ITestContext context) {
        log(fmt("Running %s", context.getName()), Level.INFO);
        TestRunner runner = (TestRunner) context;
        runner.setOutputDirectory(settings.getString("tests.testOut"));
    }

    private void log(String msg, Level lvl) {
        logger.log(lvl, msg);
        Reporter.log(msg, lvl.intValue());
    }

    @Override
    public void onFinish(ITestContext context) {
        log(fmt("Finished %s", context.getName()), Level.INFO);
    }
}
