// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Parser.java

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;

public class Parser
{

    public Parser(Scanner scanner1)
    {
        scanner = scanner1;
        stack = new Stack();
        token = scanner1.next();
    }

    public ArrayList parseStatements()
    {
        ArrayList arraylist = new ArrayList();
        for(; !token.equals(" "); arraylist.add(parseStatement()));
        return arraylist;
    }

    private void eat(String s)
    {
        if(token.equals(s) || token.equals(" ") && s.equals(";"))
        {
            if(stack.isEmpty())
                token = scanner.next();
            else
                token = (String)stack.pop();
        } else
        {
            throw new JavaTownParseException((new StringBuilder()).append("expected ").append(s).append(" but found ").append(token).toString());
        }
    }

    private void barf(String s)
    {
        stack.push(token);
        token = s;
    }

    private Statement parseStatement()
    {
        System.out.println((new StringBuilder()).append("parseStatement:  ").append(token).toString());
        if(token.equals("public"))
        {
            eat("public");
            eat("class");
            String s = token;
            eat(s);
            eat("{");
            ArrayList arraylist = parseDeclarations();
            eat("}");
            return new Definition(s, arraylist);
        }
        if(token.equals("return"))
        {
            eat("return");
            Expression expression = parseExpression();
            eat(";");
            return new Return(expression);
        }
        if(token.equals("if"))
            return parseIf();
        if(token.equals("{"))
            return parseBlock();
        String s1 = token;
        eat(s1);
        if(token.equals("="))
        {
            String s2 = s1;
            eat("=");
            Expression expression1 = parseExpression();
            eat(";");
            return new Assignment(s2, expression1);
        }
        String s3 = token;
        eat(s3);
        if(token.equals("="))
        {
            String s4 = s3;
            eat("=");
            Expression expression2 = parseExpression();
            eat(";");
            return new Assignment(s4, expression2);
        }
        barf(s3);
        barf(s1);
        System.out.println((new StringBuilder()).append(token).append(" ").append(stack).toString());
        Invocation invocation;
        invocation = (Invocation)parseExpression();
        eat(";");
        return invocation;
        ClassCastException classcastexception;
        classcastexception;
        classcastexception.printStackTrace();
        throw new JavaTownParseException("invalid syntax");
    }

    private ArrayList parseDeclarations()
    {
        ArrayList arraylist = new ArrayList();
        for(; !token.equals("}"); arraylist.add(parseDeclaration()));
        return arraylist;
    }

    private Declaration parseDeclaration()
    {
        String s;
        String s3;
label0:
        {
            ArrayList arraylist;
label1:
            {
                s = token;
                eat(s);
                String s1 = token;
                eat(s1);
                if(token.equals(";") || token.equals("("))
                {
                    s3 = s1;
                } else
                {
                    s3 = token;
                    eat(s3);
                }
                if(!token.equals("("))
                    break label0;
                if(!s.equals("public") && !s.equals("private"))
                    throw new JavaTownParseException((new StringBuilder()).append("illegal start of declaration:  ").append(s).toString());
                eat("(");
                arraylist = new ArrayList();
label2:
                do
                {
                    for(; !token.equals(")"); eat(","))
                    {
                        String s2 = token;
                        eat(s2);
                        if(token.equals(",") || token.equals(")"))
                        {
                            arraylist.add(s2);
                        } else
                        {
                            arraylist.add(token);
                            eat(token);
                        }
                        if(!token.equals(","))
                            continue label2;
                    }

                    break label1;
                } while(token.equals(")"));
                throw new JavaTownParseException((new StringBuilder()).append("expected ) but found ").append(token).toString());
            }
            eat(")");
            return new Method(s.equals("public") ? 0 : 1, s3, arraylist, parseBlock());
        }
        if(!s.equals("private"))
        {
            throw new JavaTownParseException((new StringBuilder()).append("expected private but found ").append(token).toString());
        } else
        {
            eat(";");
            return new Field(s3);
        }
    }

    private Block parseBlock()
    {
        eat("{");
        ArrayList arraylist = new ArrayList();
        for(; !token.equals("}"); arraylist.add(parseStatement()));
        eat("}");
        return new Block(arraylist);
    }

    private Expression parseExpression()
    {
        Object obj;
        for(obj = parseAnded(); token.equals("||"); obj = new BinOp('|', ((Expression) (obj)), parseAnded()))
            eat("||");

        return ((Expression) (obj));
    }

    private Expression parseAnded()
    {
        Object obj;
        for(obj = parsePossibleEquality(); token.equals("&&"); obj = new BinOp('&', ((Expression) (obj)), parsePossibleEquality()))
            eat("&&");

        return ((Expression) (obj));
    }

