package ast;

import codegen.Emitter;
import parser.BoxedValue;

/**
 * BinOp.java
 *
 * @author Grant Yang
 * @version 2024.03.25
 * A binary operator, representing a node in an abstract
 * syntax tree with left and right children. The operator
 * is stored as its string name, which is then looked up
 * in the table from OperatorSAM at evaluation time.
 */
public class BinOp extends Expression
{
    private final Expression lhs, rhs;
    private final String name;
    private final int id;
    private static int COUNT = 0;


    @Override
    public Type getType(Emitter e)
    {
        System.out.println(this);
        if (OperatorCodegen.DOUBLE_COMPARES.containsKey(name))
            return Type.Int;

        Type lht = lhs.getType(e);
        Type rht = rhs.getType(e);
        if (lht == null) System.out.println("null type on " + lhs);
        if (rht == null) System.out.println("null type on " + rhs);
        if (lht.equals(Type.Double) || rht.equals(Type.Double))
            return Type.Double;
        return rhs.getType(e);
    }

    /**
     * Construct a new binary operator
     *
     * @param type The operator type. See OperatorSAM::NAME_MAP for a list of operator types
     * @param a    The left-hand side child of this operator
     * @param b    The right-hand side child of this operator
     */
    public BinOp(String type, Expression a, Expression b)
    {
        this.name = type;
        this.id = (name.equalsIgnoreCase("AND") || name.equalsIgnoreCase("OR"))
                ? COUNT++ : 0;
        lhs = a;
        rhs = b;
    }

    /**
     * Evaluates the operator according to the rules specified by OperatorSAM.
     * The resulting behavior may either be left-associative or right-associative
     * depending on the code in the exact OperatorSAM.
     *
     * @param env The environment to evaluate in
     * @return The value that the operator evaluated to
     */
    @Override
    public BoxedValue eval(Environment env)
    {
        BoxedValue ret = OperatorSAM.NAME_MAP.get(name).apply(env, lhs, rhs);
        if (env.isDebug())
            System.out.println(this + " = " + ret);
        return ret;
    }

    @Override
    public void compile(Emitter emit)
    {
        emit.emit("# " + this);
        Type rht = rhs.getType(emit);
        if (name.equals(":=") && !rht.equals(Type.Double))
        {
            rhs.compile(emit);
            if (!OperatorCodegen.REG_SRC.containsKey(rht))
                throw new RuntimeException("unknown rule for := on type " + rht + ": " + rhs);
            String reg = OperatorCodegen.REG_SRC.get(rht);
            emit.emitPush32(reg);
            lhs.hintType(rht, emit);
            lhs.compileLValue(emit);
            emit.emitPop32(reg);
            emit.emit("sw " + reg + " ($s0)");
            return;
        }

        if (rht.equals(Type.Double) || lhs.getType(emit).equals(Type.Double))
        {
            if (name.equals(":="))
            {
                // rht MUST be Type.Double at this point
                rhs.compile(emit);
                emit.emitPushF64("$f0");
                lhs.hintType(Type.Double, emit);
                lhs.compileLValue(emit); // this *might* modify $f0??
                emit.emitPopF64("$f0");
                // no -4($s0) needed here because it's handled at frameLoc variable LValue time
                emit.emit("mfc1 $t6 $f0");
                emit.emit("sd $t6 ($s0)");
                return;
            }

            final String cvt = "mtc1 $v0 $f0\n\tcvt.d.w $f0 $f0";
            lhs.compile(emit);
            if (lhs.getType(emit).equals(Type.Int))
                emit.emit(cvt);
            emit.emitPushF64("$f0");

            rhs.compile(emit);
            if (rht.equals(Type.Int))
                emit.emit(cvt);
//            emit.emit("mov.d $f4 $f0");
            emit.emitPopF64("$f2");

            if (OperatorCodegen.DOUBLE_COMPARES.containsKey(name))
            {
                emit.emit("move $v0 $0");
                emit.emit("li $t0 1");
                emit.emit(OperatorCodegen.DOUBLE_COMPARES.get(name) + " $v0 $t0");
            }
            else
            {
                if (!OperatorCodegen.INT_BORING_CODEGEN.containsKey(name))
                    throw new RuntimeException(
                            "unknown boring int operator to be used as double: " + this);
                emit.emit(OperatorCodegen.INT_BORING_CODEGEN.get(name) + ".d $f0 $f2 $f0");
            }
        }
        else if (lhs.getType(emit).equals(Type.Int) && rht.equals(Type.Int))
        {
            if (OperatorCodegen.INT_BORING_CODEGEN.containsKey(name))
            {
                String ins = OperatorCodegen.INT_BORING_CODEGEN.get(name);
                lhs.compile(emit);
                emit.emitPush32("$v0");
                rhs.compile(emit);
//                emit.emit("move $t1 $v0");
                emit.emitPop32("$t0");
                emit.emit(ins + " $v0 $t0 $v0");
            }
            else
            {
                OperatorCodegen.INT_TRICKY_CODEGEN.get(name).apply(emit, lhs, rhs, id);
            }
        }
        else
        {
            emit.emit("# ERR: codegen failed for operator " + this);
        }
    }

    @Override
    public void compileLValue(Emitter emit)
    {
        emit.emit("# operator compile lvalue");
        if (!name.equals(":="))
            compile(emit);
        else
            throw new RuntimeException("operator compile lvalue not impl for " + this);
    }

    /**
     * Convert to string, representing it as "({lhs} {op} {rhs})"
     *
     * @return A string representing the abstract syntax tree from this point.
     */
    @Override
    public String toString()
    {
        return "(" + lhs + " " + name + " " + rhs + ")";
    }
}
