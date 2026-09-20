/*
 * Decompiled with CFR 0.150.
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class TextBox {
    public static final int SMALL_FONT = 12;
    public static final int LARGE_FONT = 14;
    public static final int ROUND = 0;
    public static final int SQUARE = 1;
    private int shape;
    private Color backColor;
    private int left;
    private int top;
    private List<TextLine> lines = new ArrayList<TextLine>();
    private int width;
    private int height;

    public TextBox(int n, Color color) {
        this(n, color, 0, 0);
    }

    public TextBox(int n, Color color, int n2, int n3) {
        this(n, color, n2, n3, -1, -1);
    }

    public TextBox(int n, Color color, int n2, int n3, int n4, int n5) {
        this.shape = n;
        this.backColor = color;
        this.left = n2;
        this.top = n3;
        this.width = n4;
        this.height = n5;
    }

    public void draw(Graphics2D graphics2D) {
        Object object;
        Object object2;
        int n;
        int n2;
        Object object3;
        Font font = graphics2D.getFont();
        if (this.width == -1) {
            object3 = this.lines.iterator();
            n2 = 0;
            n = 0;
            while (object3.hasNext()) {
                object2 = object3.next();
                Font font2 = font.deriveFont(((TextLine)object2).getStyle(), ((TextLine)object2).getSize());
                graphics2D.setFont(font2);
                object = graphics2D.getFontMetrics();
                int n3 = ((FontMetrics)object).stringWidth(((TextLine)object2).getText());
                if (n3 > n2) {
                    n2 = n3;
                }
                n += ((FontMetrics)object).getAscent() + ((FontMetrics)object).getDescent() - 2;
            }
            n += 4;
            n2 += 4;
        } else {
            n2 = this.width;
            n = this.height;
        }
        object3 = this.shape == 0 ? new RoundRectangle2D.Double(this.left, this.top, n2, n, 10.0, 10.0) : new Rectangle2D.Double(this.left, this.top, n2, n);
        graphics2D.setPaint(this.backColor);
        graphics2D.fill((Shape)object3);
        graphics2D.setPaint(Color.BLACK);
        graphics2D.draw((Shape)object3);
        object2 = this.lines.iterator();
        int n4 = this.top;
        while (object2.hasNext()) {
            object = (TextLine)object2.next();
            Font font3 = font.deriveFont(((TextLine)object).getStyle(), ((TextLine)object).getSize());
            graphics2D.setFont(font3);
            FontMetrics fontMetrics = graphics2D.getFontMetrics();
            String string = ((TextLine)object).getText();
            graphics2D.setPaint(((TextLine)object).getColor());
            graphics2D.drawString(string, this.left + 2, n4 += fontMetrics.getAscent());
            n4 += fontMetrics.getDescent() - 2;
        }
        graphics2D.setFont(font);
    }

    public void addText(Color color, int n, int n2, String string) {
        this.lines.add(new TextLine(color, n, n2, string));
    }

    public void setPosition(int n, int n2) {
        this.left = n;
        this.top = n2;
    }
}

