package io.github.jgame;

import java.awt.*;
import java.util.HashMap;

import static io.github.jgame.util.UniversalResources.JGameStr;
import static io.github.jgame.util.UniversalResources.settings;

public class Constants {
    public final static int NET_PACKET_SIZE;
    public final static boolean SILENCE_AWT_LOGS = Boolean.valueOf(settings.getString("silenceAWT"));

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
    public final static RenderingHints RENDER_HINTS = settings.getString("renderHints").equals("quality") ?
            QUALITY_RENDER_HINTS : SPEED_RENDER_HINTS;

    public final static HashMap<String, Integer> BUILTIN_ACTIONS = new HashMap<>() {{
        put("clientShutdown", 0x52_10);
        put("serverShutdown", 0x52_11);
        put("verifySend", 0x52_20);
        put("confirmPacket", 0x52_21);
        put("kick", 0x52_00);
        put("filler", 0x52_01);
    }};
}
