
public class IntExp implements Expression {

   private int value;


   public IntExp(int var1) {
      this.value = var1;
   }

   public Object eval(Environment var1) {
      return new Integer(this.value);
   }
}
