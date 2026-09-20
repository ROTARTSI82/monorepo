// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Not.java


public class Not
    implements Expression
{

    public Not(Expression expression)
    {
        exp = expression;
    }

    public Object eval(Environment environment)
    {
        Object obj = exp.eval(environment);
        if(!(obj instanceof Boolean))
            throw new JavaTownException((new StringBuilder()).append("Cannot negate non-boolean value:  ").append(obj).toString());
        if(((Boolean)obj).booleanValue())
            return Boolean.FALSE;
        else
            return Boolean.TRUE;
    }

    private Expression exp;
}
