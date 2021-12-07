// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Type.java

import java.util.*;

public class Type
{

    public Type(String s, List list)
    {
        fields = new ArrayList();
        methods = new HashSet();
        name = s;
        Iterator iterator = list.iterator();
        boolean flag = false;
        while(iterator.hasNext()) 
        {
            Declaration declaration = (Declaration)iterator.next();
            if(declaration instanceof Field)
                fields.add((Field)declaration);
            else
            if(declaration instanceof Method)
            {
                Method method = (Method)declaration;
                if(method.getName().equals(s))
                    flag = true;
                methods.add(method);
            } else
            {
                throw new JavaTownException((new StringBuilder()).append("Invalid declaration:  ").append(declaration).toString());
            }
        }
        if(!flag)
            methods.add(new Method(0, s, new ArrayList(), new Block(new ArrayList())));
    }

    public String getName()
    {
        return name;
    }

    public Iterator fields()
    {
        return fields.iterator();
    }

    public Method getMethod(String s, int i)
    {
        for(Iterator iterator = methods.iterator(); iterator.hasNext();)
        {
            Method method = (Method)iterator.next();
            if(method.getName().equals(s) && method.getParamCount() == i)
                return method;
        }

        throw new JavaTownException((new StringBuilder()).append("No method ").append(s).append(" with ").append(i).append(" parameters").toString());
    }

    private String name;
    private List fields;
    private Set methods;
}
