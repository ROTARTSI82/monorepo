package com.rotartsi.jgame;

import com.rotartsi.jgame.crypto.RSATest;
import com.rotartsi.jgame.crypto.UserDatabaseTest;
import com.rotartsi.jgame.image.SurfaceMapTest;
import com.rotartsi.jgame.math.Vector2Test;
import com.rotartsi.jgame.mixer.SoundGeneratorTest;
import com.rotartsi.jgame.net.NetUtilsTest;
import com.rotartsi.jgame.net.TCPTest;
import com.rotartsi.jgame.net.UDPTest;
import com.rotartsi.jgame.sprite.SpriteTest;
import com.rotartsi.jgame.testListeners.InvokedMethodListener;
import com.rotartsi.jgame.testListeners.SuiteListener;
import com.rotartsi.jgame.testListeners.TestListener;
import com.rotartsi.jgame.util.ResourceManagerTest;
import com.rotartsi.jgame.util.ScrapUtilsTest;
import com.rotartsi.jgame.util.SettingsBundleTest;
import com.rotartsi.jgame.util.VersionTest;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.LinkedList;

import static com.rotartsi.jgame.Constants.JGameStr;
import static com.rotartsi.jgame.Constants.settings;

public class AllTests {

    public static void main(String[] args) {
        TestNG testSuite = new TestNG();
        testSuite.addListener(new InvokedMethodListener());
        testSuite.addListener(new TestListener());
        testSuite.addListener(new SuiteListener());

        testSuite.setThreadCount(Runtime.getRuntime().availableProcessors());
        testSuite.setVerbose(2);

        testSuite.setDefaultSuiteName(JGameStr.getString("testing.suiteName"));
        testSuite.setDefaultTestName(JGameStr.getString("testing.category.default"));
        testSuite.setOutputDirectory(settings.get("tests.testOut"));
        testSuite.setRandomizeSuites(true);
        testSuite.setPreserveOrder(false);

        XmlSuite suite = new XmlSuite();

        suite.setParallel(XmlSuite.ParallelMode.TESTS);
        suite.setThreadCount(Runtime.getRuntime().availableProcessors());
        suite.setPreserveOrder(false);
        suite.setVerbose(2);

        suite.setName(JGameStr.getString("testing.suiteName"));

        addTest(JGameStr.getString("testing.category.crypto"), suite, new Class[]{
                RSATest.class,
                UserDatabaseTest.class
        });
        addTest(JGameStr.getString("testing.category.image"), suite, new Class[]{
                SurfaceMapTest.class
        });
        addTest(JGameStr.getString("testing.category.math"), suite, new Class[]{
                Vector2Test.class
        });
        addTest(JGameStr.getString("testing.category.mixer"), suite, new Class[]{
                SoundGeneratorTest.class
        });
        addTest(JGameStr.getString("testing.category.net"), suite, new Class[]{
                NetUtilsTest.class,
                TCPTest.class,
                UDPTest.class
        });
        addTest(JGameStr.getString("testing.category.sprite"), suite, new Class[]{
                SpriteTest.class
        });
        addTest(JGameStr.getString("testing.category.util"), suite, new Class[]{
                ScrapUtilsTest.class,
                ResourceManagerTest.class,
                VersionTest.class,
                SettingsBundleTest.class,
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
