package codegen;

import ast.Expression;
import ast.Program;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Emitter.java
 * Class to emit MIPS assembly code for a Pascal program.
 * This class is used by the AST nodes to generate code.
 *
 * @version 2024.05.14
 * @author Grant Yang
 */
public class Emitter
{
    /**
     * Represents a local variable in the program.
     * @param type The type of the variable
     * @param frameLoc The location of the variable in the stack frame
     *                 of the procedure
     */
    public record VarInfo(Expression.Type type, int frameLoc)
    {
    }

    private final PrintWriter out;
    private final Program parentProgram;

    /**
     * The main code segment of the program.
     */
    public StringBuilder main = new StringBuilder();

    /**
     * The size of the current stack frame.
     * This is calculated during Variable::compile() calls
     * during procedure compilation, and the information is only used at the beginning
     * of the procedure to allocate space for local variables.
     */
    public int frameSize = 0;

    /**
     * Map of local variable names to their types and stack locations
     */
    public Map<String, VarInfo> vars = new HashMap<>();

    /**
     * Map of global variable names to their types
     */
    public Map<String, Expression.Type> globalVars = new HashMap<>();

    /**
     * Whether to store variables in the global scope, in the .data segment
     * or on the stack. True for main program, false for procedures.
     */
    public boolean storeVarsGlobal = true;

    private final StringBuilder data = new StringBuilder();
    private final ArrayList<String> loopLabels = new ArrayList<>();

    /**
     * Return label to jump to on RETURN or EXIT.
     * This is set to different values when compiling procedures.
     */
    public String returnLabel = "main";

    private final HashMap<Object, String> dataCache = new HashMap<>();
    private int dataCount = 0;

    /**
     * creates an emitter for writing to a new file with given name
     * @param outputFileName the name of the file to create
     * @param parent the program to compile
     */
    public Emitter(String outputFileName, Program parent)
    {
        parentProgram = parent;
        try
        {
            out = new PrintWriter(new FileWriter(outputFileName), true);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter for the parent program
     * @return the parent program
     */
    public Program getParentProgram()
    {
        return parentProgram;
    }

    /**
     * Pushes a new loop label onto an internal stack
     * to keep track of nested loops and which loops
     * CONTINUE and BREAK expressions should refer to.
     * @param str Unique label name for the new loop
     */
    public void pushLoopLabel(String str)
    {
        loopLabels.add(str);
    }

    /**
     * Gets the current active loop label, which CONTINUE and BREAK should refer to.
     * @return The top of the loop label stack
     */
    public String getLoopLabel()
    {
        return loopLabels.getLast();
    }

    /**
     * Pops the current loop label from the internal stack.
     */
    public void popLoopLabel()
    {
        loopLabels.removeLast();
    }

    /**
     * prints one line of code to file (with non-labels indented)
     * @param code the code to emit
     */
    public void emit(String code)
    {
        if (!code.endsWith(":")) code = "\t" + code;
        main.append(code).append("\n");
    }

    /**
     * Escapes a string literal with backslashes and quotes,
     * so that .asciiz directives can be used to store it in the .data segment.
     * @param s The string to escape
     * @return The escaped string
     */
    private String escapeString(String s)
    {
        // i hope to god this is vaguely correct
        return s
                .replaceAll("\\n", "\\n")
                .replaceAll("\"", "\\\"")
                .replaceAll("\\\\", "\\\\");
    }

    /**
     * Allocates a new label and data segment entry for a literal object
     * that appears in the program. Used for strings and doubles, supports integers.
     * If the object has already been allocated a label, that label is returned,
     * and the internal hashmap is used to store the mapping.
     *
     * @param obj The object to allocate a label for
     * @return The label for the object
     */
    public String nextLabelID(Object obj)
    {
        if (dataCache.containsKey(obj))
            return dataCache.get(obj);

        String ret = "data" + dataCount++;
        data.append(ret);
        if (obj instanceof Integer)
            data.append(": .word ").append(obj);
        if (obj instanceof String)
            data.append(": .asciiz \"").append(escapeString((String) obj)).append('"');
        if (obj instanceof Double)
            data.append(": .double ").append(obj);
        data.append('\n');
        dataCache.put(obj, ret);
        return ret;
    }

    /**
     * Allocates a new label and data segment entry for a global variable.
     * @precondition If the variable has already been allocated,
     *               the previously allocated type must match the new type.
     * @param var The name of the variable
     * @param t The type of the variable
     */
    public void allocGlobalVar(String var, Expression.Type t)
    {
        if (!globalVars.containsKey(var))
        {
            System.out.println("alloc global " + var + " " + t);
            data.append('_').append(var).append(switch (t)
            {
                case Expression.Type.Int, Expression.Type.String -> ": .word 0\n";
                case Expression.Type.Double -> ": .double 0.0\n";
                default -> ": .space 0\n";
            });
        }
        else if (!globalVars.get(var).equals(t))
            throw new RuntimeException("allocGlobalVar existing var with different type: "
                    + var + " with new type " + t + " from old " + globalVars.get(var));

        globalVars.put(var, t);
    }

    /**
     * Closes the file and writes the full program to it, including
     * preamble, data segment, and text segment.
     * This should be called after all calls to emit.
     * @throws IOException if the file cannot be written to
     */
    public void close() throws IOException
    {
        out.println(Files.readString(Paths.get("preamble.asm"))
                .replaceAll("\\$date", LocalDateTime.now().toString()));
        out.println(".data");
        out.println(data);
        out.println(".text 0x00400000");
        out.println(".globl main");
        out.println("main:");
        out.println(main);
//        out.println(Files.readString(Paths.get("stdlib.asm")));
        out.close();
    }
}