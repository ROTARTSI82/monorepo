package ast;

import parser.BoxedValue;

import static ast.NamedExpression.namedOp;
import static parser.BoxedValue.box;

/**
 * Expression.java
 * @author Grant Yang
 * @version 2024.03.28
 * An expression which can be evaluated to a BoxedValue
 */
public interface Expression
{
    /**
     * Evaluates the expression to its value.
     * @param env The environment to evaluate in
     * @return The value of the expression
     * @postcondition The environment may be modified by the expression
     *                if the expression contained any := operators.
     */
    BoxedValue eval(Environment env);

    /**
     * The constant boolean literal for TRUE
     */
    Expression TRUE = namedOp((e) -> box(true), "true");

    /**
     * The constant boolean literal for FALSE
     */
    Expression FALSE = namedOp((e) -> box(false), "false");
}
