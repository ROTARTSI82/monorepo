// 
// Decompiled by Procyon v0.5.36
// 

public class BinOp implements Expression
{
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
    
    public BinOp(final char op, final Expression exp1, final Expression exp2) {
        this.op = op;
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    public Object eval(final Environment environment) {
        final Object eval = this.exp1.eval(environment);
        if (eval instanceof Boolean && this.op == '&') {
            if (eval.equals(Boolean.FALSE)) {
                return Boolean.FALSE;
            }
            return this.exp2.eval(environment);
        }
        else if (eval instanceof Boolean && this.op == '|') {
            if (eval.equals(Boolean.TRUE)) {
                return Boolean.TRUE;
            }
            return this.exp2.eval(environment);
        }
        else {
            final Object eval2 = this.exp2.eval(environment);
            try {
                if (this.op == '+') {
                    if (eval instanceof String || eval2 instanceof String) {
                        return "" + eval + eval2;
                    }
                    if (eval instanceof Integer && eval2 instanceof Integer) {
                        return new Integer((int)eval + (int)eval2);
                    }
                    throw new JavaTownException("Cannot add " + eval + " and " + eval2);
                }
                else {
                    if (this.op == '-') {
                        return new Integer((int)eval - (int)eval2);
                    }
                    if (this.op == '*') {
                        return new Integer((int)eval * (int)eval2);
                    }
                    if (this.op == '/') {
                        return new Integer((int)eval / (int)eval2);
                    }
                    if (this.op == '%') {
                        return new Integer((int)eval % (int)eval2);
                    }
                    if (this.op == '=') {
                        if (eval instanceof Integer || eval instanceof Boolean) {
                            return eval.equals(eval2) ? Boolean.TRUE : Boolean.FALSE;
                        }
                        return (eval == eval2) ? Boolean.TRUE : Boolean.FALSE;
                    }
                    else if (this.op == '!') {
                        if (eval instanceof Integer || eval instanceof Boolean) {
                            return eval.equals(eval2) ? Boolean.FALSE : Boolean.TRUE;
                        }
                        return (eval == eval2) ? Boolean.FALSE : Boolean.TRUE;
                    }
                    else {
                        if (this.op == '>') {
                            return ((int)eval > (int)eval2) ? Boolean.TRUE : Boolean.FALSE;
                        }
                        if (this.op == ']') {
                            return ((int)eval >= (int)eval2) ? Boolean.TRUE : Boolean.FALSE;
                        }
                        if (this.op == '<') {
                            return ((int)eval < (int)eval2) ? Boolean.TRUE : Boolean.FALSE;
                        }
                        if (this.op == '[') {
                            return ((int)eval <= (int)eval2) ? Boolean.TRUE : Boolean.FALSE;
                        }
                        throw new JavaTownException("Invalid operation:  " + this.op);
                    }
                }
            }
            catch (ArithmeticException ex) {
                throw new JavaTownException(ex.getMessage());
            }
        }
    }
}
