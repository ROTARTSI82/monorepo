package ast;

import parser.BoxedValue;

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
    private final Map<String, BoxedValue> variables = new HashMap<>();

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
        if (!variables.containsKey(name))
            variables.put(name, BoxedValue.box(rawValue));
        else
            variables.get(name).set(rawValue);
    }

    /**
     * Get a variable from the environment, or create it if it doesn't exist
     * @param name Name of the variable to retrieve
     * @return The BoxedValue of the variable
     * @postcondition The variable is initialized to null if it did not already exist
     */
    public BoxedValue getVariable(String name)
    {
        if (!variables.containsKey(name))
            variables.put(name, BoxedValue.newNamed(name));
        return variables.get(name);
    }
}
