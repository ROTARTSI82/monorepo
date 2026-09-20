// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ReturnException.java


public class ReturnException extends RuntimeException
{

    public ReturnException(Object obj)
    {
        value = obj;
    }

    public Object getValue()
    {
        return value;
    }

    private Object value;
}
