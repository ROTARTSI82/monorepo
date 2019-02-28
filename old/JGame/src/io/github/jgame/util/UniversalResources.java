package io.github.jgame.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class UniversalResources {
    public final static Random rand = new Random();
    public final static SecureRandom secureRand = new SecureRandom();

    public final static BigInteger one = new BigInteger("1");
    public final static BigInteger zero = new BigInteger("0");
    public final static BigInteger universalPublic = new BigInteger("65537");

    public final static StringManager JGameStr;
    public final static StringManager settings;

    static {
        Locale locale = Locale.getDefault();
        settings = new StringManager("settings", locale);
        locale = new Locale(settings.getString("lang"), settings.getString("country"));
        JGameStr = new StringManager("lang.JGame", locale);  // Remember lang.
    }
}
