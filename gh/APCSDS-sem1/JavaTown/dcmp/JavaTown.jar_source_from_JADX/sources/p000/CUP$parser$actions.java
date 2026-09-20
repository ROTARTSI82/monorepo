package p000;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java_cup.runtime.Symbol;
import java_cup.runtime.lr_parser;

/* renamed from: CUP$parser$actions */
/* compiled from: parser */
class CUP$parser$actions {
    private final parser parser;

    CUP$parser$actions(parser parser2) {
        this.parser = parser2;
    }

    public final Symbol CUP$parser$do_action(int i, lr_parser lr_parser, Stack stack, int i2) throws Exception {
        Object obj = null;
        switch (i) {
            case 0:
                return new Symbol(1, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, (Object) null);
            case 1:
                int i3 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i4 = ((Symbol) stack.elementAt(i2 - 1)).right;
                Symbol symbol = new Symbol(0, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, ((Symbol) stack.elementAt(i2 - 1)).value);
                lr_parser.done_parsing();
                return symbol;
            case sym.SEMI:
                return new Symbol(1, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, (Object) null);
            case sym.LPAREN:
                int i5 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i6 = ((Symbol) stack.elementAt(i2 + 0)).right;
                World.exec((Statement) ((Symbol) stack.elementAt(i2 + 0)).value);
                return new Symbol(15, ((Symbol) stack.elementAt(i2 + 0)).right, ((Symbol) stack.elementAt(i2 + 0)).right, (Object) null);
            case sym.RPAREN:
                if (((Symbol) stack.elementAt(i2 - 1)).value != null) {
                    obj = ((Symbol) stack.elementAt(i2 - 1)).value;
                }
                int i7 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i8 = ((Symbol) stack.elementAt(i2 - 2)).right;
                Statement statement = (Statement) ((Symbol) stack.elementAt(i2 - 2)).value;
                return new Symbol(2, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, obj);
            case sym.LBRACE:
                int i9 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i10 = ((Symbol) stack.elementAt(i2 - 2)).right;
                return new Symbol(5, ((Symbol) stack.elementAt(i2 - 4)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Definition((String) ((Symbol) stack.elementAt(i2 - 2)).value, new ArrayList()));
            case sym.RBRACE:
                int i11 = ((Symbol) stack.elementAt(i2 - 3)).left;
                int i12 = ((Symbol) stack.elementAt(i2 - 3)).right;
                int i13 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i14 = ((Symbol) stack.elementAt(i2 - 1)).right;
                return new Symbol(5, ((Symbol) stack.elementAt(i2 - 5)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Definition((String) ((Symbol) stack.elementAt(i2 - 3)).value, (List) ((Symbol) stack.elementAt(i2 - 1)).value));
            case sym.ASSIGN:
                int i15 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i16 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i17 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i18 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(5, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Assignment((String) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.COMMA:
                int i19 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i20 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(5, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, (Statement) ((Symbol) stack.elementAt(i2 + 0)).value);
            case sym.PERIOD:
                int i21 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i22 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(5, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Return((Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.f10EQ:
                return new Symbol(5, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Return(new Null()));
            case sym.f16NE:
                int i23 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i24 = ((Symbol) stack.elementAt(i2 - 1)).right;
                int i25 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i26 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Instantiation((String) ((Symbol) stack.elementAt(i2 - 1)).value, (List) ((Symbol) stack.elementAt(i2 + 0)).value));
            case 12:
                int i27 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i28 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Variable((String) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.f11GE:
                return new Symbol(6, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Null());
            case 14:
                int i29 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i30 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value);
            case sym.f14LE:
                int i31 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i32 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i33 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i34 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.PLUS, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.f15LT:
                int i35 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i36 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i37 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i38 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.MINUS, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.AND:
                int i39 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i40 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i41 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i42 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.TIMES, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.f17OR:
                int i43 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i44 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i45 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i46 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.DIVIDE, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.PLUS:
                int i47 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i48 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i49 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i50 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.MOD, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.MINUS:
                int i51 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i52 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new IntExp(((Integer) ((Symbol) stack.elementAt(i2 + 0)).value).intValue()));
            case sym.TIMES:
                int i53 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i54 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.MINUS, new IntExp(0), (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.DIVIDE:
                int i55 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i56 = ((Symbol) stack.elementAt(i2 - 1)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, (Expression) ((Symbol) stack.elementAt(i2 - 1)).value);
            case sym.MOD:
                int i57 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i58 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BoolExp(((Boolean) ((Symbol) stack.elementAt(i2 + 0)).value).booleanValue()));
            case sym.UMINUS:
                int i59 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i60 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i61 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i62 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.f0EQ, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.PUBLIC:
                return new Symbol(6, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new This());
            case sym.CLASS:
                int i63 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i64 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Not((Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.NEW:
                int i65 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i66 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i67 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i68 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.f5NE, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.NULL:
                int i69 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i70 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i71 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i72 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.AND, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.PRIVATE:
                int i73 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i74 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i75 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i76 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.f6OR, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.RETURN:
                int i77 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i78 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i79 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i80 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.f4LT, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.f13IF:
                int i81 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i82 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i83 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i84 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.f3LE, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.THIS:
                int i85 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i86 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i87 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i88 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.f2GT, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.ELSE:
                int i89 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i90 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i91 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i92 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new BinOp(BinOp.f1GE, (Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (Expression) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.IDENTIFIER:
                int i93 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i94 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(6, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new StringExp((String) ((Symbol) stack.elementAt(i2 + 0)).value));
            case sym.STRING:
                int i95 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i96 = ((Symbol) stack.elementAt(i2 - 1)).right;
                List list = (List) ((Symbol) stack.elementAt(i2 - 1)).value;
                int i97 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i98 = ((Symbol) stack.elementAt(i2 + 0)).right;
                list.add(((Symbol) stack.elementAt(i2 + 0)).value);
                return new Symbol(13, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, list);
            case sym.NUMBER:
                int i99 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i100 = ((Symbol) stack.elementAt(i2 + 0)).right;
                Object obj2 = ((Symbol) stack.elementAt(i2 + 0)).value;
                ArrayList arrayList = new ArrayList();
                arrayList.add(obj2);
                return new Symbol(13, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, arrayList);
            case sym.BOOLEAN:
                int i101 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i102 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i103 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i104 = ((Symbol) stack.elementAt(i2 - 1)).right;
                int i105 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i106 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(3, ((Symbol) stack.elementAt(i2 - 3)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Method(0, (String) ((Symbol) stack.elementAt(i2 - 2)).value, (List) ((Symbol) stack.elementAt(i2 - 1)).value, (List) ((Symbol) stack.elementAt(i2 + 0)).value));
            case 38:
                int i107 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i108 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i109 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i110 = ((Symbol) stack.elementAt(i2 - 1)).right;
                int i111 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i112 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(3, ((Symbol) stack.elementAt(i2 - 3)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Method(1, (String) ((Symbol) stack.elementAt(i2 - 2)).value, (List) ((Symbol) stack.elementAt(i2 - 1)).value, (List) ((Symbol) stack.elementAt(i2 + 0)).value));
            case 39:
                int i113 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i114 = ((Symbol) stack.elementAt(i2 - 1)).right;
                return new Symbol(3, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, (String) ((Symbol) stack.elementAt(i2 - 1)).value);
            case 40:
                return new Symbol(7, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new ArrayList());
            case 41:
                int i115 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i116 = ((Symbol) stack.elementAt(i2 - 1)).right;
                return new Symbol(7, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, (List) ((Symbol) stack.elementAt(i2 - 1)).value);
            case 42:
                int i117 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i118 = ((Symbol) stack.elementAt(i2 - 2)).right;
                List list2 = (List) ((Symbol) stack.elementAt(i2 - 2)).value;
                int i119 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i120 = ((Symbol) stack.elementAt(i2 + 0)).right;
                list2.add((String) ((Symbol) stack.elementAt(i2 + 0)).value);
                return new Symbol(8, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, list2);
            case 43:
                int i121 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i122 = ((Symbol) stack.elementAt(i2 + 0)).right;
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add((String) ((Symbol) stack.elementAt(i2 + 0)).value);
                return new Symbol(8, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, arrayList2);
            case 44:
                return new Symbol(9, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new ArrayList());
            case 45:
                int i123 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i124 = ((Symbol) stack.elementAt(i2 - 1)).right;
                return new Symbol(9, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, (List) ((Symbol) stack.elementAt(i2 - 1)).value);
            case 46:
                int i125 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i126 = ((Symbol) stack.elementAt(i2 - 2)).right;
                List list3 = (List) ((Symbol) stack.elementAt(i2 - 2)).value;
                int i127 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i128 = ((Symbol) stack.elementAt(i2 - 1)).right;
                list3.add((Statement) ((Symbol) stack.elementAt(i2 - 1)).value);
                return new Symbol(10, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, list3);
            case 47:
                int i129 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i130 = ((Symbol) stack.elementAt(i2 - 1)).right;
                ArrayList arrayList3 = new ArrayList();
                arrayList3.add((Statement) ((Symbol) stack.elementAt(i2 - 1)).value);
                return new Symbol(10, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, arrayList3);
            case 48:
                int i131 = ((Symbol) stack.elementAt(i2 - 5)).left;
                int i132 = ((Symbol) stack.elementAt(i2 - 5)).right;
                List list4 = (List) ((Symbol) stack.elementAt(i2 - 5)).value;
                int i133 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i134 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i135 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i136 = ((Symbol) stack.elementAt(i2 + 0)).right;
                list4.add(new C0000If((Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (List) ((Symbol) stack.elementAt(i2 + 0)).value, new ArrayList()));
                return new Symbol(10, ((Symbol) stack.elementAt(i2 - 5)).left, ((Symbol) stack.elementAt(i2 + 0)).right, list4);
            case 49:
                int i137 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i138 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i139 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i140 = ((Symbol) stack.elementAt(i2 + 0)).right;
                ArrayList arrayList4 = new ArrayList();
                arrayList4.add(new C0000If((Expression) ((Symbol) stack.elementAt(i2 - 2)).value, (List) ((Symbol) stack.elementAt(i2 + 0)).value, new ArrayList()));
                return new Symbol(10, ((Symbol) stack.elementAt(i2 - 4)).left, ((Symbol) stack.elementAt(i2 + 0)).right, arrayList4);
            case 50:
                int i141 = ((Symbol) stack.elementAt(i2 - 7)).left;
                int i142 = ((Symbol) stack.elementAt(i2 - 7)).right;
                List list5 = (List) ((Symbol) stack.elementAt(i2 - 7)).value;
                int i143 = ((Symbol) stack.elementAt(i2 - 4)).left;
                int i144 = ((Symbol) stack.elementAt(i2 - 4)).right;
                int i145 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i146 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i147 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i148 = ((Symbol) stack.elementAt(i2 + 0)).right;
                list5.add(new C0000If((Expression) ((Symbol) stack.elementAt(i2 - 4)).value, (List) ((Symbol) stack.elementAt(i2 - 2)).value, (List) ((Symbol) stack.elementAt(i2 + 0)).value));
                return new Symbol(10, ((Symbol) stack.elementAt(i2 - 7)).left, ((Symbol) stack.elementAt(i2 + 0)).right, list5);
            case 51:
                int i149 = ((Symbol) stack.elementAt(i2 - 4)).left;
                int i150 = ((Symbol) stack.elementAt(i2 - 4)).right;
                int i151 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i152 = ((Symbol) stack.elementAt(i2 - 2)).right;
                int i153 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i154 = ((Symbol) stack.elementAt(i2 + 0)).right;
                ArrayList arrayList5 = new ArrayList();
                arrayList5.add(new C0000If((Expression) ((Symbol) stack.elementAt(i2 - 4)).value, (List) ((Symbol) stack.elementAt(i2 - 2)).value, (List) ((Symbol) stack.elementAt(i2 + 0)).value));
                return new Symbol(10, ((Symbol) stack.elementAt(i2 - 6)).left, ((Symbol) stack.elementAt(i2 + 0)).right, arrayList5);
            case 52:
                int i155 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i156 = ((Symbol) stack.elementAt(i2 - 1)).right;
                ArrayList arrayList6 = new ArrayList();
                arrayList6.add((Statement) ((Symbol) stack.elementAt(i2 - 1)).value);
                return new Symbol(14, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, arrayList6);
            case 53:
                int i157 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i158 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(14, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, (List) ((Symbol) stack.elementAt(i2 + 0)).value);
            case 54:
                return new Symbol(11, ((Symbol) stack.elementAt(i2 - 1)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new ArrayList());
            case 55:
                int i159 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i160 = ((Symbol) stack.elementAt(i2 - 1)).right;
                return new Symbol(11, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, (List) ((Symbol) stack.elementAt(i2 - 1)).value);
            case 56:
                int i161 = ((Symbol) stack.elementAt(i2 - 2)).left;
                int i162 = ((Symbol) stack.elementAt(i2 - 2)).right;
                List list6 = (List) ((Symbol) stack.elementAt(i2 - 2)).value;
                int i163 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i164 = ((Symbol) stack.elementAt(i2 + 0)).right;
                list6.add((Expression) ((Symbol) stack.elementAt(i2 + 0)).value);
                return new Symbol(12, ((Symbol) stack.elementAt(i2 - 2)).left, ((Symbol) stack.elementAt(i2 + 0)).right, list6);
            case 57:
                int i165 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i166 = ((Symbol) stack.elementAt(i2 + 0)).right;
                ArrayList arrayList7 = new ArrayList();
                arrayList7.add((Expression) ((Symbol) stack.elementAt(i2 + 0)).value);
                return new Symbol(12, ((Symbol) stack.elementAt(i2 + 0)).left, ((Symbol) stack.elementAt(i2 + 0)).right, arrayList7);
            case 58:
                int i167 = ((Symbol) stack.elementAt(i2 - 3)).left;
                int i168 = ((Symbol) stack.elementAt(i2 - 3)).right;
                int i169 = ((Symbol) stack.elementAt(i2 - 1)).left;
                int i170 = ((Symbol) stack.elementAt(i2 - 1)).right;
                int i171 = ((Symbol) stack.elementAt(i2 + 0)).left;
                int i172 = ((Symbol) stack.elementAt(i2 + 0)).right;
                return new Symbol(4, ((Symbol) stack.elementAt(i2 - 3)).left, ((Symbol) stack.elementAt(i2 + 0)).right, new Invocation((Expression) ((Symbol) stack.elementAt(i2 - 3)).value, (String) ((Symbol) stack.elementAt(i2 - 1)).value, (List) ((Symbol) stack.elementAt(i2 + 0)).value));
            default:
                throw new Exception("Invalid action number found in internal parse table");
        }
    }
}
