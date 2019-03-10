package io.github.jgame;

import io.github.jgame.crypto.RSATest;
import io.github.jgame.crypto.UserDatabaseTest;
import io.github.jgame.image.SurfaceMapTest;
import io.github.jgame.math.Vector2Test;
import io.github.jgame.mixer.SoundGeneratorTest;
import io.github.jgame.net.NetUtilsTest;
import io.github.jgame.net.TCPTest;
import io.github.jgame.net.UDPTest;
import io.github.jgame.sprite.SpriteTest;
import io.github.jgame.testListeners.InvokedMethodListener;
import io.github.jgame.testListeners.SuiteListener;
import io.github.jgame.testListeners.TestListener;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.LinkedList;

import static io.github.jgame.Constants.settings;

public class AllTests {

    public static void main(String[] args) {
        TestNG testSuite = new TestNG();
        testSuite.addListener(new InvokedMethodListener());
        testSuite.addListener(new TestListener());
        testSuite.addListener(new SuiteListener());

        testSuite.setThreadCount(Runtime.getRuntime().availableProcessors());
        testSuite.setVerbose(2);

        testSuite.setDefaultSuiteName("JGame All Tests");
        testSuite.setDefaultTestName("Unkown JGame Test");
        testSuite.setOutputDirectory(settings.getString("tests.testOut"));
        testSuite.setRandomizeSuites(true);
        testSuite.setPreserveOrder(false);

        XmlSuite suite = new XmlSuite();

        suite.setParallel(XmlSuite.ParallelMode.TESTS);
        suite.setThreadCount(Runtime.getRuntime().availableProcessors());
        suite.setPreserveOrder(false);
        suite.setVerbose(2);

        suite.setName("JGame All Tests");

        addTest("Crypto Tests", suite, new Class[]{
                RSATest.class,
                UserDatabaseTest.class
        });
        addTest("Image Tests", suite, new Class[]{
                SurfaceMapTest.class
        });
        addTest("Math Tests", suite, new Class[]{
                Vector2Test.class
        });
        addTest("Mixer Tests", suite, new Class[]{
                SoundGeneratorTest.class
        });
        addTest("Net Tests", suite, new Class[]{
                NetUtilsTest.class,
                TCPTest.class,
                UDPTest.class
        });
        addTest("Sprite Tests", suite, new Class[]{
                SpriteTest.class
        });
        addTest("Generic JGame Tests", suite, new Class[]{
                ScrapUtilsTest.class,
                VersionTest.class
        });

        testSuite.setXmlSuites(new LinkedList<>() {{
            add(suite);
        }});

        testSuite.run();
    }

    private static void addTest(String name, XmlSuite suite, Class[] classes) {
        XmlTest test = new XmlTest(suite);
        test.setName(name);
        test.setParallel(XmlSuite.ParallelMode.TESTS);
        test.setPreserveOrder(false);
        test.setThreadCount(Runtime.getRuntime().availableProcessors());
        test.setVerbose(2);

        LinkedList<XmlClass> testClasses = new LinkedList<>();
        for (Class ks : classes) {
            XmlClass klass = new XmlClass();
            klass.setClass(ks);
            testClasses.add(klass);
        }

        test.setXmlClasses(testClasses);
    }
}
