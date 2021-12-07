// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Assignment.java


public class Assignment
    implements Statement
{

    public Assignment(String s, Expression expression)
    {
        var = s;
        exp = expression;
    }

    public void exec(Environment environment)
    {
        environment.set(var, exp.eval(environment));
        World.redraw();
    }

    private String var;
    private Expression exp;
}
