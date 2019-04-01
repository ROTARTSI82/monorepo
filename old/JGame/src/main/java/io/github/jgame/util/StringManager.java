package io.github.jgame.util;

import java.util.*;
import java.util.logging.Logger;

/**
 * Safe access to resource bundles.
 */
public class StringManager {
    /**
     * The internal resource bundle
     */
    private ResourceBundle bundle;

    /**
     * The internal logger object used for logging.
     */
    private Logger logger;

    /**
     * The strings to load in place of a missing resource.
     */
    private String[] missingResource = {"!![Missing Resource] %s_%s: %s!!", "!!%s_%s: %s!!"};

    /**
     * Load strings from a resource bundle.
     *
     * @param baseName Bundle to load
     * @param locale   Language to load.
     * @throws ExceptionInInitializerError The bundle may fail to load.
     */
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

    public StringManager() {
        bundle = null;
    }

    /**
     * Safe string formatting.
     *
     * @param format String format
     * @param args The format args.
     * @return Formatted string
     */
    public static String fmt(String format, Object... args) {
        try {
            return String.format(format, args);
        } catch (MissingFormatArgumentException e) {
            e.printStackTrace();
            String fmtArgs = Arrays.toString(args);
            return String.format("fmt(\"%s\", %s)", format, fmtArgs.substring(1, fmtArgs.length() - 1));
        }
    }

    /**
     * retrieve string from the resource bundle.
     * Return placeholder if the resource is missing.
     *
     * @param key Key to retrieve.
     * @return String
     */
    public String getString(String key) {
        if (bundle != null) {
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e) {
                // Ignore. Let it fall through
            }
        }
        Object[] fmtArgs = {Objects.requireNonNull(bundle).getBaseBundleName(), bundle.getLocale().toString(), key};
        logger.warning(fmt(missingResource[0], fmtArgs));
        return fmt(missingResource[1], fmtArgs);
    }
}
