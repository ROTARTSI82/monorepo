package io.github.jgame;

import io.github.jgame.util.StringManager;

import java.awt.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class Constants {
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
        JGameStr = new StringManager("assets.lang.JGame", locale);  // Remember lang.
    }

    public final static int NET_PACKET_SIZE;
    public final static boolean BLOCK_LOGS = Boolean.valueOf(settings.getString("logging.GenericLogger.blockLoggers"));

    static {
        int packetSize;
        try {
            packetSize = Integer.valueOf(settings.getString("net.packetSize"));
        } catch (NumberFormatException e) {
            packetSize = 65535;  // Default packet size
        }
        NET_PACKET_SIZE = packetSize;
    }

    public final static Version JGAME_VERSION = new Version(JGameStr.getString("versionFormat"),
            (byte) 0, (byte) 19, (byte) 2, (byte) 24);

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

    public final static RenderingHints CUSTOM_RENDER_HINTS = new RenderingHints(
            new HashMap<>() {{
                put(RenderingHints.KEY_ALPHA_INTERPOLATION, settings.getString("rh.alphaInterpolation").equals("quality") ?
                        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY : RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

                put(RenderingHints.KEY_INTERPOLATION, settings.getString("rh.interpolation").equals("bilinear") ?
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR : RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                put(RenderingHints.KEY_RENDERING, settings.getString("rh.rendering").equals("quality") ?
                        RenderingHints.VALUE_RENDER_QUALITY : RenderingHints.VALUE_RENDER_SPEED);

                put(RenderingHints.KEY_COLOR_RENDERING, settings.getString("rh.colorRender").equals("quality") ?
                        RenderingHints.VALUE_COLOR_RENDER_QUALITY : RenderingHints.VALUE_COLOR_RENDER_SPEED);

                put(RenderingHints.KEY_DITHERING, settings.getString("rh.dither").equals("enable") ?
                        RenderingHints.VALUE_DITHER_ENABLE : RenderingHints.VALUE_DITHER_DISABLE);

                put(RenderingHints.KEY_FRACTIONALMETRICS, settings.getString("rh.fractionalmetrics").equals("on") ?
                        RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

                put(RenderingHints.KEY_STROKE_CONTROL, settings.getString("rh.strokeControl").equals("pure") ?
                        RenderingHints.VALUE_STROKE_PURE : RenderingHints.VALUE_STROKE_NORMALIZE);

                put(RenderingHints.KEY_TEXT_ANTIALIASING, settings.getString("rh.textAntialias").equals("on") ?
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

                put(RenderingHints.KEY_ANTIALIASING, settings.getString("rh.antialias").equals("on") ?
                        RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
            }});


    public final static RenderingHints RENDER_HINTS;

    static {
        String rh = settings.getString("renderHints");

        // Default to speed render hints.
        RENDER_HINTS = rh.equals("quality") ? QUALITY_RENDER_HINTS :
                (rh.equals("custom") ? CUSTOM_RENDER_HINTS : SPEED_RENDER_HINTS);
    }

    public final static HashMap<String, Integer> BUILTIN_ACTIONS = new HashMap<>() {{
        put("clientShutdown", 0x52_10);
        put("serverShutdown", 0x52_11);
        put("verifySend", 0x52_20);
        put("confirmPacket", 0x52_21);
        put("kick", 0x52_00);
        put("filler", 0x52_01);
    }};
}
