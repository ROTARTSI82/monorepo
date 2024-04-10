package ast;

import parser.BoxedValue;

/**
 * NamedExpression.java
 *
 * @author Grant Yang
 * @version 2024.03.28
 * Thin wrapper around Expressions that give names to them
 * so that printing them is more informative.
 */
public class NamedExpression implements Expression
{
    private Expression op;
    private String name;

    /**
     * Construct a NamedExpression from an Expression and a name
     *
     * @param sm   The underlying expression
     * @param name The name of the expression to print
     * @return A new NamedExpression
     */
    public static NamedExpression namedOp(Expression sm, String name)
    {
        NamedExpression ret = new NamedExpression();
        ret.op = sm;
        ret.name = name;
        return ret;
    }

    /**
     * Convert to string, representing it as the name of the expression
     *
     * @return A string representing the abstract syntax tree from this point.
     */
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Evaluates the underlying expression
     *
     * @param env The environment to evaluate in
     * @return The value that the expression evaluated to
     */
    @Override
    public BoxedValue eval(Environment env)
    {
        return op.eval(env);
    }
}
