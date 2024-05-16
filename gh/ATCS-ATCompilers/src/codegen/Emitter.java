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

public class Emitter
{
    public record VarInfo(Expression.Type type, int frameLoc)
    {
    }

    private final PrintWriter out;
    private final Program parentProgram;
    public StringBuilder main = new StringBuilder();
    public int frameSize = 0;
    public Map<String, VarInfo> vars = new HashMap<>();
    public Map<String, Expression.Type> globalVars = new HashMap<>();
    public boolean storeVarsGlobal = true;

    private final StringBuilder data = new StringBuilder();
    private final ArrayList<String> loopLabels = new ArrayList<>();
    public String returnLabel = "main";

    private final HashMap<Object, String> dataCache = new HashMap<>();
    private int dataCount = 0;
    public int sp = 0;

    //creates an emitter for writing to a new file with given name
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

    public Program getParentProgram()
    {
        return parentProgram;
    }

    public void pushLoopLabel(String str)
    {
        loopLabels.add(str);
    }

    public String getLoopLabel()
    {
        return loopLabels.getLast();
    }

    public void popLoopLabel()
    {
        loopLabels.removeLast();
    }

	// prints one line of code to file (with non-labels indented)
    public void emit(String code)
    {
        if (!code.endsWith(":")) code = "\t" + code;
        main.append(code).append("\n");
    }

    private String escapeString(String s)
    {
        // i hope to god this is vaguely correct
        return s
                .replaceAll("\\n", "\\n")
                .replaceAll("\"", "\\\"")
                .replaceAll("\\\\", "\\\\");
    }

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

    //closes the file.  should be called after all calls to emit.
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