package ast;

import codegen.Emitter;
import parser.BoxedValue;

/**
 * Variable.java
 * @author Grant Yang
 * @version 2024.05.14
 *
 * Class to represent global and local variables in Pascal.
 */
public class Variable extends Expression
{
    private final String id;

    /**
     * Create a new variable with a certain name
     * @param id Name of the variable
     */
    public Variable(String id)
    {
        this.id = id;
    }

    @Override
    public BoxedValue eval(Environment env)
    {
        return env.getVariable(id);
    }

    /**
     * Compile the variable, both as an lvalue and rvalue.
     * The memory address of the variable is stored into $s0,
     * and depending on the type it is loaded into the correct register.
     * Doubles are loaded to $f0, integers to $v0, and strings to $a0
     * @precondition Variable has been defined by hintType(), and
     *               it is either a Double, String, or Int.
     * @throws RuntimeException if precondition is not met
     * @param emit Emitter object to use for codegen
     */
    @Override
    public void compile(Emitter emit)
    {
        System.out.println(emit.vars);
        System.out.println(emit.globalVars);
        if (!emit.vars.containsKey(id) && !emit.globalVars.containsKey(id))
            throw new RuntimeException("undefined variable " + id);
        compileLValue(emit);
        Type t = getType(emit);
        if (t.equals(Type.Double))
        {
            // no -4($s0) because its pre-handled in compileLValue
            emit.emit("ld $t6 ($s0)");
            emit.emit("mtc1.d $t6 $f0 # var load");
        }
        else if (t.equals(Type.String))
            emit.emit("lw $a0 ($s0) # var load");
        else if (t.equals(Type.Int))
            emit.emit("lw $v0 ($s0) # var load");
        else
            throw new RuntimeException("operator:= on type " + t + " not impl");
    }

    /**
     * Compile the variable of the l-value,
     * namely by storing its memory address into $s0
     * @precondition Variable has been defined by a call to hintType()
     * @throws RuntimeException if the precondition is not met
     * @param emit Emitter object to use for codegen
     */
    @Override
    public void compileLValue(Emitter emit)
    {
        if (emit.vars.containsKey(id))
        {
            int frameLoc = emit.vars.get(id).frameLoc();
            if (getType(emit).equals(Type.Double))
                frameLoc += 4; // !! Very important: stack grows from hi mem addr to lo.
            emit.emit("addi $s0 $fp -" + frameLoc + " # variable " + id);
            return;
        }
        else if (emit.globalVars.containsKey(id))
        {
            emit.emit("la $s0 _" + id + " # variable");
            return;
        }

        throw new RuntimeException("cannot compile lvalue for unknown var " + id);
    }

    /**
     * Get the type of the variable
     * @precondition The type of the variable has been defined by
     *               a call to hintType()
     * @throws RuntimeException if the precondition is not met
     * @param e Emitter to use for codegen.
     * @return The type of the variable
     */
    @Override
    public Type getType(Emitter e)
    {
        if (e.vars.containsKey(id))
            return e.vars.get(id).type();
        else if (e.globalVars.containsKey(id))
            return e.globalVars.get(id);

        System.out.println(e.vars);
        System.out.println(e.globalVars);
        throw new RuntimeException("uninitialized var type for " + id);
    }

    /**
     * Performs type checking and type inference for variables.
     * This function is called whenever a variable appears on the left-hand-side
     * of an operator:= expression (i.e. as an lvalue).
     * The first time this is called, the type of the variable is set
     * and allocation is handled. This function performs type-checking on
     * the subsequent times it is called.
     * @precondition Either the type of the variable is either undefined,
     *               or the type matches the previous call to hintType()
     * @throws RuntimeException if the precondition is not met
     * @postcondition If the variable type was undefined,
     *                the variable is allocated either as a local variable
     *                or a global one depending on the Emitter's settings.
     * @param t The type of the variable according to the rhs of :=
     * @param e The emitter object to use to emit code
     */
    @Override
    public void hintType(Type t, Emitter e)
    {
        if (e.vars.containsKey(id) && !e.vars.get(id).type().equals(t))
            throw new RuntimeException("hint type overwrote previous type of var "
                    + id + " from "
                    + e.vars.get(id).type() + " to " + t);

        if (!e.vars.containsKey(id))
        {
            System.out.println("register new variable " + id + " type " + t);

            if (e.storeVarsGlobal)
                e.allocGlobalVar(id, t);
            else
            {
                e.vars.put(id, new Emitter.VarInfo(t, e.frameSize));
                if (t.equals(Type.Double))
                    e.frameSize += 8;
                else
                    e.frameSize += 4;
            }
        }
        else
            e.vars.put(id, new Emitter.VarInfo(t, e.vars.get(id).frameLoc()));
    }

    /**
     * Print the name of the variable
     * @return A string of the form "$VARIABLE".
     */
    @Override
    public String toString()
    {
        return "$" + id;
    }
}