    private Expression parsePossibleEquality()
    {
        Expression expression = parsePossibleComparison();
        if(token.equals("=="))
        {
            eat("==");
            return new BinOp('=', expression, parsePossibleComparison());
        }
        if(token.equals("!="))
        {
            eat("!=");
            return new BinOp('!', expression, parsePossibleComparison());
        } else
        {
            return expression;
        }
    }

    private Expression parsePossibleComparison()
    {
        Expression expression = parsePossibleSum();
        if(token.equals("<"))
        {
            eat("<");
            return new BinOp('<', expression, parsePossibleSum());
        }
        if(token.equals(">"))
        {
            eat(">");
            return new BinOp('>', expression, parsePossibleSum());
        }
        if(token.equals("<="))
        {
            eat("<=");
            return new BinOp('[', expression, parsePossibleSum());
        }
        if(token.equals(">="))
        {
            eat(">=");
            return new BinOp(']', expression, parsePossibleSum());
        } else
        {
            return expression;
        }
    }

    private ArrayList parseArguments()
    {
        ArrayList arraylist;
label0:
        {
            eat("(");
            arraylist = new ArrayList();
label1:
            do
            {
                for(; !token.equals(")"); eat(","))
                {
                    arraylist.add(parseExpression());
                    if(!token.equals(","))
                        continue label1;
                }

                break label0;
            } while(token.equals(")"));
            throw new JavaTownParseException((new StringBuilder()).append("expected ) but found ").append(token).toString());
        }
        eat(")");
        return arraylist;
    }

    private Expression parsePossibleSum()
    {
        Object obj = parseTerm();
        while(token.equals("+") || token.equals("-")) 
            if(token.equals("+"))
            {
                eat("+");
                obj = new BinOp('+', ((Expression) (obj)), parseTerm());
            } else
            {
                eat("-");
                obj = new BinOp('-', ((Expression) (obj)), parseTerm());
            }
        return ((Expression) (obj));
    }

    private Expression parseTerm()
    {
        Object obj = parseFactor();
        while(token.equals("*") || token.equals("/") || token.equals("%")) 
            if(token.equals("*"))
            {
                eat("*");
                obj = new BinOp('*', ((Expression) (obj)), parseFactor());
            } else
            if(token.equals("/"))
            {
                eat("/");
                obj = new BinOp('/', ((Expression) (obj)), parseFactor());
            } else
            {
                eat("%");
                obj = new BinOp('%', ((Expression) (obj)), parseFactor());
            }
        return ((Expression) (obj));
    }

    private Expression parseFactor()
    {
        if(token.equals("-"))
        {
            eat("-");
            return new BinOp('-', new IntExp(0), parseFactor());
        }
        if(token.equals("!"))
        {
            eat("!");
            return new Not(parseFactor());
        }
        if(token.equals("new"))
        {
            eat("new");
            String s = token;
            eat(s);
            return new Instantiation(s, parseArguments());
        } else
        {
            return parseAtom();
        }
    }

    private Expression parseAtom()
    {
        if(token.equals("null"))
        {
            eat("null");
            return new Null();
        }
        if(token.equals("true"))
        {
            eat("true");
            return new BoolExp(true);
        }
        if(token.equals("false"))
        {
            eat("false");
            return new BoolExp(false);
        }
        if(token.startsWith("\""))
        {
            String s = token;
            eat(s);
            return new StringExp(s.substring(1, s.length() - 1));
        }
        int i;
        i = Integer.parseInt(token);
        eat(token);
        return new IntExp(i);
        NumberFormatException numberformatexception;
        numberformatexception;
        return parseInvocation();
    }

    public Expression parseInvocation()
    {
        Object obj;
        String s;
        ArrayList arraylist;
        for(obj = parseVariable(); token.equals("."); obj = new Invocation(((Expression) (obj)), s, arraylist))
        {
            eat(".");
            s = token;
            eat(s);
            arraylist = parseArguments();
        }

        return ((Expression) (obj));
    }

    public Expression parseVariable()
    {
        if(token.equals("("))
        {
            eat("(");
            Expression expression = parseExpression();
            eat(")");
            return expression;
        }
        if(token.equals("this"))
        {
            eat("this");
            return new This();
        } else
        {
            String s = token;
            eat(s);
            return new Variable(s);
        }
    }

    public If parseIf()
    {
        eat("if");
        eat("(");
        Expression expression = parseExpression();
        eat(")");
        Statement statement = parseStatement();
        Object obj;
        if(token.equals("else"))
        {
            eat("else");
            obj = parseStatement();
        } else
        {
            obj = new Block(new ArrayList());
        }
        return new If(expression, statement, ((Statement) (obj)));
    }

    private Scanner scanner;
    private String token;
    private Stack stack;
}
