// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Field.java


public class Field extends Declaration
{

    public Field(String s)
    {
        name = s;
    }

    public String getName()
    {
        return name;
    }

    private String name;
}
