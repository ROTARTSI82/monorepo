/*
 * Decompiled with CFR 0.150.
 */
import java.util.ArrayList;
import java.util.Stack;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Parser {
    private Scanner scanner;
    private String token;
    private Stack<String> stack;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.stack = new Stack();
        this.token = scanner.next();
    }

    public ArrayList<Statement> parseStatements() {
        ArrayList<Statement> arrayList = new ArrayList<Statement>();
        while (!this.token.equals(" ")) {
            arrayList.add(this.parseStatement());
        }
        return arrayList;
    }

    private void eat(String string) {
        if (this.token.equals(string) || this.token.equals(" ") && string.equals(";")) {
            this.token = this.stack.isEmpty() ? this.scanner.next() : this.stack.pop();
        } else {
            throw new JavaTownParseException("expected " + string + " but found " + this.token);
        }
    }

    private void barf(String string) {
        this.stack.push(this.token);
        this.token = string;
    }

    private Statement parseStatement() {
        System.out.println("parseStatement:  " + this.token);
        if (this.token.equals("public")) {
            this.eat("public");
            this.eat("class");
            String string = this.token;
            this.eat(string);
            this.eat("{");
            ArrayList<Declaration> arrayList = this.parseDeclarations();
            this.eat("}");
            return new Definition(string, arrayList);
        }
        if (this.token.equals("return")) {
            this.eat("return");
            Expression expression = this.parseExpression();
            this.eat(";");
            return new Return(expression);
        }
        if (this.token.equals("if")) {
            return this.parseIf();
        }
        if (this.token.equals("{")) {
            return this.parseBlock();
        }
        String string = this.token;
        this.eat(string);
        if (this.token.equals("=")) {
            String string2 = string;
            this.eat("=");
            Expression expression = this.parseExpression();
            this.eat(";");
            return new Assignment(string2, expression);
        }
        String string3 = this.token;
        this.eat(string3);
        if (this.token.equals("=")) {
            String string4 = string3;
            this.eat("=");
            Expression expression = this.parseExpression();
            this.eat(";");
            return new Assignment(string4, expression);
        }
        this.barf(string3);
        this.barf(string);
        System.out.println(this.token + " " + this.stack);
        try {
            Invocation invocation = (Invocation)this.parseExpression();
            this.eat(";");
            return invocation;
        }
        catch (ClassCastException classCastException) {
            classCastException.printStackTrace();
            throw new JavaTownParseException("invalid syntax");
        }
    }

    private ArrayList<Declaration> parseDeclarations() {
        ArrayList<Declaration> arrayList = new ArrayList<Declaration>();
        while (!this.token.equals("}")) {
            arrayList.add(this.parseDeclaration());
        }
        return arrayList;
    }

    private Declaration parseDeclaration() {
        String string;
        String string2 = this.token;
        this.eat(string2);
        String string3 = this.token;
        this.eat(string3);
        if (this.token.equals(";") || this.token.equals("(")) {
            string = string3;
        } else {
            string = this.token;
            this.eat(string);
        }
        if (this.token.equals("(")) {
            if (!string2.equals("public") && !string2.equals("private")) {
                throw new JavaTownParseException("illegal start of declaration:  " + string2);
            }
            this.eat("(");
            ArrayList<String> arrayList = new ArrayList<String>();
            while (!this.token.equals(")")) {
                string3 = this.token;
                this.eat(string3);
                if (this.token.equals(",") || this.token.equals(")")) {
                    arrayList.add(string3);
                } else {
                    arrayList.add(this.token);
                    this.eat(this.token);
                }
                if (this.token.equals(",")) {
                    this.eat(",");
                    continue;
                }
                if (this.token.equals(")")) continue;
                throw new JavaTownParseException("expected ) but found " + this.token);
            }
            this.eat(")");
            return new Method(string2.equals("public") ? 0 : 1, string, arrayList, this.parseBlock());
        }
        if (!string2.equals("private")) {
            throw new JavaTownParseException("expected private but found " + this.token);
        }
        this.eat(";");
        return new Field(string);
    }

    private Block parseBlock() {
        this.eat("{");
        ArrayList<Statement> arrayList = new ArrayList<Statement>();
        while (!this.token.equals("}")) {
            arrayList.add(this.parseStatement());
        }
        this.eat("}");
        return new Block(arrayList);
    }

    private Expression parseExpression() {
        Expression expression = this.parseAnded();
        while (this.token.equals("||")) {
            this.eat("||");
            expression = new BinOp('|', expression, this.parseAnded());
        }
        return expression;
    }

    private Expression parseAnded() {
        Expression expression = this.parsePossibleEquality();
        while (this.token.equals("&&")) {
            this.eat("&&");
            expression = new BinOp('&', expression, this.parsePossibleEquality());
        }
        return expression;
    }

    private Expression parsePossibleEquality() {
        Expression expression = this.parsePossibleComparison();
        if (this.token.equals("==")) {
            this.eat("==");
            return new BinOp('=', expression, this.parsePossibleComparison());
        }
        if (this.token.equals("!=")) {
            this.eat("!=");
            return new BinOp('!', expression, this.parsePossibleComparison());
        }
        return expression;
    }

    private Expression parsePossibleComparison() {
        Expression expression = this.parsePossibleSum();
        if (this.token.equals("<")) {
            this.eat("<");
            return new BinOp('<', expression, this.parsePossibleSum());
        }
        if (this.token.equals(">")) {
            this.eat(">");
            return new BinOp('>', expression, this.parsePossibleSum());
        }
        if (this.token.equals("<=")) {
            this.eat("<=");
            return new BinOp('[', expression, this.parsePossibleSum());
        }
        if (this.token.equals(">=")) {
            this.eat(">=");
            return new BinOp(']', expression, this.parsePossibleSum());
        }
        return expression;
    }

    private ArrayList<Expression> parseArguments() {
        this.eat("(");
        ArrayList<Expression> arrayList = new ArrayList<Expression>();
        while (!this.token.equals(")")) {
            arrayList.add(this.parseExpression());
            if (this.token.equals(",")) {
                this.eat(",");
                continue;
            }
            if (this.token.equals(")")) continue;
            throw new JavaTownParseException("expected ) but found " + this.token);
        }
        this.eat(")");
        return arrayList;
    }

    private Expression parsePossibleSum() {
        Expression expression = this.parseTerm();
        while (this.token.equals("+") || this.token.equals("-")) {
            if (this.token.equals("+")) {
                this.eat("+");
                expression = new BinOp('+', expression, this.parseTerm());
                continue;
            }
            this.eat("-");
            expression = new BinOp('-', expression, this.parseTerm());
        }
        return expression;
    }

    private Expression parseTerm() {
        Expression expression = this.parseFactor();
        while (this.token.equals("*") || this.token.equals("/") || this.token.equals("%")) {
            if (this.token.equals("*")) {
                this.eat("*");
                expression = new BinOp('*', expression, this.parseFactor());
                continue;
            }
            if (this.token.equals("/")) {
                this.eat("/");
                expression = new BinOp('/', expression, this.parseFactor());
                continue;
            }
            this.eat("%");
            expression = new BinOp('%', expression, this.parseFactor());
        }
        return expression;
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
            String string = this.token;
            this.eat(string);
            return new Instantiation(string, this.parseArguments());
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
            String string = this.token;
            this.eat(string);
            return new StringExp(string.substring(1, string.length() - 1));
        }
        try {
            int n = Integer.parseInt(this.token);
            this.eat(this.token);
            return new IntExp(n);
        }
        catch (NumberFormatException numberFormatException) {
            return this.parseInvocation();
        }
    }

    public Expression parseInvocation() {
        Expression expression = this.parseVariable();
        while (this.token.equals(".")) {
            this.eat(".");
            String string = this.token;
            this.eat(string);
            ArrayList<Expression> arrayList = this.parseArguments();
            expression = new Invocation(expression, string, arrayList);
        }
        return expression;
    }

    public Expression parseVariable() {
        if (this.token.equals("(")) {
            this.eat("(");
            Expression expression = this.parseExpression();
            this.eat(")");
            return expression;
        }
        if (this.token.equals("this")) {
            this.eat("this");
            return new This();
        }
        String string = this.token;
        this.eat(string);
        return new Variable(string);
    }

    public If parseIf() {
        Statement statement;
        this.eat("if");
        this.eat("(");
        Expression expression = this.parseExpression();
        this.eat(")");
        Statement statement2 = this.parseStatement();
        if (this.token.equals("else")) {
            this.eat("else");
            statement = this.parseStatement();
        } else {
            statement = new Block(new ArrayList<Statement>());
        }
        return new If(expression, statement2, statement);
    }
}

