package ast;

import java.util.ArrayList;

public class ProcedureDeclaration implements Statement
{
    private Statement body;
    private ArrayList<String> argSlots;

    public ProcedureDeclaration(Statement body, ArrayList<String> argSlots)
    {
        this.body = body;
        this.argSlots = argSlots;
    }

    public String getArg(int i)
    {
        return argSlots.get(i);
    }

    public int getNArgs()
    {
        return argSlots.size();
    }

    @Override
    public void exec(Environment env) throws ContinueException, BreakException
    {
        body.exec(env);
    }

    @Override
    public String toString()
    {
        return "func" + argSlots;
    }
}
