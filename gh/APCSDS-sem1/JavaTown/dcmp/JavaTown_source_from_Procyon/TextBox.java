import java.io.Serializable;
import java.awt.FontMetrics;
import java.util.Iterator;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

// 
// Decompiled by Procyon v0.5.36
// 

public class TextBox
{
    public static final int SMALL_FONT = 12;
    public static final int LARGE_FONT = 14;
    public static final int ROUND = 0;
    public static final int SQUARE = 1;
    private int shape;
    private Color backColor;
    private int left;
    private int top;
    private List<TextLine> lines;
    private int width;
    private int height;
    
    public TextBox(final int n, final Color color) {
        this(n, color, 0, 0);
    }
    
    public TextBox(final int n, final Color color, final int n2, final int n3) {
        this(n, color, n2, n3, -1, -1);
    }
    
    public TextBox(final int shape, final Color backColor, final int left, final int top, final int width, final int height) {
        this.lines = new ArrayList<TextLine>();
        this.shape = shape;
        this.backColor = backColor;
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }
    
    public void draw(final Graphics2D graphics2D) {
        final Font font = graphics2D.getFont();
        int width;
        int height;
        if (this.width == -1) {
            final Iterator<TextLine> iterator = this.lines.iterator();
            width = 0;
            height = 0;
            while (iterator.hasNext()) {
                final TextLine textLine = iterator.next();
                graphics2D.setFont(font.deriveFont(textLine.getStyle(), (float)textLine.getSize()));
                final FontMetrics fontMetrics = graphics2D.getFontMetrics();
                final int stringWidth = fontMetrics.stringWidth(textLine.getText());
                if (stringWidth > width) {
                    width = stringWidth;
                }
                height += fontMetrics.getAscent() + fontMetrics.getDescent() - 2;
            }
            height += 4;
            width += 4;
        }
        else {
            width = this.width;
            height = this.height;
        }
        Serializable s;
        if (this.shape == 0) {
            s = new RoundRectangle2D.Double(this.left, this.top, width, height, 10.0, 10.0);
        }
        else {
            s = new Rectangle2D.Double(this.left, this.top, width, height);
        }
        graphics2D.setPaint(this.backColor);
        graphics2D.fill((Shape)s);
        graphics2D.setPaint(Color.BLACK);
        graphics2D.draw((Shape)s);
        final Iterator<TextLine> iterator2 = this.lines.iterator();
        int top = this.top;
        while (iterator2.hasNext()) {
            final TextLine textLine2 = iterator2.next();
            graphics2D.setFont(font.deriveFont(textLine2.getStyle(), (float)textLine2.getSize()));
            final FontMetrics fontMetrics2 = graphics2D.getFontMetrics();
            final int n = top + fontMetrics2.getAscent();
            final String text = textLine2.getText();
            graphics2D.setPaint(textLine2.getColor());
            graphics2D.drawString(text, this.left + 2, n);
            top = n + (fontMetrics2.getDescent() - 2);
        }
        graphics2D.setFont(font);
    }
    
    public void addText(final Color color, final int n, final int n2, final String s) {
        this.lines.add(new TextLine(color, n, n2, s));
    }
    
    public void setPosition(final int left, final int top) {
        this.left = left;
        this.top = top;
    }
}
