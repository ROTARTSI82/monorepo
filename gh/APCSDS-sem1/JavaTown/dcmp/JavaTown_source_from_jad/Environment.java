// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Environment.java

import java.util.*;

public class Environment
{

    public Environment(String s, Environment environment, Person person)
    {
        vars = new ArrayList();
        values = new HashMap();
        name = s;
        parent = environment;
        owner = person;
    }

    public void set(String s, Object obj)
    {
        if(values.containsKey(s))
            values.put(s, obj);
        else
        if(parent != null && parent.containsVar(s))
        {
            parent.set(s, obj);
        } else
        {
            vars.add(s);
            values.put(s, obj);
        }
    }

    public void define(String s, Object obj)
    {
        vars.add(s);
        values.put(s, obj);
    }

    public Iterator variables()
    {
        return vars.iterator();
    }

    public Object get(String s)
    {
        if(containsVar(s))
            return values.get(s);
        if(parent == null)
            throw new JavaTownException((new StringBuilder()).append("Unbound variable:  ").append(s).toString());
        else
            return parent.get(s);
    }

    public boolean containsVar(String s)
    {
        return values.containsKey(s);
    }

    public String getName()
    {
        return name;
    }

    public Person getOwner()
    {
        return owner;
    }

    public void clear()
    {
        vars = new ArrayList();
        values = new HashMap();
    }

    private List vars;
    private Map values;
    private Environment parent;
    private String name;
    private Person owner;
}
