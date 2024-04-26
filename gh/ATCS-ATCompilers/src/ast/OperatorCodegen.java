package ast;

import codegen.Emitter;

import java.util.Map;

public interface OperatorCodegen
{
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

    Map<Expression.Type, String> REG_SRC = Map.of(
            Expression.Type.String, "$a0",
            Expression.Type.Int, "$v0"
//            Expression.Type.Array, "$s1"
    );

    Map<String, OperatorCodegen> INT_TRICKY_CODEGEN = Map.ofEntries(
            Map.entry("OR", (e, a, b, i) ->
            {
                a.compile(e);
                e.emit("bnez $v0 shortCirc" + i);
                b.compile(e);
                e.emit("shortCirc" + i + ":");
            }),
            Map.entry("AND", (e, a, b, i) ->
            {
                a.compile(e);
                e.emit("beqz $v0 shortCirc" + i);
                b.compile(e);
                e.emit("shortCirc" + i + ":");
            }),
            Map.entry(":=", (e, a, b, i) ->
            {
                b.compile(e);
                e.emitPush32("$v0");
                a.hintType(Expression.Type.Int, e);
                a.compileLValue(e);
                e.emitPop32("$v0");
                e.emit("sw $v0 ($s0)");
            })
    );

    Map<String, String> DOUBLE_COMPARES = Map.ofEntries(
            Map.entry("=", "c.eq.d $f2 $f4\n\tmovt"),
            Map.entry("<>", "c.eq.d $f2 $f4\n\tmovf"),
            Map.entry(">=", "c.le.d $f4 $f2\n\tmovt"),
            Map.entry("<=", "c.le.d $f2 $f4\n\tmovt"),
            Map.entry(">", "c.lt.d $f4 $f2\n\tmovt"),
            Map.entry("<", "c.lt.d $f2 $f4\n\tmovt")
    );

    void apply(Emitter emit, Expression left, Expression right, int count);
}
