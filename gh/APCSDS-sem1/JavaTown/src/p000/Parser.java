package p000;

import java.util.ArrayList;
import java.util.Stack;

/* renamed from: Parser */
public class Parser {
    private Scanner scanner;
    private Stack<String> stack = new Stack<>();
    private String token;

    public Parser(Scanner scanner2) {
        this.scanner = scanner2;
        this.token = scanner2.next();
    }

    public ArrayList<Statement> parseStatements() {
        ArrayList<Statement> arrayList = new ArrayList<>();
        while (!this.token.equals(" ")) {
            arrayList.add(parseStatement());
        }
        return arrayList;
    }

    private void eat(String str) {
        if (!this.token.equals(str) && (!this.token.equals(" ") || !str.equals(";"))) {
            throw new JavaTownParseException("expected " + str + " but found " + this.token);
        } else if (this.stack.isEmpty()) {
            this.token = this.scanner.next();
        } else {
            this.token = this.stack.pop();
        }
    }

    private void barf(String str) {
        this.stack.push(this.token);
        this.token = str;
    }

    private Statement parseStatement() {
        System.out.println("parseStatement:  " + this.token);
        if (this.token.equals("public")) {
            eat("public");
            eat("class");
            String str = this.token;
            eat(str);
            eat("{");
            ArrayList<Declaration> parseDeclarations = parseDeclarations();
            eat("}");
            return new Definition(str, parseDeclarations);
        } else if (this.token.equals("return")) {
            eat("return");
            Expression parseExpression = parseExpression();
            eat(";");
            return new Return(parseExpression);
        } else if (this.token.equals("if")) {
            return parseIf();
        } else {
            if (this.token.equals("{")) {
                return parseBlock();
            }
            String str2 = this.token;
            eat(str2);
            if (this.token.equals("=")) {
                eat("=");
                Expression parseExpression2 = parseExpression();
                eat(";");
                return new Assignment(str2, parseExpression2);
            }
            String str3 = this.token;
            eat(str3);
            if (this.token.equals("=")) {
                eat("=");
                Expression parseExpression3 = parseExpression();
                eat(";");
                return new Assignment(str3, parseExpression3);
            }
            barf(str3);
            barf(str2);
            System.out.println(this.token + " " + this.stack);
            try {
                Invocation invocation = (Invocation) parseExpression();
                eat(";");
                return invocation;
            } catch (ClassCastException e) {
                e.printStackTrace();
                throw new JavaTownParseException("invalid syntax");
            }
        }
    }

    private ArrayList<Declaration> parseDeclarations() {
        ArrayList<Declaration> arrayList = new ArrayList<>();
        while (!this.token.equals("}")) {
            arrayList.add(parseDeclaration());
        }
        return arrayList;
    }

    private Declaration parseDeclaration() {
        String str = this.token;
        eat(str);
        String str2 = this.token;
        eat(str2);
        if (!this.token.equals(";") && !this.token.equals("(")) {
            str2 = this.token;
            eat(str2);
        }
        if (this.token.equals("(")) {
            if (str.equals("public") || str.equals("private")) {
                eat("(");
                ArrayList arrayList = new ArrayList();
                while (!this.token.equals(")")) {
                    String str3 = this.token;
                    eat(str3);
                    if (this.token.equals(",") || this.token.equals(")")) {
                        arrayList.add(str3);
                    } else {
                        arrayList.add(this.token);
                        eat(this.token);
                    }
                    if (this.token.equals(",")) {
                        eat(",");
                    } else if (!this.token.equals(")")) {
                        throw new JavaTownParseException("expected ) but found " + this.token);
                    }
                }
                eat(")");
                return new Method(str.equals("public") ? 0 : 1, str2, arrayList, parseBlock());
            }
            throw new JavaTownParseException("illegal start of declaration:  " + str);
        } else if (!str.equals("private")) {
            throw new JavaTownParseException("expected private but found " + this.token);
        } else {
            eat(";");
            return new Field(str2);
        }
    }

    private Block parseBlock() {
        eat("{");
        ArrayList arrayList = new ArrayList();
        while (!this.token.equals("}")) {
            arrayList.add(parseStatement());
        }
        eat("}");
        return new Block(arrayList);
    }

    private Expression parseExpression() {
        Expression parseAnded = parseAnded();
        while (this.token.equals("||")) {
            eat("||");
            parseAnded = new BinOp(BinOp.f6OR, parseAnded, parseAnded());
        }
        return parseAnded;
    }

    private Expression parseAnded() {
        Expression parsePossibleEquality = parsePossibleEquality();
        while (this.token.equals("&&")) {
            eat("&&");
            parsePossibleEquality = new BinOp(BinOp.AND, parsePossibleEquality, parsePossibleEquality());
        }
        return parsePossibleEquality;
    }

