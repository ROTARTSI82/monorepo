package com.rotartsi.jgame;

import com.rotartsi.jgame.util.SettingsBundle;
import com.rotartsi.jgame.util.StringManager;
import com.rotartsi.jgame.util.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

/**
 * Global Constant variables.
 */
public class Constants {
    /**
     * RNG for general purpose use.
     */
    public final static Random rand = new Random();

    /**
     * Secure RNG for the crypto module.
     */
    public final static SecureRandom secureRand = new SecureRandom();

    /**
     * Constant value of 1 in BigInteger
     */
    public final static BigInteger one = new BigInteger("1");

    /**
     * Constant value of 0 in BigInteger
     */
    public final static BigInteger zero = new BigInteger("0");

    /**
     * Constant value of 65537 (2^16+1) in BigInteger
     */
    public final static BigInteger universalPublic = new BigInteger("65537");

    /**
     * Default StringManager
     */
    public final static StringManager JGameStr;

    /**
     * Settings. Capable of reading and writing.
     */
    public final static SettingsBundle settings;

    /**
     * Internal logger object used for logging.
     */
    public final static Logger logger = LogManager.getLogger(Constants.class);

    static {
        SettingsBundle tmp_settings;
        StringManager tmp_jgameStr;
        try {
            tmp_settings = new SettingsBundle("settings.properties");
            Locale locale = new Locale(tmp_settings.get("lang"), tmp_settings.get("country"));
            tmp_jgameStr = new StringManager("assets.lang.JGame", locale);  // Remember lang.
        } catch (IOException e) {
            logger.fatal("Failed to load settings", e);
            tmp_settings = new SettingsBundle();
            tmp_jgameStr = new StringManager();
        }
        settings = tmp_settings;
        JGameStr = tmp_jgameStr;
    }

    /**
     * Boolean value of whether to block certain loggers defined in settings.properties.
     */
    public final static boolean BLOCK_LOGS = Boolean.valueOf(settings.get("logging.GenericLogger.blockLoggers"));
    /**
     * Preset for custom render hints loaded from settings.properties
     */
    public final static RenderingHints CUSTOM_RENDER_HINTS = new RenderingHints(
            new HashMap<>() {{
                put(RenderingHints.KEY_ALPHA_INTERPOLATION, settings.get("rh.alphaInterpolation").equals("quality") ?
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY : RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

                put(RenderingHints.KEY_INTERPOLATION, settings.get("rh.interpolation").equals("bilinear") ?
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                put(RenderingHints.KEY_RENDERING, settings.get("rh.rendering").equals("quality") ?
                        RenderingHints.VALUE_RENDER_QUALITY : RenderingHints.VALUE_RENDER_SPEED);

                put(RenderingHints.KEY_COLOR_RENDERING, settings.get("rh.colorRender").equals("quality") ?
                        RenderingHints.VALUE_COLOR_RENDER_QUALITY : RenderingHints.VALUE_COLOR_RENDER_SPEED);

                put(RenderingHints.KEY_DITHERING, settings.get("rh.dither").equals("enable") ?
                        RenderingHints.VALUE_DITHER_ENABLE : RenderingHints.VALUE_DITHER_DISABLE);

                put(RenderingHints.KEY_FRACTIONALMETRICS, settings.get("rh.fractionalmetrics").equals("on") ?
                        RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

                put(RenderingHints.KEY_STROKE_CONTROL, settings.get("rh.strokeControl").equals("pure") ?
                        RenderingHints.VALUE_STROKE_PURE : RenderingHints.VALUE_STROKE_NORMALIZE);

                put(RenderingHints.KEY_TEXT_ANTIALIASING, settings.get("rh.textAntialias").equals("on") ?
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

                put(RenderingHints.KEY_ANTIALIASING, settings.get("rh.antialias").equals("on") ?
                        RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
            }});

    /**
     * The default font for displaying text.
     */
    public final static Font defaultFont = new Font("Arial", Font.PLAIN, 50);

    /**
     * The size of UDP packets.
     */
    public final static int NET_PACKET_SIZE;
    /**
     * The current version of JGame. (dev19.05.2)
     */
    public final static Version JGAME_VERSION = new Version(JGameStr.getString("versionFormat"),
            (byte) 0, (byte) 19, (byte) 5, (byte) 18);

    /**
     * Preset for quality render hints.
     */
    public final static RenderingHints QUALITY_RENDER_HINTS = new RenderingHints(
            new HashMap<>() {{
                put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

                put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                // Disabled temporarily until I fix java.lang.IllegalArgumentException:
                //      LCD VRGB antialiasing text mode is not compatible with Text-specific LCD contrast key
                //put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                // Disabled due to it being documented @since 1.9+
                // put(RenderingHints.KEY_RESOLUTION_VARIANT, RenderingHints.VALUE_RESOLUTION_VARIANT_SIZE_FIT);

                put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }});

    /**
     * Preset for speedy render hints
     */
    public final static RenderingHints SPEED_RENDER_HINTS = new RenderingHints(
            new HashMap<>() {{
                put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
                put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);

                put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
                put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
                put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

                // Disabled temporarily until I fix java.lang.IllegalArgumentException:
                //      LCD VRGB antialiasing text mode is not compatible with Text-specific LCD contrast key
                //put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                // Disabled due to it being documented @since 1.9+
                //put(RenderingHints.KEY_RESOLUTION_VARIANT, RenderingHints.VALUE_RESOLUTION_VARIANT_SIZE_FIT);

                put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }});

    static {
        int packetSize;
        try {
            packetSize = Integer.valueOf(settings.get("net.packetSize"));
        } catch (NumberFormatException e) {
            packetSize = 65535;  // Default packet size
        }
        NET_PACKET_SIZE = packetSize;
    }


    /**
     * The current render hints being used.
     */
    public final static RenderingHints RENDER_HINTS;

    static {
        String rh = settings.get("renderHints");

        // Default to speed render hints.
        RENDER_HINTS = rh.equals("quality") ? QUALITY_RENDER_HINTS :
                (rh.equals("custom") ? CUSTOM_RENDER_HINTS : SPEED_RENDER_HINTS);
    }

    /**
     * The builtin actions. All user defined actions should never be in the range of 0x5200-0x52FF.
     */
    public final static HashMap<String, Integer> BUILTIN_ACTIONS = new HashMap<>() {{
        put("clientShutdown", 0x52_10);
        put("serverShutdown", 0x52_11);
        put("verifySend", 0x52_20);
        put("confirmPacket", 0x52_21);
        put("kick", 0x52_00);
        put("filler", 0x52_01);
    }};
}
