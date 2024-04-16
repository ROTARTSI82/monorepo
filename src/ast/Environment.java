package ast;

import parser.BoxedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Environment.java
 *
 * @author Grant Yang
 * @version 2024.03.28
 * A class that holds variables and their values in
 * the Pascal stack frames, along with the Program with procedure definitions.
 */
public class Environment
{
    private final ArrayList<Map<String, BoxedValue>> variables = new ArrayList<>();
    private final ArrayList<String> frameNames = new ArrayList<>();
    private final Program parent;

    /**
     * Construct a new environment with a parent program
     *
     * @param parent The program that this environment is a part of.
     *               This object contains the procedure definitions.
     */
    public Environment(Program parent)
    {
        this.parent = parent;
    }

    /**
     * Push a new stack frame onto the environment
     *
     * @param name The name of the new stack frame.
     *             This is only currently used for debugging purposes.
     * @postcondition The new stack frame is the current frame
     */
    public void push(String name)
    {
        frameNames.add(name);
        variables.add(new HashMap<>());
    }

    /**
     * Pop the current stack frame off the environment
     *
     * @postcondition The current stack frame is removed,
     * and the previous frame is now activated.
     */
    public void pop()
    {
        variables.removeLast();
        frameNames.removeLast();
    }

    /**
     * Get the name of the current active stack frame
     *
     * @return The name of the current stack frame set in push()
     */
    public String getFrameName()
    {
        return frameNames.getLast();
    }

    /**
     * Set a variable in the environment.
     * If the variable does not exist, it is created in the current active frame.
     *
     * @param name     The name of the variable
     * @param rawValue The value of the variable, not a BoxedValue
     * @postcondition The variable in the environment has been updated
     */
    public void setVariable(String name, Object rawValue)
    {
        if (rawValue instanceof BoxedValue)
            throw new RuntimeException("setVariable() takes raw value, not boxed value");
        for (int i = variables.size() - 1; i >= 0; i--)
            if (variables.get(i).containsKey(name))
            {
                variables.get(i).get(name).set(rawValue);
                return;
            }
        declareVariable(name, rawValue);
    }

    /**
     * Declare a new variable in the environment in the current active stack frame.
     * If the variable already exists in the current frame, it is overwritten.
     * However, if the variable exists in a parent frame, it is not overwritten
     * but rather shadowed by the new variable.
     *
     * @param name  The name of the variable
     * @param value The value of the variable
     * @postcondition The variable in the current frame has been updated
     */
    public void declareVariable(String name, Object value)
    {
        if (isDebug())
        {
            System.out.println("NEW VAR OR SET declareVariable(): " + name + " = " + value);
            System.out.print("\t");
            dumpFrames();
        }

        if (!variables.getLast().containsKey(name))
            variables.getLast().put(name, BoxedValue.box(value));
        else
            variables.getLast().get(name).set(value);
    }

    /**
     * Get a variable from the environment, or create it if it doesn't exist.
     * This method searches the stack frames from the current frame all the way
     * to the global frame for the variable. If it does not exist in any parent frame,
     * it is created in the current active stack frame.
     *
     * @param name Name of the variable to retrieve
     * @return The BoxedValue of the variable
     * @postcondition The variable is initialized to null if it did not already exist
     */
    public BoxedValue getVariable(String name)
    {
        for (int i = variables.size() - 1; i >= 0; i--)
            if (variables.get(i).containsKey(name))
                return variables.get(i).get(name);

        if (isDebug())
        {
            System.out.println("NEW VARIABLE getVariable() creates: " + name);
            System.out.print("\t");
            dumpFrames();
        }

        BoxedValue val = BoxedValue.newNamed(name);
        variables.getLast().put(name, val);
        return val;
    }

    /**
     * Retrieve a procedure from the parent program.
     * Note that this method does not search the stack frames for procedures.
     *
     * @param name The name of the procedure to retrieve
     * @return The ProcedureDeclaration object for the procedure
     */
    public ProcedureDeclaration getProcedure(String name)
    {
        return parent.procedures.get(name);
    }

    /**
     * Dump the stack frames to the console for debugging purposes.
     */
    public void dumpFrames()
    {
        System.out.println("Frames: " + frameNames);
    }

    /**
     * Check if the environment is in debug mode.
     *
     * @return True if debug information should be printed, false otherwise.
     */
    public boolean isDebug()
    {
        return false;
    }
}
