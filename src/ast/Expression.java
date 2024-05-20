package ast;

import codegen.Emitter;
import parser.BoxedValue;

import static parser.BoxedValue.box;

/**
 * Expression.java
 *
 * @author Grant Yang
 * @version 2024.03.28
 * An expression which can be evaluated to a BoxedValue
 */
public abstract class Expression
{
    /**
     * The constant boolean literal for TRUE
     */
    public static final Expression TRUE = new Expression()
    {
        @Override
        public Type getType(Emitter e)
        {
            return Type.Int;
        }

        @Override
        public BoxedValue eval(Environment env)
        {
            return box(true);
        }

        @Override
        public void compile(Emitter emit)
        {
            emit.emit("addi $v0 $0 1");
        }

        @Override
        public String toString()
        {
            return "true";
        }
    };

    /**
     * The constant boolean literal for FALSE
     */
    public static final Expression FALSE = new Expression()
    {
        @Override
        public Type getType(Emitter e)
        {
            return Type.Int;
        }

        @Override
        public BoxedValue eval(Environment env)
        {
            return box(false);
        }

        @Override
        public void compile(Emitter emit)
        {
            emit.emit("move $v0 $0");
        }

        @Override
        public String toString()
        {
            return "false";
        }
    };

    /**
     * A simple Expression that does nothing when executed.
     */
    public static final Expression NO_OP = new Expression()
    {
        @Override
        public BoxedValue eval(Environment env)
        {
            return BoxedValue.NULL;
        }

        @Override
        public void compile(Emitter emit)
        {
            emit.emit("# no op");
        }
    };

    /**
     * Possible types for an expression
     */
    public enum Type
    {
        Null, Int, Double, String, Array
    }

    /**
     * Convert from a string name to the type object
     * @precondition The string is a recognized type name
     * @param s String name of the type
     * @return Type object corresponding to the string
     */
    public static Type typeFromString(String s)
    {
        return switch (s.toLowerCase())
        {
            case "integer" -> Expression.Type.Int;
            case "real" -> Expression.Type.Double;
            case "string" -> Expression.Type.String;
            default -> throw new RuntimeException("unrecognized type annotation "
                    + s);
        };
    }

    /**
     * Evaluates the expression to its value.
     *
     * @param env The environment to evaluate in
     * @return The value of the expression
     * @postcondition The environment may be modified by the expression
     * if the expression contained any := operators.
     */
    public abstract BoxedValue eval(Environment env);

    /**
     * Calculate the type of this expression.
     * For variables, hintType() should be called before getType().
     * @param e Emitter object containing the context for this expression
     * @return The type of the variable, defaulting to Type.Null
     */
    public Type getType(Emitter e)
    {
        return Type.Null;
    }

    /**
     * Compile this expression, storing the results into
     * $v0, $a0, etc. depending on the type of the expression.
     * Registers are not guaranteed to be preserved, so the stack
     * is used extensively.
     * @param emit Object into which to emit the code
     */
    public abstract void compile(Emitter emit);

    /**
     * Compile L-value, putting the address of the expression into $s0
     * This is used for variables and anything that may appear on the left
     * hand side of an assignment operator :=.
     * @param emit Object into which to emit the code
     */
    public void compileLValue(Emitter emit)
    {
        emit.emit("# ERR cannot produce lvalue for this type of expr");
    }

    /**
     * Emit a hint to the emitter about the type of this expression.
     * This is used to set the type of variables on the first assignment.
     * @param t Type to hint
     * @param e Emitter to hint to
     * @precondition Any previous calls to hintType() should agree with this call,
     *               i.e. all previous calls had the same type t.
     */
    public void hintType(Type t, Emitter e)
    {
        e.emit("# hint type " + t);
    }
}
