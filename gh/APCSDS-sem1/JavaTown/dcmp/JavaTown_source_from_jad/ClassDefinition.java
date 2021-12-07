// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ClassDefinition.java

import java.util.Set;

public class ClassDefinition extends Statement
{

    public ClassDefinition(String s, Set set)
    {
        name = s;
    }

    public void exec(Environment environment)
    {
        Office.addClass(new WorkerClass(name));
    }

    private String name;
}
