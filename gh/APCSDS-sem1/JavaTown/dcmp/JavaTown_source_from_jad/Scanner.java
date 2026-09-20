// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Scanner.java

import java.io.IOException;
import java.io.Reader;

public class Scanner
{

    public Scanner(Reader reader)
    {
        in = reader;
        recent = "";
        advance();
    }

    private void advance()
    {
        int i = in.read();
        if(i == -1)
            ch = '\0';
        else
            ch = (char)i;
        new StringBuilder();
        this;
        JVM INSTR dup_x1 ;
        recent;
        append();
        ch;
        append();
        toString();
        recent;
        for(; recent.length() > 100 && recent.indexOf("\n") != -1; recent = recent.substring(recent.indexOf("\n") + 1));
        break MISSING_BLOCK_LABEL_118;
        IOException ioexception;
        ioexception;
        throw new RuntimeException(ioexception);
    }

    private void eat(char c)
    {
        if(ch == c)
            advance();
        else
            throw new JavaTownParseException((new StringBuilder()).append("expected ").append(c).append(" but found ").append(ch).toString());
    }

    private boolean isLetter()
    {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_';
    }

    private boolean isDigit()
    {
        return ch >= '0' && ch <= '9';
    }

    public String next()
    {
        while(ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t') 
            eat(ch);
        String s = "";
        if(ch == '/')
        {
            eat('/');
            if(ch == '/')
            {
                while(ch != '\n' && ch != 0) 
                    eat(ch);
                return next();
            } else
            {
                return "/";
            }
        }
        if(isLetter())
        {
            for(; isLetter() || isDigit(); eat(ch))
                s = (new StringBuilder()).append(s).append(ch).toString();

            return s;
        }
        if(isDigit())
        {
            for(; isDigit(); eat(ch))
                s = (new StringBuilder()).append(s).append(ch).toString();

            return s;
        }
        if("=!<>".indexOf((new StringBuilder()).append(ch).append("").toString()) != -1)
        {
            s = (new StringBuilder()).append(s).append(ch).toString();
            eat(ch);
            if(ch == '=')
            {
                s = (new StringBuilder()).append(s).append('=').toString();
                eat('=');
            }
            return s;
        }
        if(ch == '|')
        {
            eat('|');
            eat('|');
            return "||";
        }
        if(ch == '&')
        {
            eat('&');
            eat('&');
            return "&&";
        }
        if(ch == 0)
        {
            eat('\0');
            return " ";
        }
        if("{};.(),+-*/%".indexOf((new StringBuilder()).append("").append(ch).toString()) != -1)
        {
            s = (new StringBuilder()).append(s).append(ch).toString();
            eat(ch);
            return s;
        }
        if(ch == '"')
        {
            s = (new StringBuilder()).append(s).append('"').toString();
            eat('"');
            while(ch != '"' && ch != 0) 
            {
                s = (new StringBuilder()).append(s).append(ch).toString();
                eat(ch);
            }
            if(ch == 0)
            {
                throw new JavaTownParseException("missing closing quotation mark on string");
            } else
            {
                s = (new StringBuilder()).append(s).append('"').toString();
                eat('"');
                return s;
            }
        } else
        {
            throw new JavaTownParseException((new StringBuilder()).append("invalid character ").append(ch).toString());
        }
    }

    public String getRecent()
    {
        return recent;
    }

    private Reader in;
    private char ch;
    private String recent;
}
