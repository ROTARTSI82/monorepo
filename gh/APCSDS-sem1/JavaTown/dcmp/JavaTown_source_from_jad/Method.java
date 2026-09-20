// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Method.java

import java.util.*;

public class Method extends Declaration
{

    public Method(int i, String s, List list, Block block)
    {
        access = i;
        name = s;
        params = list;
        body = block;
    }

    public String getParam(int i)
    {
        return (String)params.get(i);
    }

    public Iterator body()
    {
        return body.getStatements().iterator();
    }

    public String getName()
    {
        return name;
    }

    public int getParamCount()
    {
        return params.size();
    }

    public boolean isPublic()
    {
        return access == 0;
    }

    public static final int PUBLIC = 0;
    public static final int PRIVATE = 1;
    private int access;
    private String name;
    private List params;
    private Block body;
}
