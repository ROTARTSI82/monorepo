package ast;

import codegen.Emitter;

import java.util.Map;

/**
 * OperatorCodegen.java
 * Interface for code generation for operators
 *
 * @version 2024.03.28
 * @author Grant Yang
 */
public interface OperatorCodegen
{
    /**
     * Map of normal integer operators to their MIPS equivalents
     */
    Map<String, String> INT_BORING_CODEGEN = Map.ofEntries(
            Map.entry("*", "mul"),
            Map.entry("/", "div"),
            Map.entry("mod", "rem"),
            Map.entry("+", "add"),
            Map.entry("-", "sub"),

            Map.entry("=", "seq"),
            Map.entry("<>", "sne"),
            Map.entry(">=", "sge"),
            Map.entry("<=", "sle"),
            Map.entry(">", "sgt"),
            Map.entry("<", "slt")
    );

    /**
     * Map of expression types to the corresponding register
     * that expressions get stored in for compile()
     */
    Map<Expression.Type, String> REG_SRC = Map.of(
            Expression.Type.String, "$a0",
            Expression.Type.Int, "$v0"
//            Expression.Type.Array, "$s1"
    );

    /**
     * Map of special expressions with specialized code generation
     */
    Map<String, OperatorCodegen> INT_TRICKY_CODEGEN = Map.ofEntries(
            Map.entry("OR", (e, a, b, i) ->
            {
                a.compile(e);
                e.emit("bnez $v0 shortCirc" + i);
                b.compile(e);
                e.emit("shortCirc" + i + ": # binop (" + a + " OR " + b + "):");
            }),
            Map.entry("AND", (e, a, b, i) ->
            {
                a.compile(e);
                e.emit("beqz $v0 shortCirc" + i);
                b.compile(e);
                e.emit("shortCirc" + i + ": # binop (" + a + " AND " + b + "):");
            }),
            Map.entry(":=", (e, a, b, i) ->
            {
                b.compile(e);
                e.emit("push $v0");
                a.hintType(Expression.Type.Int, e);
                a.compileLValue(e);
                e.emit("pop $v0");
                e.emit("sw $v0 ($s0) # binop (" + a + " := " + b + ")");
            })
    );

    /**
     * Map of Pascal double comparison operators to their MIPS equivalents
     */
    Map<String, String> DOUBLE_COMPARES = Map.ofEntries(
            Map.entry("=", "c.eq.d $f2 $f0\n\tmovt"),
            Map.entry("<>", "c.eq.d $f2 $f0\n\tmovf"),
            Map.entry(">=", "c.le.d $f0 $f2\n\tmovt"),
            Map.entry("<=", "c.le.d $f2 $f0\n\tmovt"),
            Map.entry(">", "c.lt.d $f0 $f2\n\tmovt"),
            Map.entry("<", "c.lt.d $f2 $f0\n\tmovt")
    );

    /**
     * Apply the operator to the left and right expressions, emitting the code
     * into emit and compiling the expressions into MIPS assembly.
     * @param emit Emitter object to emit the code into
     * @param left The left expression
     * @param right The right expression
     * @param count A unique identifier supplied by the caller to be used for labels
     */
    void apply(Emitter emit, Expression left, Expression right, int count);
}
