package ast;

import parser.BoxedValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * ProcedureCall.java
 *
 * @author Grant Yang
 * @version 2024.04.10
 * A class that represents a procedure call node in the AST.
 * This is used to call a procedure with a given name and argument values.
 */
public class ProcedureCall implements Expression
{
    private final String name;
    private final ArrayList<Expression> args;

    /**
     * Constructs a new procedure call node
     *
     * @param name The name of the procedure to call
     * @param args The values of the arguments to pass to the procedure
     */
    public ProcedureCall(String name, ArrayList<Expression> args)
    {
        this.name = name;
        this.args = args;
    }

    /**
     * Execute a special procedure call (WRITELN, WRITE, and READLN)
     *
     * @param values The values of the arguments to pass to the procedure
     * @return The return value of the procedure.
     * WRITE and WRITELN return the string they have written to STDOUT,
     * and READLN returns an r-value copy of the value read.
     * @postcondition Side effects like writing to STDOUT or reading from
     * STDIN have been performed.
     */
    private BoxedValue specialProcedures(List<BoxedValue> values)
    {
        if (name.toUpperCase().startsWith("WRITE"))
        {
            String s = values.stream()
                    .map(e -> String.valueOf(e.get()))
                    .reduce("", (a, b) -> a + b)
                    + (name.toUpperCase().endsWith("LN") ? "\n" : "");
            System.out.print(s);
            return BoxedValue.box(s);
        }
        else if (name.equalsIgnoreCase("READLN"))
        {
            try
            {
                values.getFirst().set(new BufferedReader(
                        new InputStreamReader(System.in)).readLine());
            }
            catch (Exception err)
            {
                err.printStackTrace();
            }
            return BoxedValue.box(values.getFirst().get());
        }
        throw new RuntimeException("Unrecognized special procedure " + name);
    }

    /**
     * Evaluate the procedure call, executing the procedure with the given arguments.
     * The procedure definition is read from the global environment of the Program.
     *
     * @param env The environment to evaluate in
     * @return The (boxed) return value of the procedure
     * @postcondition Code and side effects have been executed in the environment.
     */
    @Override
    public BoxedValue eval(Environment env)
    {
        List<BoxedValue> values = args.stream().map(e -> e.eval(env)).toList();
        if (List.of("WRITE", "WRITELN", "READLN").contains(name.toUpperCase()))
            return specialProcedures(values);

        ProcedureDeclaration proc = env.getProcedure(name);
        if (values.size() != proc.getNArgs())
            throw new RuntimeException(
                    "%s called with wrong number of arguments (%s): expected %s but got %s"
                            .formatted(proc, this, proc.getNArgs(), values.size()));

        if (env.isDebug())
            System.out.println("PROCEDURE " + proc + "\t" + this + "\t" + values);
        env.push(name);
        for (int i = 0; i < values.size(); i++)
            env.declareVariable(proc.getArg(i), values.get(i).get());

        try
        {
            proc.eval(env);
        }
        catch (ReturnException ignored)
        {
        }

        Object ret = env.getVariable(name).get();
        env.pop();
        if (env.isDebug())
            System.out.println("RETURN PROCEDURE " + proc + "\t" + this + "\t" + values);

        return BoxedValue.box(ret); // re-box to force a copy
    }

    /**
     * Get a string representation of the procedure call.
     * The string is of the form "name[arg1, arg2, ...]"
     * where args are the expressions in the list.
     *
     * @return A string representation of the procedure call
     */
    @Override
    public String toString()
    {
        return name + args;
    }
}
