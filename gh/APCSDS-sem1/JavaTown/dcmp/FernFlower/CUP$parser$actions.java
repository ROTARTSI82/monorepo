import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java_cup.runtime.Symbol;
import java_cup.runtime.lr_parser;

class CUP$parser$actions {

   private final parser parser;


   CUP$parser$actions(parser var1) {
      this.parser = var1;
   }

   public final Symbol CUP$parser$do_action(int var1, lr_parser var2, Stack var3, int var4) throws Exception {
      Symbol var5;
      Invocation var6;
      int var7;
      int var8;
      Expression var9;
      int var10;
      int var11;
      String var12;
      int var13;
      int var14;
      List var15;
      ArrayList var19;
      List var20;
      Statement var21;
      Expression var22;
      List var23;
      String var24;
      Object var27;
      Method var28;
      BinOp var31;
      Return var41;
      Definition var44;
      switch(var1) {
      case 0:
         var6 = null;
         var5 = new Symbol(1, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var6);
         return var5;
      case 1:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var27 = ((Symbol)var3.elementAt(var4 - 1)).value;
         var5 = new Symbol(0, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var27);
         var2.done_parsing();
         return var5;
      case 2:
         var6 = null;
         var5 = new Symbol(1, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var6);
         return var5;
      case 3:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var21 = (Statement)((Symbol)var3.elementAt(var4 - 0)).value;
         World.exec(var21);
         var5 = new Symbol(15, ((Symbol)var3.elementAt(var4 - 0)).right, ((Symbol)var3.elementAt(var4 - 0)).right, var6);
         return var5;
      case 4:
         Object var45 = null;
         if(((Symbol)var3.elementAt(var4 - 1)).value != null) {
            var45 = ((Symbol)var3.elementAt(var4 - 1)).value;
         }

         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var21 = (Statement)((Symbol)var3.elementAt(var4 - 2)).value;
         var5 = new Symbol(2, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var45);
         return var5;
      case 5:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var24 = (String)((Symbol)var3.elementAt(var4 - 2)).value;
         var44 = new Definition(var24, new ArrayList());
         var5 = new Symbol(5, ((Symbol)var3.elementAt(var4 - 4)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var44);
         return var5;
      case 6:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 3)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 3)).right;
         var24 = (String)((Symbol)var3.elementAt(var4 - 3)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var23 = (List)((Symbol)var3.elementAt(var4 - 1)).value;
         var44 = new Definition(var24, var23);
         var5 = new Symbol(5, ((Symbol)var3.elementAt(var4 - 5)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var44);
         return var5;
      case 7:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var24 = (String)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         Assignment var43 = new Assignment(var24, var22);
         var5 = new Symbol(5, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var43);
         return var5;
      case 8:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var27 = ((Symbol)var3.elementAt(var4 - 0)).value;
         Statement var42 = (Statement)var27;
         var5 = new Symbol(5, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var42);
         return var5;
      case 9:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var41 = new Return(var9);
         var5 = new Symbol(5, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var41);
         return var5;
      case 10:
         var6 = null;
         var41 = new Return(new Null());
         var5 = new Symbol(5, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var41);
         return var5;
      case 11:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var24 = (String)((Symbol)var3.elementAt(var4 - 1)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var23 = (List)((Symbol)var3.elementAt(var4 - 0)).value;
         Instantiation var40 = new Instantiation(var24, var23);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var40);
         return var5;
      case 12:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var24 = (String)((Symbol)var3.elementAt(var4 - 0)).value;
         Variable var39 = new Variable(var24);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var39);
         return var5;
      case 13:
         var6 = null;
         Null var38 = new Null();
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var38);
         return var5;
      case 14:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var27 = ((Symbol)var3.elementAt(var4 - 0)).value;
         Expression var37 = (Expression)var27;
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var37);
         return var5;
      case 15:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('+', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 16:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('-', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 17:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('*', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 18:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('/', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 19:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('%', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 20:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         Integer var32 = (Integer)((Symbol)var3.elementAt(var4 - 0)).value;
         IntExp var36 = new IntExp(var32.intValue());
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var36);
         return var5;
      case 21:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('-', new IntExp(0), var9);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 22:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 1)).value;
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var9);
         return var5;
      case 23:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         Boolean var30 = (Boolean)((Symbol)var3.elementAt(var4 - 0)).value;
         BoolExp var35 = new BoolExp(var30.booleanValue());
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var35);
         return var5;
      case 24:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('=', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 25:
         var6 = null;
         This var34 = new This();
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var34);
         return var5;
      case 26:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         Not var33 = new Not(var9);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var33);
         return var5;
      case 27:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('!', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 28:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('&', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 29:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('|', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 30:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('<', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 31:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('[', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 32:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp('>', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 33:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var31 = new BinOp(']', var9, var22);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var31);
         return var5;
      case 34:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var24 = (String)((Symbol)var3.elementAt(var4 - 0)).value;
         StringExp var29 = new StringExp(var24);
         var5 = new Symbol(6, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var29);
         return var5;
      case 35:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var20 = (List)((Symbol)var3.elementAt(var4 - 1)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         Object var26 = ((Symbol)var3.elementAt(var4 - 0)).value;
         var20.add(var26);
         var5 = new Symbol(13, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var20);
         return var5;
      case 36:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var27 = ((Symbol)var3.elementAt(var4 - 0)).value;
         var19 = new ArrayList();
         var19.add(var27);
         var5 = new Symbol(13, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var19);
         return var5;
      case 37:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var24 = (String)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var23 = (List)((Symbol)var3.elementAt(var4 - 1)).value;
         var13 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var14 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var15 = (List)((Symbol)var3.elementAt(var4 - 0)).value;
         var28 = new Method(0, var24, var23, var15);
         var5 = new Symbol(3, ((Symbol)var3.elementAt(var4 - 3)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var28);
         return var5;
      case 38:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var24 = (String)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var23 = (List)((Symbol)var3.elementAt(var4 - 1)).value;
         var13 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var14 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var15 = (List)((Symbol)var3.elementAt(var4 - 0)).value;
         var28 = new Method(1, var24, var23, var15);
         var5 = new Symbol(3, ((Symbol)var3.elementAt(var4 - 3)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var28);
         return var5;
      case 39:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var24 = (String)((Symbol)var3.elementAt(var4 - 1)).value;
         var5 = new Symbol(3, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var24);
         return var5;
      case 40:
         var6 = null;
         var19 = new ArrayList();
         var5 = new Symbol(7, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var19);
         return var5;
      case 41:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var20 = (List)((Symbol)var3.elementAt(var4 - 1)).value;
         var5 = new Symbol(7, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var20);
         return var5;
      case 42:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var20 = (List)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var12 = (String)((Symbol)var3.elementAt(var4 - 0)).value;
         var20.add(var12);
         var5 = new Symbol(8, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var20);
         return var5;
      case 43:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var24 = (String)((Symbol)var3.elementAt(var4 - 0)).value;
         var19 = new ArrayList();
         var19.add(var24);
         var5 = new Symbol(8, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var19);
         return var5;
      case 44:
         var6 = null;
         var19 = new ArrayList();
         var5 = new Symbol(9, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var19);
         return var5;
      case 45:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var20 = (List)((Symbol)var3.elementAt(var4 - 1)).value;
         var5 = new Symbol(9, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var20);
         return var5;
      case 46:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var20 = (List)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 1)).right;
         Statement var25 = (Statement)((Symbol)var3.elementAt(var4 - 1)).value;
         var20.add(var25);
         var5 = new Symbol(10, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var20);
         return var5;
      case 47:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var21 = (Statement)((Symbol)var3.elementAt(var4 - 1)).value;
         var19 = new ArrayList();
         var19.add(var21);
         var5 = new Symbol(10, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var19);
         return var5;
      case 48:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 5)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 5)).right;
         var20 = (List)((Symbol)var3.elementAt(var4 - 5)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var13 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var14 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var15 = (List)((Symbol)var3.elementAt(var4 - 0)).value;
         var20.add(new If(var22, var15, new ArrayList()));
         var5 = new Symbol(10, ((Symbol)var3.elementAt(var4 - 5)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var20);
         return var5;
      case 49:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var23 = (List)((Symbol)var3.elementAt(var4 - 0)).value;
         var19 = new ArrayList();
         var19.add(new If(var9, var23, new ArrayList()));
         var5 = new Symbol(10, ((Symbol)var3.elementAt(var4 - 4)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var19);
         return var5;
      case 50:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 7)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 7)).right;
         var20 = (List)((Symbol)var3.elementAt(var4 - 7)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 4)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 4)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 4)).value;
         var13 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var14 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var15 = (List)((Symbol)var3.elementAt(var4 - 2)).value;
         int var16 = ((Symbol)var3.elementAt(var4 - 0)).left;
         int var17 = ((Symbol)var3.elementAt(var4 - 0)).right;
         List var18 = (List)((Symbol)var3.elementAt(var4 - 0)).value;
         var20.add(new If(var22, var15, var18));
         var5 = new Symbol(10, ((Symbol)var3.elementAt(var4 - 7)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var20);
         return var5;
      case 51:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 4)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 4)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 4)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var23 = (List)((Symbol)var3.elementAt(var4 - 2)).value;
         var13 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var14 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var15 = (List)((Symbol)var3.elementAt(var4 - 0)).value;
         var19 = new ArrayList();
         var19.add(new If(var9, var23, var15));
         var5 = new Symbol(10, ((Symbol)var3.elementAt(var4 - 6)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var19);
         return var5;
      case 52:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var21 = (Statement)((Symbol)var3.elementAt(var4 - 1)).value;
         var19 = new ArrayList();
         var19.add(var21);
         var5 = new Symbol(14, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var19);
         return var5;
      case 53:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var20 = (List)((Symbol)var3.elementAt(var4 - 0)).value;
         var5 = new Symbol(14, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var20);
         return var5;
      case 54:
         var6 = null;
         var19 = new ArrayList();
         var5 = new Symbol(11, ((Symbol)var3.elementAt(var4 - 1)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var19);
         return var5;
      case 55:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var20 = (List)((Symbol)var3.elementAt(var4 - 1)).value;
         var5 = new Symbol(11, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var20);
         return var5;
      case 56:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 2)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 2)).right;
         var20 = (List)((Symbol)var3.elementAt(var4 - 2)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var22 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var20.add(var22);
         var5 = new Symbol(12, ((Symbol)var3.elementAt(var4 - 2)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var20);
         return var5;
      case 57:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 0)).value;
         var19 = new ArrayList();
         var19.add(var9);
         var5 = new Symbol(12, ((Symbol)var3.elementAt(var4 - 0)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var19);
         return var5;
      case 58:
         var6 = null;
         var7 = ((Symbol)var3.elementAt(var4 - 3)).left;
         var8 = ((Symbol)var3.elementAt(var4 - 3)).right;
         var9 = (Expression)((Symbol)var3.elementAt(var4 - 3)).value;
         var10 = ((Symbol)var3.elementAt(var4 - 1)).left;
         var11 = ((Symbol)var3.elementAt(var4 - 1)).right;
         var12 = (String)((Symbol)var3.elementAt(var4 - 1)).value;
         var13 = ((Symbol)var3.elementAt(var4 - 0)).left;
         var14 = ((Symbol)var3.elementAt(var4 - 0)).right;
         var15 = (List)((Symbol)var3.elementAt(var4 - 0)).value;
         var6 = new Invocation(var9, var12, var15);
         var5 = new Symbol(4, ((Symbol)var3.elementAt(var4 - 3)).left, ((Symbol)var3.elementAt(var4 - 0)).right, var6);
         return var5;
      default:
         throw new Exception("Invalid action number found in internal parse table");
      }
   }
}
