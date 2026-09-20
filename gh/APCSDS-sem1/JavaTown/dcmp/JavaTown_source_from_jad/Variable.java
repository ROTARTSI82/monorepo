// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Variable.java


public class Variable
    implements Expression
{

    public Variable(String s)
    {
        name = s;
    }

    public Object eval(Environment environment)
    {
        return environment.get(name);
    }

    private String name;
}
