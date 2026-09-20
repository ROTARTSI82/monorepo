
public class Not implements Expression {

   private Expression exp;


   public Not(Expression var1) {
      this.exp = var1;
   }

   public Object eval(Environment var1) {
      Object var2 = this.exp.eval(var1);
      if(!(var2 instanceof Boolean)) {
         throw new JavaTownException("Cannot negate non-boolean value:  " + var2);
      } else {
         return ((Boolean)var2).booleanValue()?Boolean.FALSE:Boolean.TRUE;
      }
   }
}
