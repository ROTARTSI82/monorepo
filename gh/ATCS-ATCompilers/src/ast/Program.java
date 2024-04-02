package ast;

import java.util.ArrayList;
import java.util.Map;

public class Program
{
    Map<String, ProcedureDeclaration> procs;
    private Statement main;

    public Program(Statement main, Map<String, ProcedureDeclaration> procs)
    {
        this.main = main;
        this.procs = procs;
    }

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
