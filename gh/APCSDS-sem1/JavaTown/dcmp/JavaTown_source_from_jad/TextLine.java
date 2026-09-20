// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TextLine.java

import java.awt.Color;

public class TextLine
{

    public TextLine(Color color1, int i, int j, String s)
    {
        color = color1;
        style = i;
        size = j;
        text = s;
    }

    public Color getColor()
    {
        return color;
    }

    public int getStyle()
    {
        return style;
    }

    public int getSize()
    {
        return size;
    }

    public String getText()
    {
        return text;
    }

    private Color color;
    private int style;
    private int size;
    private String text;
}
