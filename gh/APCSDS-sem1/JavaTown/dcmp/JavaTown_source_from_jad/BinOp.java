// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BinOp.java


public class BinOp
    implements Expression
{

    public BinOp(char c, Expression expression, Expression expression1)
    {
        op = c;
        exp1 = expression;
        exp2 = expression1;
    }

    public Object eval(Environment environment)
    {
        Object obj;
        Object obj1;
        obj = exp1.eval(environment);
        if((obj instanceof Boolean) && op == '&')
            if(obj.equals(Boolean.FALSE))
                return Boolean.FALSE;
            else
                return exp2.eval(environment);
        if((obj instanceof Boolean) && op == '|')
            if(obj.equals(Boolean.TRUE))
                return Boolean.TRUE;
            else
                return exp2.eval(environment);
        obj1 = exp2.eval(environment);
        if(op != '+')
            break MISSING_BLOCK_LABEL_224;
        if((obj instanceof String) || (obj1 instanceof String))
            return (new StringBuilder()).append("").append(obj).append(obj1).toString();
        if((obj instanceof Integer) && (obj1 instanceof Integer))
            return new Integer(((Integer)obj).intValue() + ((Integer)obj1).intValue());
        throw new JavaTownException((new StringBuilder()).append("Cannot add ").append(obj).append(" and ").append(obj1).toString());
        if(op == '-')
            return new Integer(((Integer)obj).intValue() - ((Integer)obj1).intValue());
        if(op == '*')
            return new Integer(((Integer)obj).intValue() * ((Integer)obj1).intValue());
        if(op == '/')
            return new Integer(((Integer)obj).intValue() / ((Integer)obj1).intValue());
        if(op == '%')
            return new Integer(((Integer)obj).intValue() % ((Integer)obj1).intValue());
        if(op != '=')
            break MISSING_BLOCK_LABEL_408;
        if((obj instanceof Integer) || (obj instanceof Boolean))
            return obj.equals(obj1) ? Boolean.TRUE : Boolean.FALSE;
        return obj != obj1 ? Boolean.FALSE : Boolean.TRUE;
        if(op != '!')
            break MISSING_BLOCK_LABEL_464;
        if((obj instanceof Integer) || (obj instanceof Boolean))
            return obj.equals(obj1) ? Boolean.FALSE : Boolean.TRUE;
        return obj != obj1 ? Boolean.TRUE : Boolean.FALSE;
        if(op == '>')
            return ((Integer)obj).intValue() <= ((Integer)obj1).intValue() ? Boolean.FALSE : Boolean.TRUE;
        if(op == ']')
            return ((Integer)obj).intValue() < ((Integer)obj1).intValue() ? Boolean.FALSE : Boolean.TRUE;
        if(op == '<')
            return ((Integer)obj).intValue() >= ((Integer)obj1).intValue() ? Boolean.FALSE : Boolean.TRUE;
        if(op == '[')
            return ((Integer)obj).intValue() > ((Integer)obj1).intValue() ? Boolean.FALSE : Boolean.TRUE;
        try
        {
            throw new JavaTownException((new StringBuilder()).append("Invalid operation:  ").append(op).toString());
        }
        catch(ArithmeticException arithmeticexception)
        {
            throw new JavaTownException(arithmeticexception.getMessage());
        }
    }

    public static final char PLUS = 43;
    public static final char MINUS = 45;
    public static final char TIMES = 42;
    public static final char DIVIDE = 47;
    public static final char MOD = 37;
    public static final char EQ = 61;
    public static final char NE = 33;
    public static final char AND = 38;
    public static final char OR = 124;
    public static final char LT = 60;
    public static final char LE = 91;
    public static final char GT = 62;
    public static final char GE = 93;
    private char op;
    private Expression exp1;
    private Expression exp2;
}
