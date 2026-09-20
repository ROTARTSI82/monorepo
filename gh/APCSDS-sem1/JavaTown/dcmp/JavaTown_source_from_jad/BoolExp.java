// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BoolExp.java


public class BoolExp
    implements Expression
{

    public BoolExp(boolean flag)
    {
        value = flag;
    }

    public Object eval(Environment environment)
    {
        return value ? Boolean.TRUE : Boolean.FALSE;
    }

    private boolean value;
}
