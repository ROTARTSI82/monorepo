package ast;

import codegen.Emitter;
import parser.BoxedValue;

public class Variable extends Expression
{
    private final String id;

    public Variable(String id)
    {
        this.id = id;
    }

    @Override
    public BoxedValue eval(Environment env)
    {
        return env.getVariable(id);
    }

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
            emit.emit("mtc1.d $t6 $f0");
        }
        else if (t.equals(Type.String))
            emit.emit("lw $a0 ($s0)");
        else if (t.equals(Type.Int))
            emit.emit("lw $v0 ($s0)");
        else
            throw new RuntimeException("operator:= on type " + t + " not impl");
    }

    @Override
    public void compileLValue(Emitter emit)
    {
        if (emit.vars.containsKey(id))
        {
            int frameLoc = emit.vars.get(id).frameLoc();
            if (getType(emit).equals(Type.Double))
                frameLoc += 4; // !! Very important: stack grows from hi mem addr to lo.
            emit.emit("subi $s0 $fp " + frameLoc + " # " + id);
            return;
        }
        else if (emit.globalVars.containsKey(id))
        {
            emit.emit("la $s0 _" + id);
            return;
        }

        emit.emit("# ERR: compile Lvalue failed for unknown var " + id);
    }

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

    @Override
    public void hintType(Type t, Emitter e)
    {
        if (e.vars.containsKey(id) && !e.vars.get(id).type().equals(t))
            throw new RuntimeException("hint type overwrote previous type from "
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

    @Override
    public String toString()
    {
        return "VARIABLE " + id;
    }
}
