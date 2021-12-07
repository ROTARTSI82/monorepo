// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Location.java


public class Location
{

    public Location(int i, int j)
    {
        row = i;
        col = j;
        address = 2 * i + j + 1;
    }

    public int row()
    {
        return row;
    }

    public int col()
    {
        return col;
    }

    public String toString()
    {
        return (new StringBuilder()).append("#").append(address).toString();
    }

    private int row;
    private int col;
    private int address;
}
