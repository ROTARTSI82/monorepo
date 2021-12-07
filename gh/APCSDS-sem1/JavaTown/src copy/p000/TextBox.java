package p000;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: TextBox */
public class TextBox {
    public static final int LARGE_FONT = 14;
    public static final int ROUND = 0;
    public static final int SMALL_FONT = 12;
    public static final int SQUARE = 1;
    private Color backColor;
    private int height;
    private int left;
    private List<TextLine> lines;
    private int shape;
    private int top;
    private int width;

    public TextBox(int i, Color color) {
        this(i, color, 0, 0);
    }

    public TextBox(int i, Color color, int i2, int i3) {
        this(i, color, i2, i3, -1, -1);
    }

    public TextBox(int i, Color color, int i2, int i3, int i4, int i5) {
        this.lines = new ArrayList();
        this.shape = i;
        this.backColor = color;
        this.left = i2;
        this.top = i3;
        this.width = i4;
        this.height = i5;
    }

    public void draw(Graphics2D graphics2D) {
        int i;
        int i2;
        Shape doubleR;
        int i3;
        Font font = graphics2D.getFont();
        if (this.width == -1) {
            Iterator<TextLine> it = this.lines.iterator();
            int i4 = 0;
            int i5 = 0;
            while (true) {
                i3 = i5;
                if (!it.hasNext()) {
                    break;
                }
                TextLine next = it.next();
                graphics2D.setFont(font.deriveFont(next.getStyle(), (float) next.getSize()));
                FontMetrics fontMetrics = graphics2D.getFontMetrics();
                int stringWidth = fontMetrics.stringWidth(next.getText());
                if (stringWidth > i4) {
                    i4 = stringWidth;
                }
                i5 = ((fontMetrics.getAscent() + fontMetrics.getDescent()) - 2) + i3;
            }
            i = i3 + 4;
            i2 = i4 + 4;
        } else {
            int i6 = this.width;
            i = this.height;
            i2 = i6;
        }
        if (this.shape == 0) {
            doubleR = new RoundRectangle2D.Double((double) this.left, (double) this.top, (double) i2, (double) i, 10.0d, 10.0d);
        } else {
            doubleR = new Rectangle2D.Double((double) this.left, (double) this.top, (double) i2, (double) i);
        }
        graphics2D.setPaint(this.backColor);
        graphics2D.fill(doubleR);
        graphics2D.setPaint(Color.BLACK);
        graphics2D.draw(doubleR);
        Iterator<TextLine> it2 = this.lines.iterator();
        int i7 = this.top;
        while (true) {
            int i8 = i7;
            if (it2.hasNext()) {
                TextLine next2 = it2.next();
                graphics2D.setFont(font.deriveFont(next2.getStyle(), (float) next2.getSize()));
                FontMetrics fontMetrics2 = graphics2D.getFontMetrics();
                int ascent = i8 + fontMetrics2.getAscent();
                String text = next2.getText();
                graphics2D.setPaint(next2.getColor());
                graphics2D.drawString(text, this.left + 2, ascent);
                i7 = (fontMetrics2.getDescent() - 2) + ascent;
            } else {
                graphics2D.setFont(font);
                return;
            }
        }
    }

    public void addText(Color color, int i, int i2, String str) {
        this.lines.add(new TextLine(color, i, i2, str));
    }

    public void setPosition(int i, int i2) {
        this.left = i;
        this.top = i2;
    }
}
