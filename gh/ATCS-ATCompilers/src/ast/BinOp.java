package ast;

import parser.BoxedValue;

/**
 * BinOp.java
 * @author Grant Yang
 * @version 2024.03.25
 * A binary operator, representing a node in an abstract
 * syntax tree with left and right children. The operator
 * is stored as its string name, which is then looked up
 * in the table from OperatorSAM at evaluation time.
 */
public class BinOp implements Expression
{
    private final Expression lhs, rhs;
    private final String name;

    /**
     * Construct a new binary operator
     * @param type The operator type. See OperatorSAM::NAME_MAP for a list of operator types
     * @param a The left-hand side child of this operator
     * @param b The right-hand side child of this operator
     */
    public BinOp(String type, Expression a, Expression b)
    {
        this.name = type;
        lhs = a;
        rhs = b;
    }

    /**
     * Evaluates the operator according to the rules specified by OperatorSAM.
     * The resulting behavior may either be left-associative or right-associative
     * depending on the code in the exact OperatorSAM.
     * @param env The environment to evaluate in
     * @return The value that the operator evaluated to
     */
    @Override
    public BoxedValue eval(Environment env)
    {
        BoxedValue ret = OperatorSAM.NAME_MAP.get(name).apply(env, lhs, rhs);
//        System.out.println(this + " = " + ret);
        return ret;
    }

    /**
     * Convert to string, representing it as "({lhs} {op} {rhs})"
     * @return A string representing the abstract syntax tree from this point.
     */
    @Override
    public String toString()
    {
        return "(" + lhs + " " + name + " " + rhs + ")";
    }
}
