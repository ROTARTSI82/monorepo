package p000;

import java.awt.Color;

/* renamed from: TextLine */
public class TextLine {
    private Color color;
    private int size;
    private int style;
    private String text;

    public TextLine(Color color2, int i, int i2, String str) {
        this.color = color2;
        this.style = i;
        this.size = i2;
        this.text = str;
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
