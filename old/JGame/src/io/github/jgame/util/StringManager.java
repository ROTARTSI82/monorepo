package io.github.jgame.util;

import java.util.*;

public class StringManager {
    private ResourceBundle bundle;
    private String missingResource;

    public StringManager(String baseName, Locale locale) {
        bundle = ResourceBundle.getBundle(baseName, locale);
        try {
            missingResource = bundle.getString("missingResource");
        } catch (MissingResourceException e) {
            missingResource = "Missing Resource: %s";
        }
    }

    public static String fmt(String format, Object... args) {
        try {
            return String.format(format, args);
        } catch (MissingFormatArgumentException e) {
            return format + Arrays.toString(args);
        }
    }

    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return fmt(missingResource, key);
        }
    }
}
