package ast;

import codegen.Emitter;
import parser.BoxedValue;

import java.util.ArrayList;
import java.util.Map;

/**
 * ProcedureDeclaration.java
 *
 * @author Grant Yang
 * @version 2024.04.10
 * A class that represents a procedure declaration in a Pascal program.
 * This contains information about how to execute the procedure,
 * including its arguments and body code/statement.
 */
public class ProcedureDeclaration extends Expression
{
    private final Expression body;
    private final Expression.Type retType;
    private final ArrayList<Map.Entry<String, Type>> argSlots;

    /**
     * Constructs a new procedure declaration
     *
     * @param body     The code to execute when the procedure is called
     * @param argSlots The names of the arguments to the procedure
     * @param ret      The return type of this function
     */
    public ProcedureDeclaration(Expression body, ArrayList<Map.Entry<String, Type>> argSlots,
                                Expression.Type ret)
    {
        this.body = body;
        this.argSlots = argSlots;
        this.retType = ret;
    }

    @Override
    public Type getType(Emitter e)
    {
        // the body's type is supposed to be null/ignored
        return retType;
    }

    /**
     * Get the name of the ith argument to the procedure
     *
     * @param i The index of the argument to get
     * @return The name of the argument and its type
     */
    public Map.Entry<String, Type> getArg(int i)
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

    @Override
    public void compile(Emitter emit)
    {
        body.compile(emit);
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
