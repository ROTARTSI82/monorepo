package ast;

import parser.BoxedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Environment.java
 * @author Grant Yang
 * @version 2024.03.28
 * A class that holds variables and their values.
 * At the moment it is simply a thin wrapper around a HashMap.
 */
public class Environment
{
    private final ArrayList<Map<String, BoxedValue>> variables = new ArrayList<>();
    private final ArrayList<String> frameNames = new ArrayList<>();
    private Program parent;

    private final boolean debug = false;


    public Environment(Program parent)
    {
        this.parent = parent;
    }

    public void push(String name)
    {
        frameNames.add(name);
        variables.add(new HashMap<>());
    }

    public void pop()
    {
        variables.removeLast();
        frameNames.removeLast();
    }

    public String getFrameName()
    {
        return frameNames.getLast();
    }

    /**
     * Set a variable in the environment
     * @param name The name of the variable
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
     * Get a variable from the environment, or create it if it doesn't exist
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

    public ProcedureDeclaration getProcedure(String name)
    {
        return parent.procs.get(name);
    }

    public void dumpFrames()
    {
        System.out.println("Frames: " + frameNames);
    }

    public boolean isDebug()
    {
        return debug;
    }
}
