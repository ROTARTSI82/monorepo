/*
 * Decompiled with CFR 0.150.
 */
public class BinOp
implements Expression {
    public static final char PLUS = '+';
    public static final char MINUS = '-';
    public static final char TIMES = '*';
    public static final char DIVIDE = '/';
    public static final char MOD = '%';
    public static final char EQ = '=';
    public static final char NE = '!';
    public static final char AND = '&';
    public static final char OR = '|';
    public static final char LT = '<';
    public static final char LE = '[';
    public static final char GT = '>';
    public static final char GE = ']';
    private char op;
    private Expression exp1;
    private Expression exp2;

    public BinOp(char c, Expression expression, Expression expression2) {
        this.op = c;
        this.exp1 = expression;
        this.exp2 = expression2;
    }

    public Object eval(Environment environment) {
        Object object = this.exp1.eval(environment);
        if (object instanceof Boolean && this.op == '&') {
            if (object.equals(Boolean.FALSE)) {
                return Boolean.FALSE;
            }
            return this.exp2.eval(environment);
        }
        if (object instanceof Boolean && this.op == '|') {
            if (object.equals(Boolean.TRUE)) {
                return Boolean.TRUE;
            }
            return this.exp2.eval(environment);
        }
        Object object2 = this.exp2.eval(environment);
        try {
            if (this.op == '+') {
                if (object instanceof String || object2 instanceof String) {
                    return "" + object + object2;
                }
                if (object instanceof Integer && object2 instanceof Integer) {
                    return new Integer((Integer)object + (Integer)object2);
                }
                throw new JavaTownException("Cannot add " + object + " and " + object2);
            }
            if (this.op == '-') {
                return new Integer((Integer)object - (Integer)object2);
            }
            if (this.op == '*') {
                return new Integer((Integer)object * (Integer)object2);
            }
            if (this.op == '/') {
                return new Integer((Integer)object / (Integer)object2);
            }
            if (this.op == '%') {
                return new Integer((Integer)object % (Integer)object2);
            }
            if (this.op == '=') {
                if (object instanceof Integer || object instanceof Boolean) {
                    return object.equals(object2) ? Boolean.TRUE : Boolean.FALSE;
                }
                return object == object2 ? Boolean.TRUE : Boolean.FALSE;
            }
            if (this.op == '!') {
                if (object instanceof Integer || object instanceof Boolean) {
                    return object.equals(object2) ? Boolean.FALSE : Boolean.TRUE;
                }
                return object == object2 ? Boolean.FALSE : Boolean.TRUE;
            }
            if (this.op == '>') {
                return (Integer)object > (Integer)object2 ? Boolean.TRUE : Boolean.FALSE;
            }
            if (this.op == ']') {
                return (Integer)object >= (Integer)object2 ? Boolean.TRUE : Boolean.FALSE;
            }
            if (this.op == '<') {
                return (Integer)object < (Integer)object2 ? Boolean.TRUE : Boolean.FALSE;
            }
            if (this.op == '[') {
                return (Integer)object <= (Integer)object2 ? Boolean.TRUE : Boolean.FALSE;
            }
            throw new JavaTownException("Invalid operation:  " + this.op);
        }
        catch (ArithmeticException arithmeticException) {
            throw new JavaTownException(arithmeticException.getMessage());
        }
    }
}

