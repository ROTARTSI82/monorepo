package io.github.jgame;

import java.awt.*;
import java.util.HashMap;

public class Constants {
    public static int NET_PACKET_SIZE = 65535;
    public static boolean SILENCE_AWT_LOGS = true;

    public static RenderingHints RENDER_HINTS = new RenderingHints(new HashMap<RenderingHints.Key, Object>() {{
        put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }});
}
