package com.rotartsi.jgame.testListeners;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.Reporter;

import java.util.Map;

import static com.rotartsi.jgame.Constants.JGameStr;
import static com.rotartsi.jgame.util.StringManager.fmt;

public class SuiteListener implements ISuiteListener {
    private Logger logger = LogManager.getLogger(SuiteListener.class);

    public SuiteListener() {
        super();
    }

    private void log(String msg, Level lvl) {
        logger.log(lvl, msg);
        Reporter.log(msg + "\n");
    }

    @Override
    public void onStart(ISuite suite) {
        //Log4jLogger.setup(settings.get("tests.logOut"));
        // GenericLogger.setup(Level.ALL, Level.ALL, Level.OFF, settings.get("tests.logOut"));

        log(fmt(JGameStr.getString("AllTests.startSuite"), suite.getName(),
                suite.getOutputDirectory()), Level.INFO);

        Map<String, String> env = System.getenv();
        for (String x : env.keySet()) {
            log(fmt("%s=%s", x, env.get(x)), Level.TRACE);
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        /*
        for (IInvokedMethod x : suite.getAllInvokedMethods()) {
            logger.fine(fmt(JGameStr.getString("AllTests.invokedMethod"), x));
        }
        */
    }
}