    private Expression parsePossibleEquality() {
        Expression parsePossibleComparison = parsePossibleComparison();
        if (this.token.equals("==")) {
            eat("==");
            return new BinOp(BinOp.f0EQ, parsePossibleComparison, parsePossibleComparison());
        } else if (!this.token.equals("!=")) {
            return parsePossibleComparison;
        } else {
            eat("!=");
            return new BinOp(BinOp.f5NE, parsePossibleComparison, parsePossibleComparison());
        }
    }

    private Expression parsePossibleComparison() {
        Expression parsePossibleSum = parsePossibleSum();
        if (this.token.equals("<")) {
            eat("<");
            return new BinOp(BinOp.f4LT, parsePossibleSum, parsePossibleSum());
        } else if (this.token.equals(">")) {
            eat(">");
            return new BinOp(BinOp.f2GT, parsePossibleSum, parsePossibleSum());
        } else if (this.token.equals("<=")) {
            eat("<=");
            return new BinOp(BinOp.f3LE, parsePossibleSum, parsePossibleSum());
        } else if (!this.token.equals(">=")) {
            return parsePossibleSum;
        } else {
            eat(">=");
            return new BinOp(BinOp.f1GE, parsePossibleSum, parsePossibleSum());
        }
    }

    private ArrayList<Expression> parseArguments() {
        eat("(");
        ArrayList<Expression> arrayList = new ArrayList<>();
        while (!this.token.equals(")")) {
            arrayList.add(parseExpression());
            if (this.token.equals(",")) {
                eat(",");
            } else if (!this.token.equals(")")) {
                throw new JavaTownParseException("expected ) but found " + this.token);
            }
        }
        eat(")");
        return arrayList;
    }

    private Expression parsePossibleSum() {
        Expression parseTerm = parseTerm();
        while (true) {
            if (!this.token.equals("+") && !this.token.equals("-")) {
                return parseTerm;
            }
            if (this.token.equals("+")) {
                eat("+");
                parseTerm = new BinOp(BinOp.PLUS, parseTerm, parseTerm());
            } else {
                eat("-");
                parseTerm = new BinOp(BinOp.MINUS, parseTerm, parseTerm());
            }
        }
    }

    private Expression parseTerm() {
        Expression parseFactor = parseFactor();
        while (true) {
            if (!this.token.equals("*") && !this.token.equals("/") && !this.token.equals("%")) {
                return parseFactor;
            }
            if (this.token.equals("*")) {
                eat("*");
                parseFactor = new BinOp(BinOp.TIMES, parseFactor, parseFactor());
            } else if (this.token.equals("/")) {
                eat("/");
                parseFactor = new BinOp(BinOp.DIVIDE, parseFactor, parseFactor());
            } else {
                eat("%");
                parseFactor = new BinOp(BinOp.MOD, parseFactor, parseFactor());
            }
        }
    }

    private Expression parseFactor() {
        if (this.token.equals("-")) {
            eat("-");
            return new BinOp(BinOp.MINUS, new IntExp(0), parseFactor());
        } else if (this.token.equals("!")) {
            eat("!");
            return new Not(parseFactor());
        } else if (!this.token.equals("new")) {
            return parseAtom();
        } else {
            eat("new");
            String str = this.token;
            eat(str);
            return new Instantiation(str, parseArguments());
        }
    }

    private Expression parseAtom() {
        if (this.token.equals("null")) {
            eat("null");
            return new Null();
        } else if (this.token.equals("true")) {
            eat("true");
            return new BoolExp(true);
        } else if (this.token.equals("false")) {
            eat("false");
            return new BoolExp(false);
        } else if (this.token.startsWith("\"")) {
            String str = this.token;
            eat(str);
            return new StringExp(str.substring(1, str.length() - 1));
        } else {
            try {
                int parseInt = Integer.parseInt(this.token);
                eat(this.token);
                return new IntExp(parseInt);
            } catch (NumberFormatException e) {
                return parseInvocation();
            }
        }
    }

    public Expression parseInvocation() {
        Expression parseVariable = parseVariable();
        while (this.token.equals(".")) {
            eat(".");
            String str = this.token;
            eat(str);
            parseVariable = new Invocation(parseVariable, str, parseArguments());
        }
        return parseVariable;
    }

    public Expression parseVariable() {
        if (this.token.equals("(")) {
            eat("(");
            Expression parseExpression = parseExpression();
            eat(")");
            return parseExpression;
        } else if (this.token.equals("this")) {
            eat("this");
            return new This();
        } else {
            String str = this.token;
            eat(str);
            return new Variable(str);
        }
    }

    public C0000If parseIf() {
        Statement block;
        eat("if");
        eat("(");
        Expression parseExpression = parseExpression();
        eat(")");
        Statement parseStatement = parseStatement();
        if (this.token.equals("else")) {
            eat("else");
            block = parseStatement();
        } else {
            block = new Block(new ArrayList());
        }
        return new C0000If(parseExpression, parseStatement, block);
    }
}
