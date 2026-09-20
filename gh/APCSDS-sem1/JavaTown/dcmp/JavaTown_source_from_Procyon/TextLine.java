import java.awt.Color;

// 
// Decompiled by Procyon v0.5.36
// 

public class TextLine
{
    private Color color;
    private int style;
    private int size;
    private String text;
    
    public TextLine(final Color color, final int style, final int size, final String text) {
        this.color = color;
        this.style = style;
        this.size = size;
        this.text = text;
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
