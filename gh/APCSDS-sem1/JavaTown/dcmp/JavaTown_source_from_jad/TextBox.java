// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TextBox.java

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

public class TextBox
{

    public TextBox(int i, Color color)
    {
        this(i, color, 0, 0);
    }

    public TextBox(int i, Color color, int j, int k)
    {
        this(i, color, j, k, -1, -1);
    }

    public TextBox(int i, Color color, int j, int k, int l, int i1)
    {
        lines = new ArrayList();
        shape = i;
        backColor = color;
        left = j;
        top = k;
        width = l;
        height = i1;
    }

    public void draw(Graphics2D graphics2d)
    {
        Font font = graphics2d.getFont();
        int i;
        int j;
        if(width == -1)
        {
            Iterator iterator = lines.iterator();
            i = 0;
            FontMetrics fontmetrics;
            for(j = 0; iterator.hasNext(); j += (fontmetrics.getAscent() + fontmetrics.getDescent()) - 2)
            {
                TextLine textline = (TextLine)iterator.next();
                Font font1 = font.deriveFont(textline.getStyle(), textline.getSize());
                graphics2d.setFont(font1);
                fontmetrics = graphics2d.getFontMetrics();
                int l = fontmetrics.stringWidth(textline.getText());
                if(l > i)
                    i = l;
            }

            j += 4;
            i += 4;
        } else
        {
            i = width;
            j = height;
        }
        Object obj;
        if(shape == 0)
            obj = new java.awt.geom.RoundRectangle2D.Double(left, top, i, j, 10D, 10D);
        else
            obj = new java.awt.geom.Rectangle2D.Double(left, top, i, j);
        graphics2d.setPaint(backColor);
        graphics2d.fill(((java.awt.Shape) (obj)));
        graphics2d.setPaint(Color.BLACK);
        graphics2d.draw(((java.awt.Shape) (obj)));
        Iterator iterator1 = lines.iterator();
        FontMetrics fontmetrics1;
        for(int k = top; iterator1.hasNext(); k += fontmetrics1.getDescent() - 2)
        {
            TextLine textline1 = (TextLine)iterator1.next();
            Font font2 = font.deriveFont(textline1.getStyle(), textline1.getSize());
            graphics2d.setFont(font2);
            fontmetrics1 = graphics2d.getFontMetrics();
            k += fontmetrics1.getAscent();
            String s = textline1.getText();
            graphics2d.setPaint(textline1.getColor());
            graphics2d.drawString(s, left + 2, k);
        }

        graphics2d.setFont(font);
    }

    public void addText(Color color, int i, int j, String s)
    {
        lines.add(new TextLine(color, i, j, s));
    }

    public void setPosition(int i, int j)
    {
        left = i;
        top = j;
    }

    public static final int SMALL_FONT = 12;
    public static final int LARGE_FONT = 14;
    public static final int ROUND = 0;
    public static final int SQUARE = 1;
    private int shape;
    private Color backColor;
    private int left;
    private int top;
    private java.util.List lines;
    private int width;
    private int height;
}
