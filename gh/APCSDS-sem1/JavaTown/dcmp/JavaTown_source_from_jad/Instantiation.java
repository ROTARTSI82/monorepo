// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Instantiation.java

import java.util.List;

public class Instantiation
    implements Expression
{

    public Instantiation(String s, List list)
    {
        name = s;
        args = list;
    }

    public Object eval(Environment environment)
    {
        return World.instantiate(name, args, environment);
    }

    private String name;
    private List args;
}
