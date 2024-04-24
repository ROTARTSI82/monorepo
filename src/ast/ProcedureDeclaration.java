package ast;

import parser.BoxedValue;

import java.util.ArrayList;

/**
 * ProcedureDeclaration.java
 *
 * @author Grant Yang
 * @version 2024.04.10
 * A class that represents a procedure declaration in a Pascal program.
 * This contains information about how to execute the procedure,
 * including its arguments and body code/statement.
 */
public class ProcedureDeclaration implements Expression
{
    private final Expression body;
    private final ArrayList<String> argSlots;

    /**
     * Constructs a new procedure declaration
     *
     * @param body     The code to execute when the procedure is called
     * @param argSlots The names of the arguments to the procedure
     */
    public ProcedureDeclaration(Expression body, ArrayList<String> argSlots)
    {
        this.body = body;
        this.argSlots = argSlots;
    }

    /**
     * Get the name of the ith argument to the procedure
     *
     * @param i The index of the argument to get
     * @return The name of the argument
     */
    public String getArg(int i)
    {
        return argSlots.get(i);
    }

    /**
     * Get the number of arguments to the procedure
     *
     * @return The number of arguments
     */
    public int getNArgs()
    {
        return argSlots.size();
    }

    /**
     * Execute the procedure body
     *
     * @param env The environment to execute in
     * @postcondition The effects of the code have been executed in the environment
     * and side effects like WRITELN and READLN have been performed.
     */
    @Override
    public BoxedValue eval(Environment env)
    {
        return body.eval(env);
    }

    /**
     * Get a string representation of the procedure.
     * The string is of the form "func[arg1, arg2, ...]"
     *
     * @return A string representation of the procedure
     */
    @Override
    public String toString()
    {
        return "func" + argSlots;
    }
}
