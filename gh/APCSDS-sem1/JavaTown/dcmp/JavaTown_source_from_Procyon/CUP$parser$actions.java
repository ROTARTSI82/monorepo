import java.util.ArrayList;
import java.util.List;
import java_cup.runtime.Symbol;
import java.util.Stack;
import java_cup.runtime.lr_parser;

// 
// Decompiled by Procyon v0.5.36
// 

class CUP$parser$actions
{
    private final parser parser;
    
    CUP$parser$actions(final parser parser) {
        this.parser = parser;
    }
    
    public final Symbol CUP$parser$do_action(final int n, final lr_parser lr_parser, final Stack stack, final int n2) throws Exception {
        switch (n) {
            case 58: {
                final int left = stack.elementAt(n2 - 3).left;
                final int right = stack.elementAt(n2 - 3).right;
                final Expression expression = (Expression)stack.elementAt(n2 - 3).value;
                final int left2 = stack.elementAt(n2 - 1).left;
                final int right2 = stack.elementAt(n2 - 1).right;
                final String s = (String)stack.elementAt(n2 - 1).value;
                final int left3 = stack.elementAt(n2 - 0).left;
                final int right3 = stack.elementAt(n2 - 0).right;
                return new Symbol(4, stack.elementAt(n2 - 3).left, stack.elementAt(n2 - 0).right, (Object)new Invocation(expression, s, (List)stack.elementAt(n2 - 0).value));
            }
            case 57: {
                final int left4 = stack.elementAt(n2 - 0).left;
                final int right4 = stack.elementAt(n2 - 0).right;
                final Expression expression2 = (Expression)stack.elementAt(n2 - 0).value;
                final ArrayList<Expression> list = new ArrayList<Expression>();
                list.add(expression2);
                return new Symbol(12, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)list);
            }
            case 56: {
                final int left5 = stack.elementAt(n2 - 2).left;
                final int right5 = stack.elementAt(n2 - 2).right;
                final List list2 = (List)stack.elementAt(n2 - 2).value;
                final int left6 = stack.elementAt(n2 - 0).left;
                final int right6 = stack.elementAt(n2 - 0).right;
                list2.add(stack.elementAt(n2 - 0).value);
                return new Symbol(12, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)list2);
            }
            case 55: {
                final int left7 = stack.elementAt(n2 - 1).left;
                final int right7 = stack.elementAt(n2 - 1).right;
                return new Symbol(11, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)stack.elementAt(n2 - 1).value);
            }
            case 54: {
                return new Symbol(11, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, (Object)new ArrayList());
            }
            case 53: {
                final int left8 = stack.elementAt(n2 - 0).left;
                final int right8 = stack.elementAt(n2 - 0).right;
                return new Symbol(14, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)stack.elementAt(n2 - 0).value);
            }
            case 52: {
                final int left9 = stack.elementAt(n2 - 1).left;
                final int right9 = stack.elementAt(n2 - 1).right;
                final Statement statement = (Statement)stack.elementAt(n2 - 1).value;
                final ArrayList<Statement> list3 = new ArrayList<Statement>();
                list3.add(statement);
                return new Symbol(14, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, (Object)list3);
            }
            case 51: {
                final int left10 = stack.elementAt(n2 - 4).left;
                final int right10 = stack.elementAt(n2 - 4).right;
                final Expression expression3 = (Expression)stack.elementAt(n2 - 4).value;
                final int left11 = stack.elementAt(n2 - 2).left;
                final int right11 = stack.elementAt(n2 - 2).right;
                final List list4 = (List)stack.elementAt(n2 - 2).value;
                final int left12 = stack.elementAt(n2 - 0).left;
                final int right12 = stack.elementAt(n2 - 0).right;
                final List list5 = (List)stack.elementAt(n2 - 0).value;
                final ArrayList<If> list6 = new ArrayList<If>();
                list6.add(new If(expression3, list4, list5));
                return new Symbol(10, stack.elementAt(n2 - 6).left, stack.elementAt(n2 - 0).right, (Object)list6);
            }
            case 50: {
                final int left13 = stack.elementAt(n2 - 7).left;
                final int right13 = stack.elementAt(n2 - 7).right;
                final List list7 = (List)stack.elementAt(n2 - 7).value;
                final int left14 = stack.elementAt(n2 - 4).left;
                final int right14 = stack.elementAt(n2 - 4).right;
                final Expression expression4 = (Expression)stack.elementAt(n2 - 4).value;
                final int left15 = stack.elementAt(n2 - 2).left;
                final int right15 = stack.elementAt(n2 - 2).right;
                final List list8 = (List)stack.elementAt(n2 - 2).value;
                final int left16 = stack.elementAt(n2 - 0).left;
                final int right16 = stack.elementAt(n2 - 0).right;
                list7.add(new If(expression4, list8, (List)stack.elementAt(n2 - 0).value));
                return new Symbol(10, stack.elementAt(n2 - 7).left, stack.elementAt(n2 - 0).right, (Object)list7);
            }
            case 49: {
                final int left17 = stack.elementAt(n2 - 2).left;
                final int right17 = stack.elementAt(n2 - 2).right;
                final Expression expression5 = (Expression)stack.elementAt(n2 - 2).value;
                final int left18 = stack.elementAt(n2 - 0).left;
                final int right18 = stack.elementAt(n2 - 0).right;
                final List list9 = (List)stack.elementAt(n2 - 0).value;
                final ArrayList<If> list10 = new ArrayList<If>();
                list10.add(new If(expression5, list9, (List)new ArrayList()));
                return new Symbol(10, stack.elementAt(n2 - 4).left, stack.elementAt(n2 - 0).right, (Object)list10);
            }
            case 48: {
                final int left19 = stack.elementAt(n2 - 5).left;
                final int right19 = stack.elementAt(n2 - 5).right;
                final List list11 = (List)stack.elementAt(n2 - 5).value;
                final int left20 = stack.elementAt(n2 - 2).left;
                final int right20 = stack.elementAt(n2 - 2).right;
                final Expression expression6 = (Expression)stack.elementAt(n2 - 2).value;
                final int left21 = stack.elementAt(n2 - 0).left;
                final int right21 = stack.elementAt(n2 - 0).right;
                list11.add(new If(expression6, (List)stack.elementAt(n2 - 0).value, (List)new ArrayList()));
                return new Symbol(10, stack.elementAt(n2 - 5).left, stack.elementAt(n2 - 0).right, (Object)list11);
            }
            case 47: {
                final int left22 = stack.elementAt(n2 - 1).left;
                final int right22 = stack.elementAt(n2 - 1).right;
                final Statement statement2 = (Statement)stack.elementAt(n2 - 1).value;
                final ArrayList<Statement> list12 = new ArrayList<Statement>();
                list12.add(statement2);
                return new Symbol(10, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, (Object)list12);
            }
            case 46: {
                final int left23 = stack.elementAt(n2 - 2).left;
                final int right23 = stack.elementAt(n2 - 2).right;
                final List list13 = (List)stack.elementAt(n2 - 2).value;
                final int left24 = stack.elementAt(n2 - 1).left;
                final int right24 = stack.elementAt(n2 - 1).right;
                list13.add(stack.elementAt(n2 - 1).value);
                return new Symbol(10, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)list13);
            }
            case 45: {
                final int left25 = stack.elementAt(n2 - 1).left;
                final int right25 = stack.elementAt(n2 - 1).right;
                return new Symbol(9, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)stack.elementAt(n2 - 1).value);
            }
            case 44: {
                return new Symbol(9, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, (Object)new ArrayList());
            }
            case 43: {
                final int left26 = stack.elementAt(n2 - 0).left;
                final int right26 = stack.elementAt(n2 - 0).right;
                final String s2 = (String)stack.elementAt(n2 - 0).value;
                final ArrayList<String> list14 = new ArrayList<String>();
                list14.add(s2);
                return new Symbol(8, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)list14);
            }
            case 42: {
                final int left27 = stack.elementAt(n2 - 2).left;
                final int right27 = stack.elementAt(n2 - 2).right;
                final List list15 = (List)stack.elementAt(n2 - 2).value;
                final int left28 = stack.elementAt(n2 - 0).left;
                final int right28 = stack.elementAt(n2 - 0).right;
                list15.add(stack.elementAt(n2 - 0).value);
                return new Symbol(8, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)list15);
            }
            case 41: {
                final int left29 = stack.elementAt(n2 - 1).left;
                final int right29 = stack.elementAt(n2 - 1).right;
                return new Symbol(7, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)stack.elementAt(n2 - 1).value);
            }
            case 40: {
                return new Symbol(7, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, (Object)new ArrayList());
            }
            case 39: {
                final int left30 = stack.elementAt(n2 - 1).left;
                final int right30 = stack.elementAt(n2 - 1).right;
                return new Symbol(3, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)stack.elementAt(n2 - 1).value);
            }
            case 38: {
                final int left31 = stack.elementAt(n2 - 2).left;
                final int right31 = stack.elementAt(n2 - 2).right;
                final String s3 = (String)stack.elementAt(n2 - 2).value;
                final int left32 = stack.elementAt(n2 - 1).left;
                final int right32 = stack.elementAt(n2 - 1).right;
                final List list16 = (List)stack.elementAt(n2 - 1).value;
                final int left33 = stack.elementAt(n2 - 0).left;
                final int right33 = stack.elementAt(n2 - 0).right;
                return new Symbol(3, stack.elementAt(n2 - 3).left, stack.elementAt(n2 - 0).right, (Object)new Method(1, s3, list16, (List)stack.elementAt(n2 - 0).value));
            }
            case 37: {
                final int left34 = stack.elementAt(n2 - 2).left;
                final int right34 = stack.elementAt(n2 - 2).right;
                final String s4 = (String)stack.elementAt(n2 - 2).value;
                final int left35 = stack.elementAt(n2 - 1).left;
                final int right35 = stack.elementAt(n2 - 1).right;
                final List list17 = (List)stack.elementAt(n2 - 1).value;
                final int left36 = stack.elementAt(n2 - 0).left;
                final int right36 = stack.elementAt(n2 - 0).right;
                return new Symbol(3, stack.elementAt(n2 - 3).left, stack.elementAt(n2 - 0).right, (Object)new Method(0, s4, list17, (List)stack.elementAt(n2 - 0).value));
            }
            case 36: {
                final int left37 = stack.elementAt(n2 - 0).left;
                final int right37 = stack.elementAt(n2 - 0).right;
                final Object value = stack.elementAt(n2 - 0).value;
                final ArrayList<Object> list18 = new ArrayList<Object>();
                list18.add(value);
                return new Symbol(13, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)list18);
            }
            case 35: {
                final int left38 = stack.elementAt(n2 - 1).left;
                final int right38 = stack.elementAt(n2 - 1).right;
                final List list19 = (List)stack.elementAt(n2 - 1).value;
                final int left39 = stack.elementAt(n2 - 0).left;
                final int right39 = stack.elementAt(n2 - 0).right;
                list19.add(stack.elementAt(n2 - 0).value);
                return new Symbol(13, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, (Object)list19);
            }
            case 34: {
                final int left40 = stack.elementAt(n2 - 0).left;
                final int right40 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)new StringExp((String)stack.elementAt(n2 - 0).value));
            }
            case 33: {
                final int left41 = stack.elementAt(n2 - 2).left;
                final int right41 = stack.elementAt(n2 - 2).right;
                final Expression expression7 = (Expression)stack.elementAt(n2 - 2).value;
                final int left42 = stack.elementAt(n2 - 0).left;
                final int right42 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp(']', expression7, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 32: {
                final int left43 = stack.elementAt(n2 - 2).left;
                final int right43 = stack.elementAt(n2 - 2).right;
                final Expression expression8 = (Expression)stack.elementAt(n2 - 2).value;
                final int left44 = stack.elementAt(n2 - 0).left;
                final int right44 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('>', expression8, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 31: {
                final int left45 = stack.elementAt(n2 - 2).left;
                final int right45 = stack.elementAt(n2 - 2).right;
                final Expression expression9 = (Expression)stack.elementAt(n2 - 2).value;
                final int left46 = stack.elementAt(n2 - 0).left;
                final int right46 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('[', expression9, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 30: {
                final int left47 = stack.elementAt(n2 - 2).left;
                final int right47 = stack.elementAt(n2 - 2).right;
                final Expression expression10 = (Expression)stack.elementAt(n2 - 2).value;
                final int left48 = stack.elementAt(n2 - 0).left;
                final int right48 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('<', expression10, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 29: {
                final int left49 = stack.elementAt(n2 - 2).left;
                final int right49 = stack.elementAt(n2 - 2).right;
                final Expression expression11 = (Expression)stack.elementAt(n2 - 2).value;
                final int left50 = stack.elementAt(n2 - 0).left;
                final int right50 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('|', expression11, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 28: {
                final int left51 = stack.elementAt(n2 - 2).left;
                final int right51 = stack.elementAt(n2 - 2).right;
                final Expression expression12 = (Expression)stack.elementAt(n2 - 2).value;
                final int left52 = stack.elementAt(n2 - 0).left;
                final int right52 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('&', expression12, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 27: {
                final int left53 = stack.elementAt(n2 - 2).left;
                final int right53 = stack.elementAt(n2 - 2).right;
                final Expression expression13 = (Expression)stack.elementAt(n2 - 2).value;
                final int left54 = stack.elementAt(n2 - 0).left;
                final int right54 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('!', expression13, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 26: {
                final int left55 = stack.elementAt(n2 - 0).left;
                final int right55 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, (Object)new Not((Expression)stack.elementAt(n2 - 0).value));
            }
            case 25: {
                return new Symbol(6, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)new This());
            }
            case 24: {
                final int left56 = stack.elementAt(n2 - 2).left;
                final int right56 = stack.elementAt(n2 - 2).right;
                final Expression expression14 = (Expression)stack.elementAt(n2 - 2).value;
                final int left57 = stack.elementAt(n2 - 0).left;
                final int right57 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('=', expression14, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 23: {
                final int left58 = stack.elementAt(n2 - 0).left;
                final int right58 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)new BoolExp((boolean)stack.elementAt(n2 - 0).value));
            }
            case 22: {
                final int left59 = stack.elementAt(n2 - 1).left;
                final int right59 = stack.elementAt(n2 - 1).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)stack.elementAt(n2 - 1).value);
            }
            case 21: {
                final int left60 = stack.elementAt(n2 - 0).left;
                final int right60 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('-', new IntExp(0), (Expression)stack.elementAt(n2 - 0).value));
            }
            case 20: {
                final int left61 = stack.elementAt(n2 - 0).left;
                final int right61 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)new IntExp((int)stack.elementAt(n2 - 0).value));
            }
            case 19: {
                final int left62 = stack.elementAt(n2 - 2).left;
                final int right62 = stack.elementAt(n2 - 2).right;
                final Expression expression15 = (Expression)stack.elementAt(n2 - 2).value;
                final int left63 = stack.elementAt(n2 - 0).left;
                final int right63 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('%', expression15, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 18: {
                final int left64 = stack.elementAt(n2 - 2).left;
                final int right64 = stack.elementAt(n2 - 2).right;
                final Expression expression16 = (Expression)stack.elementAt(n2 - 2).value;
                final int left65 = stack.elementAt(n2 - 0).left;
                final int right65 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('/', expression16, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 17: {
                final int left66 = stack.elementAt(n2 - 2).left;
                final int right66 = stack.elementAt(n2 - 2).right;
                final Expression expression17 = (Expression)stack.elementAt(n2 - 2).value;
                final int left67 = stack.elementAt(n2 - 0).left;
                final int right67 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('*', expression17, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 16: {
                final int left68 = stack.elementAt(n2 - 2).left;
                final int right68 = stack.elementAt(n2 - 2).right;
                final Expression expression18 = (Expression)stack.elementAt(n2 - 2).value;
                final int left69 = stack.elementAt(n2 - 0).left;
                final int right69 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('-', expression18, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 15: {
                final int left70 = stack.elementAt(n2 - 2).left;
                final int right70 = stack.elementAt(n2 - 2).right;
                final Expression expression19 = (Expression)stack.elementAt(n2 - 2).value;
                final int left71 = stack.elementAt(n2 - 0).left;
                final int right71 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new BinOp('+', expression19, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 14: {
                final int left72 = stack.elementAt(n2 - 0).left;
                final int right72 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)stack.elementAt(n2 - 0).value);
            }
            case 13: {
                return new Symbol(6, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)new Null());
            }
            case 12: {
                final int left73 = stack.elementAt(n2 - 0).left;
                final int right73 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)new Variable((String)stack.elementAt(n2 - 0).value));
            }
            case 11: {
                final int left74 = stack.elementAt(n2 - 1).left;
                final int right74 = stack.elementAt(n2 - 1).right;
                final String s5 = (String)stack.elementAt(n2 - 1).value;
                final int left75 = stack.elementAt(n2 - 0).left;
                final int right75 = stack.elementAt(n2 - 0).right;
                return new Symbol(6, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new Instantiation(s5, (List)stack.elementAt(n2 - 0).value));
            }
            case 10: {
                return new Symbol(5, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)new Return(new Null()));
            }
            case 9: {
                final int left76 = stack.elementAt(n2 - 0).left;
                final int right76 = stack.elementAt(n2 - 0).right;
                return new Symbol(5, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, (Object)new Return((Expression)stack.elementAt(n2 - 0).value));
            }
            case 8: {
                final int left77 = stack.elementAt(n2 - 0).left;
                final int right77 = stack.elementAt(n2 - 0).right;
                return new Symbol(5, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)stack.elementAt(n2 - 0).value);
            }
            case 7: {
                final int left78 = stack.elementAt(n2 - 2).left;
                final int right78 = stack.elementAt(n2 - 2).right;
                final String s6 = (String)stack.elementAt(n2 - 2).value;
                final int left79 = stack.elementAt(n2 - 0).left;
                final int right79 = stack.elementAt(n2 - 0).right;
                return new Symbol(5, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, (Object)new Assignment(s6, (Expression)stack.elementAt(n2 - 0).value));
            }
            case 6: {
                final int left80 = stack.elementAt(n2 - 3).left;
                final int right80 = stack.elementAt(n2 - 3).right;
                final String s7 = (String)stack.elementAt(n2 - 3).value;
                final int left81 = stack.elementAt(n2 - 1).left;
                final int right81 = stack.elementAt(n2 - 1).right;
                return new Symbol(5, stack.elementAt(n2 - 5).left, stack.elementAt(n2 - 0).right, (Object)new Definition(s7, (List)stack.elementAt(n2 - 1).value));
            }
            case 5: {
                final int left82 = stack.elementAt(n2 - 2).left;
                final int right82 = stack.elementAt(n2 - 2).right;
                return new Symbol(5, stack.elementAt(n2 - 4).left, stack.elementAt(n2 - 0).right, (Object)new Definition((String)stack.elementAt(n2 - 2).value, new ArrayList()));
            }
            case 4: {
                Object value2 = null;
                if (stack.elementAt(n2 - 1).value != null) {
                    value2 = stack.elementAt(n2 - 1).value;
                }
                final int left83 = stack.elementAt(n2 - 2).left;
                final int right83 = stack.elementAt(n2 - 2).right;
                final Statement statement3 = (Statement)stack.elementAt(n2 - 2).value;
                return new Symbol(2, stack.elementAt(n2 - 2).left, stack.elementAt(n2 - 0).right, value2);
            }
            case 3: {
                final Object o = null;
                final int left84 = stack.elementAt(n2 - 0).left;
                final int right84 = stack.elementAt(n2 - 0).right;
                World.exec((Statement)stack.elementAt(n2 - 0).value);
                return new Symbol(15, stack.elementAt(n2 - 0).right, stack.elementAt(n2 - 0).right, o);
            }
            case 2: {
                return new Symbol(1, stack.elementAt(n2 - 0).left, stack.elementAt(n2 - 0).right, (Object)null);
            }
            case 1: {
                final int left85 = stack.elementAt(n2 - 1).left;
                final int right85 = stack.elementAt(n2 - 1).right;
                final Symbol symbol = new Symbol(0, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, stack.elementAt(n2 - 1).value);
                lr_parser.done_parsing();
                return symbol;
            }
            case 0: {
                return new Symbol(1, stack.elementAt(n2 - 1).left, stack.elementAt(n2 - 0).right, (Object)null);
            }
            default: {
                throw new Exception("Invalid action number found in internal parse table");
            }
        }
    }
}
