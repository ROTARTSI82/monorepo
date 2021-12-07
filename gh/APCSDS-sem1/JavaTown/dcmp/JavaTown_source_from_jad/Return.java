// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Return.java


public class Return
    implements Statement
{

    public Return(Expression expression)
    {
        exp = expression;
    }

    public void exec(Environment environment)
    {
        Object obj = exp.eval(environment);
        throw new ReturnException(obj);
    }

    private Expression exp;
}
