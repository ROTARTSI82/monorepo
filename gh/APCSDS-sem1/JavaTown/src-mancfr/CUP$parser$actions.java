/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  java_cup.runtime.Symbol
 *  java_cup.runtime.lr_parser
 *  parser
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java_cup.runtime.Symbol;
import java_cup.runtime.lr_parser;

class CUP$parser$actions {
    private final lr_parser parser;

    CUP$parser$actions(lr_parser parser2) {
        this.parser = parser2;
    }

    public final Symbol CUP$parser$do_action(int n, lr_parser lr_parser2, Stack stack, int n2) throws Exception {
        switch (n) {
            case 58: {
                Invocation invocation = null;
                int n3 = ((Symbol)stack.elementAt((int)(n2 - 3))).left;
                int n4 = ((Symbol)stack.elementAt((int)(n2 - 3))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 3))).value;
                int n5 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n6 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                int n7 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n8 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                List list = (List)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                invocation = new Invocation(expression, string, list);
                Symbol symbol = new Symbol(4, ((Symbol)stack.elementAt((int)(n2 - 3))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)invocation);
                return symbol;
            }
            case 57: {
                ArrayList<Expression> arrayList = null;
                int n9 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n10 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                arrayList = new ArrayList<Expression>();
                arrayList.add(expression);
                Symbol symbol = new Symbol(12, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, arrayList);
                return symbol;
            }
            case 56: {
                List list = null;
                int n11 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n12 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                List list2 = (List)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n13 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n14 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                list2.add(expression);
                list = list2;
                Symbol symbol = new Symbol(12, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)list);
                return symbol;
            }
            case 55: {
                List list;
                List list3 = null;
                int n15 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n16 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                list3 = list = (List)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                Symbol symbol = new Symbol(11, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)list3);
                return symbol;
            }
            case 54: {
                ArrayList arrayList = null;
                arrayList = new ArrayList();
                Symbol symbol = new Symbol(11, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, arrayList);
                return symbol;
            }
            case 53: {
                List list;
                List list4 = null;
                int n17 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n18 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                list4 = list = (List)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                Symbol symbol = new Symbol(14, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)list4);
                return symbol;
            }
            case 52: {
                ArrayList<Statement> arrayList = null;
                int n19 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n20 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                Statement statement = (Statement)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                arrayList = new ArrayList<Statement>();
                arrayList.add(statement);
                Symbol symbol = new Symbol(14, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, arrayList);
                return symbol;
            }
            case 51: {
                ArrayList<If> arrayList = null;
                int n21 = ((Symbol)stack.elementAt((int)(n2 - 4))).left;
                int n22 = ((Symbol)stack.elementAt((int)(n2 - 4))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 4))).value;
                int n23 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n24 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                List list = (List)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n25 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n26 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                List list5 = (List)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                arrayList = new ArrayList<If>();
                arrayList.add(new If(expression, list, list5));
                Symbol symbol = new Symbol(10, ((Symbol)stack.elementAt((int)(n2 - 6))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, arrayList);
                return symbol;
            }
            case 50: {
                List list = null;
                int n27 = ((Symbol)stack.elementAt((int)(n2 - 7))).left;
                int n28 = ((Symbol)stack.elementAt((int)(n2 - 7))).right;
                List list6 = (List)((Symbol)stack.elementAt((int)(n2 - 7))).value;
                int n29 = ((Symbol)stack.elementAt((int)(n2 - 4))).left;
                int n30 = ((Symbol)stack.elementAt((int)(n2 - 4))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 4))).value;
                int n31 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n32 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                List list7 = (List)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n33 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n34 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                List list8 = (List)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                list6.add(new If(expression, list7, list8));
                list = list6;
                Symbol symbol = new Symbol(10, ((Symbol)stack.elementAt((int)(n2 - 7))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)list);
                return symbol;
            }
            case 49: {
                ArrayList<If> arrayList = null;
                int n35 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n36 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n37 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n38 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                List list = (List)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                arrayList = new ArrayList<If>();
                arrayList.add(new If(expression, list, new ArrayList()));
                Symbol symbol = new Symbol(10, ((Symbol)stack.elementAt((int)(n2 - 4))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, arrayList);
                return symbol;
            }
            case 48: {
                List list = null;
                int n39 = ((Symbol)stack.elementAt((int)(n2 - 5))).left;
                int n40 = ((Symbol)stack.elementAt((int)(n2 - 5))).right;
                List list9 = (List)((Symbol)stack.elementAt((int)(n2 - 5))).value;
                int n41 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n42 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n43 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n44 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                List list10 = (List)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                list9.add(new If(expression, list10, new ArrayList()));
                list = list9;
                Symbol symbol = new Symbol(10, ((Symbol)stack.elementAt((int)(n2 - 5))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)list);
                return symbol;
            }
            case 47: {
                ArrayList<Statement> arrayList = null;
                int n45 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n46 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                Statement statement = (Statement)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                arrayList = new ArrayList<Statement>();
                arrayList.add(statement);
                Symbol symbol = new Symbol(10, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, arrayList);
                return symbol;
            }
            case 46: {
                List list = null;
                int n47 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n48 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                List list11 = (List)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n49 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n50 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                Statement statement = (Statement)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                list11.add(statement);
                list = list11;
                Symbol symbol = new Symbol(10, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)list);
                return symbol;
            }
            case 45: {
                List list;
                List list12 = null;
                int n51 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n52 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                list12 = list = (List)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                Symbol symbol = new Symbol(9, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)list12);
                return symbol;
            }
            case 44: {
                ArrayList arrayList = null;
                arrayList = new ArrayList();
                Symbol symbol = new Symbol(9, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, arrayList);
                return symbol;
            }
            case 43: {
                ArrayList<String> arrayList = null;
                int n53 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n54 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                arrayList = new ArrayList<String>();
                arrayList.add(string);
                Symbol symbol = new Symbol(8, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, arrayList);
                return symbol;
            }
            case 42: {
                List list = null;
                int n55 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n56 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                List list13 = (List)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n57 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n58 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                list13.add(string);
                list = list13;
                Symbol symbol = new Symbol(8, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)list);
                return symbol;
            }
            case 41: {
                List list;
                List list14 = null;
                int n59 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n60 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                list14 = list = (List)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                Symbol symbol = new Symbol(7, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)list14);
                return symbol;
            }
            case 40: {
                ArrayList arrayList = null;
                arrayList = new ArrayList();
                Symbol symbol = new Symbol(7, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, arrayList);
                return symbol;
            }
            case 39: {
                String string;
                String string2 = null;
                int n61 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n62 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                string2 = string = (String)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                Symbol symbol = new Symbol(3, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)string2);
                return symbol;
            }
            case 38: {
                Method method = null;
                int n63 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n64 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n65 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n66 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                List list = (List)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                int n67 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n68 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                List list15 = (List)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                method = new Method(1, string, list, list15);
                Symbol symbol = new Symbol(3, ((Symbol)stack.elementAt((int)(n2 - 3))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)method);
                return symbol;
            }
            case 37: {
                Method method = null;
                int n69 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n70 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n71 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n72 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                List list = (List)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                int n73 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n74 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                List list16 = (List)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                method = new Method(0, string, list, list16);
                Symbol symbol = new Symbol(3, ((Symbol)stack.elementAt((int)(n2 - 3))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)method);
                return symbol;
            }
            case 36: {
                ArrayList<Object> arrayList = null;
                int n75 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n76 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Object object = ((Symbol)stack.elementAt((int)(n2 - 0))).value;
                arrayList = new ArrayList<Object>();
                arrayList.add(object);
                Symbol symbol = new Symbol(13, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, arrayList);
                return symbol;
            }
            case 35: {
                List list = null;
                int n77 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n78 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                List list17 = (List)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                int n79 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n80 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Object object = ((Symbol)stack.elementAt((int)(n2 - 0))).value;
                list17.add(object);
                list = list17;
                Symbol symbol = new Symbol(13, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)list);
                return symbol;
            }
            case 34: {
                StringExp stringExp = null;
                int n81 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n82 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                stringExp = new StringExp(string);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)stringExp);
                return symbol;
            }
            case 33: {
                BinOp binOp = null;
                int n83 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n84 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n85 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n86 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression2 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp(']', expression, expression2);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 32: {
                BinOp binOp = null;
                int n87 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n88 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n89 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n90 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression3 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('>', expression, expression3);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 31: {
                BinOp binOp = null;
                int n91 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n92 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n93 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n94 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression4 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('[', expression, expression4);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 30: {
                BinOp binOp = null;
                int n95 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n96 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n97 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n98 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression5 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('<', expression, expression5);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 29: {
                BinOp binOp = null;
                int n99 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n100 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n101 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n102 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression6 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('|', expression, expression6);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 28: {
                BinOp binOp = null;
                int n103 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n104 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n105 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n106 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression7 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('&', expression, expression7);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 27: {
                BinOp binOp = null;
                int n107 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n108 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n109 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n110 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression8 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('!', expression, expression8);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 26: {
                Not not = null;
                int n111 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n112 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                not = new Not(expression);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)not);
                return symbol;
            }
            case 25: {
                This this_ = null;
                this_ = new This();
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)this_);
                return symbol;
            }
            case 24: {
                BinOp binOp = null;
                int n113 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n114 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n115 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n116 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression9 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('=', expression, expression9);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 23: {
                BoolExp boolExp = null;
                int n117 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n118 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Boolean bl = (Boolean)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                boolExp = new BoolExp(bl);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)boolExp);
                return symbol;
            }
            case 22: {
                Expression expression;
                Expression expression10 = null;
                int n119 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n120 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                expression10 = expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)expression10);
                return symbol;
            }
            case 21: {
                BinOp binOp = null;
                int n121 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n122 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('-', new IntExp(0), expression);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 20: {
                IntExp intExp = null;
                int n123 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n124 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Integer n125 = (Integer)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                intExp = new IntExp(n125);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)intExp);
                return symbol;
            }
            case 19: {
                BinOp binOp = null;
                int n126 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n127 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n128 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n129 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression11 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('%', expression, expression11);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 18: {
                BinOp binOp = null;
                int n130 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n131 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n132 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n133 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression12 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('/', expression, expression12);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 17: {
                BinOp binOp = null;
                int n134 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n135 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n136 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n137 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression13 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('*', expression, expression13);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 16: {
                BinOp binOp = null;
                int n138 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n139 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n140 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n141 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression14 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('-', expression, expression14);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 15: {
                BinOp binOp = null;
                int n142 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n143 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n144 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n145 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression15 = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                binOp = new BinOp('+', expression, expression15);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)binOp);
                return symbol;
            }
            case 14: {
                Expression expression = null;
                int n146 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n147 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Object object = ((Symbol)stack.elementAt((int)(n2 - 0))).value;
                expression = (Expression)object;
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)expression);
                return symbol;
            }
            case 13: {
                Null nullVal = null;
                nullVal = new Null();
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)nullVal);
                return symbol;
            }
            case 12: {
                Variable variable = null;
                int n148 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n149 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                variable = new Variable(string);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)variable);
                return symbol;
            }
            case 11: {
                Instantiation instantiation = null;
                int n150 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n151 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                int n152 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n153 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                List list = (List)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                instantiation = new Instantiation(string, list);
                Symbol symbol = new Symbol(6, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)instantiation);
                return symbol;
            }
            case 10: {
                Return return_ = null;
                return_ = new Return(new Null());
                Symbol symbol = new Symbol(5, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)return_);
                return symbol;
            }
            case 9: {
                Return return_ = null;
                int n154 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n155 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                return_ = new Return(expression);
                Symbol symbol = new Symbol(5, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)return_);
                return symbol;
            }
            case 8: {
                Statement statement = null;
                int n156 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n157 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Object object = ((Symbol)stack.elementAt((int)(n2 - 0))).value;
                statement = (Statement)object;
                Symbol symbol = new Symbol(5, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)statement);
                return symbol;
            }
            case 7: {
                Assignment assignment = null;
                int n158 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n159 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                int n160 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n161 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Expression expression = (Expression)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                assignment = new Assignment(string, expression);
                Symbol symbol = new Symbol(5, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)assignment);
                return symbol;
            }
            case 6: {
                Definition definition = null;
                int n162 = ((Symbol)stack.elementAt((int)(n2 - 3))).left;
                int n163 = ((Symbol)stack.elementAt((int)(n2 - 3))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 3))).value;
                int n164 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n165 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                List list = (List)((Symbol)stack.elementAt((int)(n2 - 1))).value;
                definition = new Definition(string, list);
                Symbol symbol = new Symbol(5, ((Symbol)stack.elementAt((int)(n2 - 5))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)definition);
                return symbol;
            }
            case 5: {
                Definition definition = null;
                int n166 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n167 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                String string = (String)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                definition = new Definition(string, new ArrayList());
                Symbol symbol = new Symbol(5, ((Symbol)stack.elementAt((int)(n2 - 4))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, (Object)definition);
                return symbol;
            }
            case 4: {
                Object object = null;
                if (((Symbol)stack.elementAt((int)(n2 - 1))).value != null) {
                    object = ((Symbol)stack.elementAt((int)(n2 - 1))).value;
                }
                int n168 = ((Symbol)stack.elementAt((int)(n2 - 2))).left;
                int n169 = ((Symbol)stack.elementAt((int)(n2 - 2))).right;
                Statement statement = (Statement)((Symbol)stack.elementAt((int)(n2 - 2))).value;
                Symbol symbol = new Symbol(2, ((Symbol)stack.elementAt((int)(n2 - 2))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, object);
                return symbol;
            }
            case 3: {
                Object var6_60 = null;
                int n170 = ((Symbol)stack.elementAt((int)(n2 - 0))).left;
                int n171 = ((Symbol)stack.elementAt((int)(n2 - 0))).right;
                Statement statement = (Statement)((Symbol)stack.elementAt((int)(n2 - 0))).value;
                World.exec(statement);
                Symbol symbol = new Symbol(15, ((Symbol)stack.elementAt((int)(n2 - 0))).right, ((Symbol)stack.elementAt((int)(n2 - 0))).right, var6_60);
                return symbol;
            }
            case 2: {
                Object var6_61 = null;
                Symbol symbol = new Symbol(1, ((Symbol)stack.elementAt((int)(n2 - 0))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, var6_61);
                return symbol;
            }
            case 1: {
                Object object;
                Object object2 = null;
                int n172 = ((Symbol)stack.elementAt((int)(n2 - 1))).left;
                int n173 = ((Symbol)stack.elementAt((int)(n2 - 1))).right;
                object2 = object = ((Symbol)stack.elementAt((int)(n2 - 1))).value;
                Symbol symbol = new Symbol(0, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, object2);
                lr_parser2.done_parsing();
                return symbol;
            }
            case 0: {
                Object var6_63 = null;
                Symbol symbol = new Symbol(1, ((Symbol)stack.elementAt((int)(n2 - 1))).left, ((Symbol)stack.elementAt((int)(n2 - 0))).right, var6_63);
                return symbol;
            }
        }
        throw new Exception("Invalid action number found in internal parse table");
    }
}

