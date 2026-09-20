// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Invocation.java

import java.util.List;

public class Invocation
    implements Statement, Expression
{

    public Invocation(Expression expression, String s, List list)
    {
        target = expression;
        message = s;
        args = list;
    }

    public void exec(Environment environment)
    {
        eval(environment);
    }

    public Object eval(Environment environment)
    {
        Object obj = target.eval(environment);
        if(obj == null)
            throw new JavaTownException("Null Pointer Exception:\nAttempt to pass message to null");
        if(!(obj instanceof Person))
            throw new JavaTownException((new StringBuilder()).append("Attempt to pass message to primitive:  ").append(obj).toString());
        else
            return World.invoke((Person)obj, message, args, environment);
    }

    private Expression target;
    private String message;
    private List args;
}
