// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   IntExp.java


public class IntExp
    implements Expression
{

    public IntExp(int i)
    {
        value = i;
    }

    public Object eval(Environment environment)
    {
        return new Integer(value);
    }

    private int value;
}
