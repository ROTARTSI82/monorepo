package io.github.jgame;

import java.awt.*;
import java.util.HashMap;

public class Constants {
    public final static int NET_PACKET_SIZE = 65535;
    public final static boolean SILENCE_AWT_LOGS = true;

    public final static RenderingHints QUALITY_RENDER_HINTS = new RenderingHints(
            new HashMap<RenderingHints.Key, Object>() {{
                put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

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
            new HashMap<RenderingHints.Key, Object>() {{
                put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
                put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

                put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
                put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);

                put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
                put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
                put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

                // Disabled temporarily until I fix java.lang.IllegalArgumentException:
                //      LCD VRGB antialiasing text mode is not compatible with Text-specific LCD contrast key

                //put(RenderingHints.KEY_TEXT_LCD_CONTRAST, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                // Disabled due to it being documented @since 1.9+
                // put(RenderingHints.KEY_RESOLUTION_VARIANT, RenderingHints.VALUE_RESOLUTION_VARIANT_SIZE_FIT);

                put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            }});
    public final static RenderingHints RENDER_HINTS = QUALITY_RENDER_HINTS;
}
