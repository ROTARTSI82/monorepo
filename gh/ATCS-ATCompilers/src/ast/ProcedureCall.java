package ast;

import parser.BoxedValue;

import java.util.ArrayList;
import java.util.List;

public class ProcedureCall implements Expression
{
    private String name;
    private ArrayList<Expression> args;

    public ProcedureCall(String name, ArrayList<Expression> args)
    {
        this.name = name;
        this.args = args;
    }

    @Override
    public BoxedValue eval(Environment env)
    {
        ProcedureDeclaration proc = env.getProcedure(name);
        List<BoxedValue> vals = args.stream().map(e -> e.eval(env)).toList();

        if (vals.size() != proc.getNArgs())
            throw new RuntimeException(
                    "%s called with wrong number of arguments (%s): expected %s but got %s"
                            .formatted(proc, this, proc.getNArgs(), vals.size()));

        if (env.isDebug())
            System.out.println("PROCEDURE " + proc + "\t" + this + "\t" + vals);
        env.push(name);
        for (int i = 0; i < vals.size(); i++)
            env.declareVariable(proc.getArg(i), vals.get(i).get());

        try
        {
            proc.exec(env);
        }
        catch (ReturnException ignored)
        {
        }

        Object ret = env.getVariable(name).get();
        env.pop();
        if (env.isDebug())
            System.out.println("RETURN PROCEDURE " + proc + "\t" + this + "\t" + vals);

        return BoxedValue.box(ret); // rebox to force a copy
    }

    @Override
    public String toString()
    {
        return name + args;
    }
}
