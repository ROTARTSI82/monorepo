
public class BoolExp implements Expression {

   private boolean value;


   public BoolExp(boolean var1) {
      this.value = var1;
   }

   public Object eval(Environment var1) {
      return this.value?Boolean.TRUE:Boolean.FALSE;
   }
}
