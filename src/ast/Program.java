package ast;

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
    private final Statement main;

    /**
     * Constructor for the Program class
     *
     * @param main       The main entry point statement of the program
     * @param procedures A map from procedure names to their declarations
     */
    public Program(Statement main, Map<String, ProcedureDeclaration> procedures)
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
            main.exec(env);
        }
        catch (Exception e)
        {
            env.dumpFrames();
            throw e;
        }
        return env;
    }
}
