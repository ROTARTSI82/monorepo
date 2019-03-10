package io.github.jgame.util;

import java.util.*;
import java.util.logging.Logger;

public class StringManager {
    private ResourceBundle bundle;
    private Logger logger;
    private String[] missingResource = {"!![Missing Resource] %s_%s: %s!!", "!!%s_%s: %s!!"};

    public StringManager(String baseName, Locale locale) throws ExceptionInInitializerError {
        logger = Logger.getLogger(this.getClass().getName());
        bundle = ResourceBundle.getBundle(baseName, locale);
        try {
            missingResource = new String[]{bundle.getString("missingResource.log"),
                    bundle.getString("missingResource.ret")};
        } catch (MissingResourceException e) {
            // Ignore this
        }
    }

    public static String fmt(String format, Object... args) {
        try {
            return String.format(format, args);
        } catch (MissingFormatArgumentException e) {
            e.printStackTrace();
            String fmtArgs = Arrays.toString(args);
            return String.format("fmt(\"%s\", %s)", format, fmtArgs.substring(1, fmtArgs.length() - 1));
        }
    }

    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            Object[] fmtArgs = {bundle.getBaseBundleName(), bundle.getLocale().toString(), key};
            logger.warning(fmt(missingResource[0], fmtArgs));
            return fmt(missingResource[1], fmtArgs);
        }
    }
}
