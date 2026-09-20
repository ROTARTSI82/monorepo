// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Block.java

import java.util.ArrayList;
import java.util.Iterator;

public class Block
    implements Statement
{

    public Block(ArrayList arraylist)
    {
        statements = arraylist;
    }

    public ArrayList getStatements()
    {
        return statements;
    }

    public void exec(Environment environment)
    {
        Statement statement;
        for(Iterator iterator = statements.iterator(); iterator.hasNext(); statement.exec(environment))
            statement = (Statement)iterator.next();

    }

    private ArrayList statements;
}
