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
    final Map<String, ProcedureDeclaration> procedures;
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
        emit.storeVarsGlobal = true;
        emit.vars.clear();
        main.compile(emit);
        String code = emit.main.toString();
        emit.main = new StringBuilder();

        if (emit.frameSize != 0) throw new RuntimeException("main has nonzero frame size");
//        emit.emit("move $fp $sp");
//        emit.emit("subi $sp $sp " + emit.frameSize);

        emit.emit(code.trim());
        emit.emit("li $v0 10");
        emit.emit("syscall");

        emit.storeVarsGlobal = false;
        procedures.forEach((name, proc) ->
        {
            emit.frameSize = 8;
            emit.vars.clear();
            for (int i = 0; i < proc.getNArgs(); i++)
                new Variable(proc.getArg(i).getKey()).hintType(proc.getArg(i).getValue(), emit);

            StringBuilder old = emit.main;
            emit.main = new StringBuilder();
            proc.compile(emit);
            String procCode = emit.main.toString();
            emit.main = old;

            emit.emit("proc_" + name + ":");
            emit.emit("sw $fp ($sp)");
            emit.emit("sw $ra -4($sp)");
            emit.emit("move $fp $sp");
            emit.emit("subi $sp $sp " + emit.frameSize);
            emit.emit(procCode.trim());
            emit.emitReturn();
            // return codez not impl
        });
    }
}
