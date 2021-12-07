import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

// 
// Decompiled by Procyon v0.5.36
// 

public class Parser
{
    private Scanner scanner;
    private String token;
    private Stack<String> stack;
    
    public Parser(final Scanner scanner) {
        this.scanner = scanner;
        this.stack = new Stack<String>();
        this.token = scanner.next();
    }
    
    public ArrayList<Statement> parseStatements() {
        final ArrayList<Statement> list = new ArrayList<Statement>();
        while (!this.token.equals(" ")) {
            list.add(this.parseStatement());
        }
        return list;
    }
    
    private void eat(final String s) {
        if (this.token.equals(s) || (this.token.equals(" ") && s.equals(";"))) {
            if (this.stack.isEmpty()) {
                this.token = this.scanner.next();
            }
            else {
                this.token = this.stack.pop();
            }
            return;
        }
        throw new JavaTownParseException("expected " + s + " but found " + this.token);
    }
    
    private void barf(final String token) {
        this.stack.push(this.token);
        this.token = token;
    }
    
    private Statement parseStatement() {
        System.out.println("parseStatement:  " + this.token);
        if (this.token.equals("public")) {
            this.eat("public");
            this.eat("class");
            final String token = this.token;
            this.eat(token);
            this.eat("{");
            final ArrayList<Declaration> declarations = this.parseDeclarations();
            this.eat("}");
            return new Definition(token, declarations);
        }
        if (this.token.equals("return")) {
            this.eat("return");
            final Expression expression = this.parseExpression();
            this.eat(";");
            return new Return(expression);
        }
        if (this.token.equals("if")) {
            return this.parseIf();
        }
        if (this.token.equals("{")) {
            return this.parseBlock();
        }
        final String token2 = this.token;
        this.eat(token2);
        if (this.token.equals("=")) {
            final String s = token2;
            this.eat("=");
            final Expression expression2 = this.parseExpression();
            this.eat(";");
            return new Assignment(s, expression2);
        }
        final String token3 = this.token;
        this.eat(token3);
        if (this.token.equals("=")) {
            final String s2 = token3;
            this.eat("=");
            final Expression expression3 = this.parseExpression();
            this.eat(";");
            return new Assignment(s2, expression3);
        }
        this.barf(token3);
        this.barf(token2);
        System.out.println(this.token + " " + this.stack);
        try {
            final Invocation invocation = (Invocation)this.parseExpression();
            this.eat(";");
            return invocation;
        }
        catch (ClassCastException ex) {
            ex.printStackTrace();
            throw new JavaTownParseException("invalid syntax");
        }
    }
    
    private ArrayList<Declaration> parseDeclarations() {
        final ArrayList<Declaration> list = new ArrayList<Declaration>();
        while (!this.token.equals("}")) {
            list.add(this.parseDeclaration());
        }
        return list;
    }
    
    private Declaration parseDeclaration() {
        final String token = this.token;
        this.eat(token);
        final String token2 = this.token;
        this.eat(token2);
        String token3;
        if (this.token.equals(";") || this.token.equals("(")) {
            token3 = token2;
        }
        else {
            token3 = this.token;
            this.eat(token3);
        }
        if (this.token.equals("(")) {
            if (!token.equals("public") && !token.equals("private")) {
                throw new JavaTownParseException("illegal start of declaration:  " + token);
            }
            this.eat("(");
            final ArrayList<String> list = new ArrayList<String>();
            while (!this.token.equals(")")) {
                final String token4 = this.token;
                this.eat(token4);
                if (this.token.equals(",") || this.token.equals(")")) {
                    list.add(token4);
                }
                else {
                    list.add(this.token);
                    this.eat(this.token);
                }
                if (this.token.equals(",")) {
                    this.eat(",");
                }
                else {
                    if (!this.token.equals(")")) {
                        throw new JavaTownParseException("expected ) but found " + this.token);
                    }
                    continue;
                }
            }
            this.eat(")");
            return new Method(token.equals("public") ? 0 : 1, token3, list, this.parseBlock());
        }
        else {
            if (!token.equals("private")) {
                throw new JavaTownParseException("expected private but found " + this.token);
            }
            this.eat(";");
            return new Field(token3);
        }
    }
    
    private Block parseBlock() {
        this.eat("{");
        final ArrayList<Statement> list = new ArrayList<Statement>();
        while (!this.token.equals("}")) {
            list.add(this.parseStatement());
        }
        this.eat("}");
        return new Block(list);
    }
    
    private Expression parseExpression() {
        Expression anded = this.parseAnded();
        while (this.token.equals("||")) {
            this.eat("||");
            anded = new BinOp('|', anded, this.parseAnded());
        }
        return anded;
    }
    
    private Expression parseAnded() {
        Expression possibleEquality = this.parsePossibleEquality();
        while (this.token.equals("&&")) {
            this.eat("&&");
            possibleEquality = new BinOp('&', possibleEquality, this.parsePossibleEquality());
        }
        return possibleEquality;
    }
    
