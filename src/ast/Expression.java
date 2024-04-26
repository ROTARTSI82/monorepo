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

    public static final Expression UNIMPL = new Expression()
    {
        @Override
        public BoxedValue eval(Environment env)
        {
            throw new RuntimeException("unimplemented expr");
        }

        @Override
        public void compile(Emitter emit)
        {
            emit.emit("# ERR: unimpl expr");
        }
    };

    public enum Type
    {
        Null, Int, Double, String, Array
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

    public Type getType(Emitter e)
    {
        return Type.Null;
    }

    public abstract void compile(Emitter emit);

    // special functions for lvalues and variables
    public void compileLValue(Emitter emit)
    {
        emit.emit("# ERR cannot produce lvalue for this type of expr");
    }

    public void hintType(Type t, Emitter e)
    {
        e.emit("# hint type " + t);
    }
}
