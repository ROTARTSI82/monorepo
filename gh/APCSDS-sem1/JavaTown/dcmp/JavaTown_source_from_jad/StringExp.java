// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StringExp.java


public class StringExp
    implements Expression
{

    public StringExp(String s)
    {
        value = s;
    }

    public Object eval(Environment environment)
    {
        return value;
    }

    private String value;
}