    private Expression parsePossibleEquality() {
        final Expression possibleComparison = this.parsePossibleComparison();
        if (this.token.equals("==")) {
            this.eat("==");
            return new BinOp('=', possibleComparison, this.parsePossibleComparison());
        }
        if (this.token.equals("!=")) {
            this.eat("!=");
            return new BinOp('!', possibleComparison, this.parsePossibleComparison());
        }
        return possibleComparison;
    }
    
    private Expression parsePossibleComparison() {
        final Expression possibleSum = this.parsePossibleSum();
        if (this.token.equals("<")) {
            this.eat("<");
            return new BinOp('<', possibleSum, this.parsePossibleSum());
        }
        if (this.token.equals(">")) {
            this.eat(">");
            return new BinOp('>', possibleSum, this.parsePossibleSum());
        }
        if (this.token.equals("<=")) {
            this.eat("<=");
            return new BinOp('[', possibleSum, this.parsePossibleSum());
        }
        if (this.token.equals(">=")) {
            this.eat(">=");
            return new BinOp(']', possibleSum, this.parsePossibleSum());
        }
        return possibleSum;
    }
    
    private ArrayList<Expression> parseArguments() {
        this.eat("(");
        final ArrayList<Expression> list = new ArrayList<Expression>();
        while (!this.token.equals(")")) {
            list.add(this.parseExpression());
            if (this.token.equals(",")) {
                this.eat(",");
            }
            else {
                if (!this.token.equals(")")) {
                    throw new JavaTownParseException("expected ) but found " + this.token);
                }
                continue;
            }
        }
        this.eat(")");
        return list;
    }
    
    private Expression parsePossibleSum() {
        Expression term = this.parseTerm();
        while (this.token.equals("+") || this.token.equals("-")) {
            if (this.token.equals("+")) {
                this.eat("+");
                term = new BinOp('+', term, this.parseTerm());
            }
            else {
                this.eat("-");
                term = new BinOp('-', term, this.parseTerm());
            }
        }
        return term;
    }
    
    private Expression parseTerm() {
        Expression factor = this.parseFactor();
        while (this.token.equals("*") || this.token.equals("/") || this.token.equals("%")) {
            if (this.token.equals("*")) {
                this.eat("*");
                factor = new BinOp('*', factor, this.parseFactor());
            }
            else if (this.token.equals("/")) {
                this.eat("/");
                factor = new BinOp('/', factor, this.parseFactor());
            }
            else {
                this.eat("%");
                factor = new BinOp('%', factor, this.parseFactor());
            }
        }
        return factor;
    }
    
    private Expression parseFactor() {
        if (this.token.equals("-")) {
            this.eat("-");
            return new BinOp('-', new IntExp(0), this.parseFactor());
        }
        if (this.token.equals("!")) {
            this.eat("!");
            return new Not(this.parseFactor());
        }
        if (this.token.equals("new")) {
            this.eat("new");
            final String token = this.token;
            this.eat(token);
            return new Instantiation(token, this.parseArguments());
        }
        return this.parseAtom();
    }
    
    private Expression parseAtom() {
        if (this.token.equals("null")) {
            this.eat("null");
            return new Null();
        }
        if (this.token.equals("true")) {
            this.eat("true");
            return new BoolExp(true);
        }
        if (this.token.equals("false")) {
            this.eat("false");
            return new BoolExp(false);
        }
        if (this.token.startsWith("\"")) {
            final String token = this.token;
            this.eat(token);
            return new StringExp(token.substring(1, token.length() - 1));
        }
        try {
            final int int1 = Integer.parseInt(this.token);
            this.eat(this.token);
            return new IntExp(int1);
        }
        catch (NumberFormatException ex) {
            return this.parseInvocation();
        }
    }
    
    public Expression parseInvocation() {
        Expression variable = this.parseVariable();
        while (this.token.equals(".")) {
            this.eat(".");
            final String token = this.token;
            this.eat(token);
            variable = new Invocation(variable, token, this.parseArguments());
        }
        return variable;
    }
    
    public Expression parseVariable() {
        if (this.token.equals("(")) {
            this.eat("(");
            final Expression expression = this.parseExpression();
            this.eat(")");
            return expression;
        }
        if (this.token.equals("this")) {
            this.eat("this");
            return new This();
        }
        final String token = this.token;
        this.eat(token);
        return new Variable(token);
    }
    
    public If parseIf() {
        this.eat("if");
        this.eat("(");
        final Expression expression = this.parseExpression();
        this.eat(")");
        final Statement statement = this.parseStatement();
        Statement statement2;
        if (this.token.equals("else")) {
            this.eat("else");
            statement2 = this.parseStatement();
        }
        else {
            statement2 = new Block(new ArrayList<Statement>());
        }
        return new If(expression, statement, statement2);
    }
}
