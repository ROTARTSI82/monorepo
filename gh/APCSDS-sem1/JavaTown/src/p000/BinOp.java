package p000;

/* renamed from: BinOp */
public class BinOp implements Expression {
    public static final char AND = '&';
    public static final char DIVIDE = '/';

    /* renamed from: EQ */
    public static final char f0EQ = '=';

    /* renamed from: GE */
    public static final char f1GE = ']';

    /* renamed from: GT */
    public static final char f2GT = '>';

    /* renamed from: LE */
    public static final char f3LE = '[';

    /* renamed from: LT */
    public static final char f4LT = '<';
    public static final char MINUS = '-';
    public static final char MOD = '%';

    /* renamed from: NE */
    public static final char f5NE = '!';

    /* renamed from: OR */
    public static final char f6OR = '|';
    public static final char PLUS = '+';
    public static final char TIMES = '*';
    private Expression exp1;
    private Expression exp2;

    /* renamed from: op */
    private char f7op;

    public BinOp(char c, Expression expression, Expression expression2) {
        this.f7op = c;
        this.exp1 = expression;
        this.exp2 = expression2;
    }

    public Object eval(Environment environment) {
        Object eval = this.exp1.eval(environment);
        if (!(eval instanceof Boolean) || this.f7op != '&') {
            if (!(eval instanceof Boolean) || this.f7op != '|') {
                Object eval2 = this.exp2.eval(environment);
                try {
                    if (this.f7op == '+') {
                        if ((eval instanceof String) || (eval2 instanceof String)) {
                            return "" + eval + eval2;
                        }
                        if (!(eval instanceof Integer) || !(eval2 instanceof Integer)) {
                            throw new JavaTownException("Cannot add " + eval + " and " + eval2);
                        }
                        return new Integer(((Integer) eval2).intValue() + ((Integer) eval).intValue());
                    } else if (this.f7op == '-') {
                        return new Integer(((Integer) eval).intValue() - ((Integer) eval2).intValue());
                    } else {
                        if (this.f7op == '*') {
                            return new Integer(((Integer) eval).intValue() * ((Integer) eval2).intValue());
                        }
                        if (this.f7op == '/') {
                            return new Integer(((Integer) eval).intValue() / ((Integer) eval2).intValue());
                        }
                        if (this.f7op == '%') {
                            return new Integer(((Integer) eval).intValue() % ((Integer) eval2).intValue());
                        }
                        if (this.f7op == '=') {
                            return ((eval instanceof Integer) || (eval instanceof Boolean)) ? eval.equals(eval2) ? Boolean.TRUE : Boolean.FALSE : eval == eval2 ? Boolean.TRUE : Boolean.FALSE;
                        }
                        if (this.f7op == '!') {
                            return ((eval instanceof Integer) || (eval instanceof Boolean)) ? eval.equals(eval2) ? Boolean.FALSE : Boolean.TRUE : eval == eval2 ? Boolean.FALSE : Boolean.TRUE;
                        }
                        if (this.f7op == '>') {
                            return ((Integer) eval).intValue() > ((Integer) eval2).intValue() ? Boolean.TRUE : Boolean.FALSE;
                        }
                        if (this.f7op == ']') {
                            return ((Integer) eval).intValue() >= ((Integer) eval2).intValue() ? Boolean.TRUE : Boolean.FALSE;
                        }
                        if (this.f7op == '<') {
                            return ((Integer) eval).intValue() < ((Integer) eval2).intValue() ? Boolean.TRUE : Boolean.FALSE;
                        }
                        if (this.f7op == '[') {
                            return ((Integer) eval).intValue() <= ((Integer) eval2).intValue() ? Boolean.TRUE : Boolean.FALSE;
                        }
                        throw new JavaTownException("Invalid operation:  " + this.f7op);
                    }
                } catch (ArithmeticException e) {
                    throw new JavaTownException(e.getMessage());
                }
            } else if (eval.equals(Boolean.TRUE)) {
                return Boolean.TRUE;
            } else {
                return this.exp2.eval(environment);
            }
        } else if (eval.equals(Boolean.FALSE)) {
            return Boolean.FALSE;
        } else {
            return this.exp2.eval(environment);
        }
    }
}
