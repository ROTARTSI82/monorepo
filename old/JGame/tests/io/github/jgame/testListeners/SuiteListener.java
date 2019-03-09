package io.github.jgame.testListeners;

import io.github.jgame.logging.GenericLogger;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.Reporter;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;
import static io.github.jgame.util.UniversalResources.settings;

public class SuiteListener implements ISuiteListener {
    private Logger logger;

    public SuiteListener() {
        super();
        logger = Logger.getLogger(this.getClass().getName());
    }

    private void log(String msg, Level lvl) {
        logger.log(lvl, msg);
        Reporter.log(msg + "\n");
    }

    @Override
    public void onStart(ISuite suite) {
        GenericLogger.setup(Level.ALL, Level.ALL, Level.OFF, settings.getString("tests.logOut"));

        log(fmt(JGameStr.getString("AllTests.startSuite"), suite.getName(),
                suite.getOutputDirectory()), Level.INFO);

        Map<String, String> env = System.getenv();
        for (String x : env.keySet()) {
            log(fmt("%s=%s", x, env.get(x)), Level.CONFIG);
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
