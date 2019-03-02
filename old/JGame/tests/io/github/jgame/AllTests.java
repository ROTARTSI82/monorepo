package io.github.jgame;

import io.github.jgame.crypto.RSATest;
import io.github.jgame.crypto.UserDatabaseTest;
import io.github.jgame.image.SurfaceMapTest;
import io.github.jgame.logging.GenericLogger;
import io.github.jgame.math.Vector2Test;
import io.github.jgame.mixer.SoundGeneratorTest;
import io.github.jgame.net.NetUtilsTest;
import io.github.jgame.net.TCPTest;
import io.github.jgame.net.UDPTest;
import io.github.jgame.sprite.SpriteTest;
import org.testng.*;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.jgame.util.StringManager.fmt;

public class AllTests implements ISuiteListener {
    private Logger logger;

    public AllTests() {
        logger = Logger.getLogger(this.getClass().getName());
    }

    public static void main(String[] args) {
        GenericLogger.setup(Level.ALL, Level.ALL, Level.OFF);
        TestNG testSuite = new TestNG();
        testSuite.setTestClasses(new Class[]{
                RSATest.class,
                UserDatabaseTest.class,
                SurfaceMapTest.class,
                Vector2Test.class,
                SoundGeneratorTest.class,
                NetUtilsTest.class,
                TCPTest.class,
                UDPTest.class,
                SpriteTest.class,
                ScrapUtilsTest.class,
                VersionTest.class,
        });
        testSuite.addListener(new AllTests());
        testSuite.setDefaultSuiteName("JGame All Tests");
        testSuite.setDefaultTestName("All Tests");
        //testSuite.setOutputDirectory("/Users/pankaj/temp/testng-output");
        testSuite.run();
    }

    @Override
    public void onStart(ISuite suite) {
        logger.info(fmt("Suite %s outputing to %s", suite.getName(), suite.getOutputDirectory()));
    }

    @Override
    public void onFinish(ISuite suite) {
        for (IInvokedMethod x : suite.getAllInvokedMethods()) {
            logger.info(fmt("Invoked Method %s", x));
        }
        Map<String, ISuiteResult> results = suite.getResults();
        for (String key : results.keySet()) {
            logger.info(fmt("Result %s: %s", key, results.get(key)));
        }
    }
}
