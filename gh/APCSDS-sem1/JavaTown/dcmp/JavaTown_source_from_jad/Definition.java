// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Definition.java

import java.util.List;

public class Definition
    implements Statement
{

    public Definition(String s, List list)
    {
        name = s;
        decls = list;
    }

    public void exec(Environment environment)
    {
        World.addClass(new Type(name, decls));
    }

    private String name;
    private List decls;
}
