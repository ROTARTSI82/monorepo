package codegen;

import ast.Expression;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Emitter
{
    public record VarInfo(Expression.Type type, int frameLoc)
    {
    }

    private final PrintWriter out;
    public StringBuilder main = new StringBuilder();
    public int frameSize = 0;
    public Map<String, VarInfo> vars = new HashMap<>();
    public Map<String, Expression.Type> globalVars = new HashMap<>();
    public boolean storeVarsGlobal = true;

    private final StringBuilder data = new StringBuilder();
    private final ArrayList<String> loopLabels = new ArrayList<>();

    private final HashMap<Object, String> dataCache = new HashMap<>();
    private int dataCount = 0;
    public int sp = 0;

    //creates an emitter for writing to a new file with given name
    public Emitter(String outputFileName)
    {
        try
        {
            out = new PrintWriter(new FileWriter(outputFileName), true);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

	public int emitPush32(String src)
	{
        sp += 4;
        main.append("\n\t# push i32 ").append(src).append('\n');
		main.append("\tsw " + src + " ($sp)\n");
		main.append("\tsubi $sp $sp 4\n");
        return sp - 4;
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

    public void emitPushF0()
    {
        emit("# push $f0");
        emit("mfc1.d $v0 $f0");
        emitPush32("$v0");
        emitPush32("$v1");
    }

    public void emitPopF0()
    {
        main.append("\n\t# pop f64\n");
        emitPop32("$v1");
        emitPop32("$v0");
        emit("mtc1.d $v0 $f2");
    }

	public int emitPop32(String dst)
	{
        sp -= 4;
        main.append("\n\t# pop i32 to ").append(dst).append('\n');
        main.append("\taddi $sp $sp 4\n");
		main.append("\tlw ").append(dst).append(" ($sp)\n");
        return sp;
	}

	// prints one line of code to file (with non-labels indented)
    public void emit(String code)
    {
        if (!code.endsWith(":")) code = "\t" + code;
        main.append(code).append("\n");
    }

    public void emitReturn()
    {
        emit("move $sp $fp");
        emit("subi $fp $fp 4");
        emit("lw $ra ($fp)");
        emit("lw $fp ($sp)");
        emit("jr $ra");
    }

    private String escapeString(String s)
    {
        return s
                .replaceAll("\\n", "\\n")
                .replaceAll("\"", "\\\"")
                .replaceAll("\\\\", "\\\\");
    }

    public String tryAllocGlobal(Object obj)
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
            throw new RuntimeException("allocGlobalVar existing var with different type");

        globalVars.put(var, t);
    }

    //closes the file.  should be called after all calls to emit.
    public void close()
    {
        out.println(".data");
        out.println(data);
        out.println(".text 0x00400000");
        out.println(".globl main");
        out.println("main:");
        out.println(main);
        out.close();
    }
}