// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   If.java


public class If
    implements Statement
{

    public If(Expression expression, Statement statement, Statement statement1)
    {
        pred = expression;
        cons = statement;
        alt = statement1;
    }

    public void exec(Environment environment)
    {
        boolean flag = ((Boolean)pred.eval(environment)).booleanValue();
        if(flag)
            cons.exec(environment);
        else
            alt.exec(environment);
    }

    private Expression pred;
    private Statement cons;
    private Statement alt;
}
