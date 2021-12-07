/*
 * Decompiled with CFR 0.150.
 */
import java.awt.Color;

public class TextLine {
    private Color color;
    private int style;
    private int size;
    private String text;

    public TextLine(Color color, int n, int n2, String string) {
        this.color = color;
        this.style = n;
        this.size = n2;
        this.text = string;
    }

    public Color getColor() {
        return this.color;
    }

    public int getStyle() {
        return this.style;
    }

    public int getSize() {
        return this.size;
    }

    public String getText() {
        return this.text;
    }
}

