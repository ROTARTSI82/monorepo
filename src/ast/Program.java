package ast;

import codegen.Emitter;

import java.util.Map;

/**
 * Program.java
 *
 * @author Grant Yang
 * @version 2024.04.10
 * A class that represents a full Pascal program,
 * containing a main statement and procedures defined in the program.
 */
public class Program
{
    public final Map<String, ProcedureDeclaration> procedures;
    private final Expression main;

    /**
     * Constructor for the Program class
     *
     * @param main       The main entry point statement of the program
     * @param procedures A map from procedure names to their declarations
     */
    public Program(Expression main, Map<String, ProcedureDeclaration> procedures)
    {
        this.main = main;
        this.procedures = procedures;
    }

    /**
     * Execute the program. This is a pure function.
     *
     * @return The environment after the program has executed
     */
    public Environment exec()
    {
        Environment env = new Environment(this);
        try
        {
            env.push("GLOBAL");
            main.eval(env);
        }
        catch (Exception e)
        {
            env.dumpFrames();
            throw e;
        }
        return env;
    }

    public void compile(Emitter emit)
    {
        emit.frameSize = 0;
        emit.returnLabel = "main";
        emit.storeVarsGlobal = true;
        emit.vars.clear();

        main.compile(emit);
        if (emit.frameSize != 0) throw new RuntimeException("main has nonzero frame size");

        emit.emit("return_main:");
        emit.emit("immediateReturn_main:");
        emit.emit("li $v0 10");
        emit.emit("syscall");

        emit.storeVarsGlobal = false;
        for (Map.Entry<String, ProcedureDeclaration> raw : procedures.entrySet())
        {
            String name = raw.getKey();
            ProcedureDeclaration proc = raw.getValue();

            emit.frameSize = 8;
            emit.vars.clear();
            emit.returnLabel = name;

            for (int i = 0; i < proc.getNArgs(); i++)
                new Variable(proc.getArg(i).getKey()).hintType(proc.getArg(i).getValue(),
                        emit);

            StringBuilder old = emit.main;
            emit.main = new StringBuilder();
            proc.compile(emit);
            String procCode = emit.main.toString();
            emit.main = old;

            emit.emit("proc_" + name + ":");
            emit.emit("sw $fp ($sp)");
            emit.emit("sw $ra -4($sp)");
            emit.emit("move $fp $sp");
            emit.emit("addi $sp $sp -" + emit.frameSize);

            emit.main.append('\t').append(procCode.trim()).append('\n');

            emit.emit("return_" + name + ":");

            if (emit.vars.containsKey(name)) // return value!
            {
                Expression.Type declared = proc.getType(emit);
                Expression.Type found = emit.vars.get(name).type();
                if (!found.equals(declared))
                    throw new RuntimeException(
                            "mismatched return variable type for " + name + ": declared "
                            + declared + " but found " + found);
                new Variable(name).compile(emit); // this is so funny
            }
            else
            {
                emit.emit("# no return value");
            }

            emit.emit("immediateReturn_" + name + ":");

            emit.emit("move $sp $fp");
//            emit.emit("addi $fp $fp -4");
            emit.emit("lw $ra -4($sp)");
            emit.emit("lw $fp ($sp)");
            emit.emit("jr $ra");
        }
    }
}
