
public class BinOp implements Expression {

   public static final char PLUS = '+';
   public static final char MINUS = '-';
   public static final char TIMES = '*';
   public static final char DIVIDE = '/';
   public static final char MOD = '%';
   public static final char EQ = '=';
   public static final char NE = '!';
   public static final char AND = '&';
   public static final char OR = '|';
   public static final char LT = '<';
   public static final char LE = '[';
   public static final char GT = '>';
   public static final char GE = ']';
   private char op;
   private Expression exp1;
   private Expression exp2;


   public BinOp(char var1, Expression var2, Expression var3) {
      this.op = var1;
      this.exp1 = var2;
      this.exp2 = var3;
   }

   public Object eval(Environment var1) {
      Object var2 = this.exp1.eval(var1);
      if(var2 instanceof Boolean && this.op == 38) {
         return var2.equals(Boolean.FALSE)?Boolean.FALSE:this.exp2.eval(var1);
      } else if(var2 instanceof Boolean && this.op == 124) {
         return var2.equals(Boolean.TRUE)?Boolean.TRUE:this.exp2.eval(var1);
      } else {
         Object var3 = this.exp2.eval(var1);

         try {
            if(this.op == 43) {
               if(!(var2 instanceof String) && !(var3 instanceof String)) {
                  if(var2 instanceof Integer && var3 instanceof Integer) {
                     return new Integer(((Integer)var2).intValue() + ((Integer)var3).intValue());
                  } else {
                     throw new JavaTownException("Cannot add " + var2 + " and " + var3);
                  }
               } else {
                  return "" + var2 + var3;
               }
            } else if(this.op == 45) {
               return new Integer(((Integer)var2).intValue() - ((Integer)var3).intValue());
            } else if(this.op == 42) {
               return new Integer(((Integer)var2).intValue() * ((Integer)var3).intValue());
            } else if(this.op == 47) {
               return new Integer(((Integer)var2).intValue() / ((Integer)var3).intValue());
            } else if(this.op == 37) {
               return new Integer(((Integer)var2).intValue() % ((Integer)var3).intValue());
            } else if(this.op == 61) {
               return !(var2 instanceof Integer) && !(var2 instanceof Boolean)?(var2 == var3?Boolean.TRUE:Boolean.FALSE):(var2.equals(var3)?Boolean.TRUE:Boolean.FALSE);
            } else if(this.op == 33) {
               return !(var2 instanceof Integer) && !(var2 instanceof Boolean)?(var2 == var3?Boolean.FALSE:Boolean.TRUE):(var2.equals(var3)?Boolean.FALSE:Boolean.TRUE);
            } else if(this.op == 62) {
               return ((Integer)var2).intValue() > ((Integer)var3).intValue()?Boolean.TRUE:Boolean.FALSE;
            } else if(this.op == 93) {
               return ((Integer)var2).intValue() >= ((Integer)var3).intValue()?Boolean.TRUE:Boolean.FALSE;
            } else if(this.op == 60) {
               return ((Integer)var2).intValue() < ((Integer)var3).intValue()?Boolean.TRUE:Boolean.FALSE;
            } else if(this.op == 91) {
               return ((Integer)var2).intValue() <= ((Integer)var3).intValue()?Boolean.TRUE:Boolean.FALSE;
            } else {
               throw new JavaTownException("Invalid operation:  " + this.op);
            }
         } catch (ArithmeticException var5) {
            throw new JavaTownException(var5.getMessage());
         }
      }
   }
}
