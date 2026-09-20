
public class StringExp implements Expression {

   private String value;


   public StringExp(String var1) {
      this.value = var1;
   }

   public Object eval(Environment var1) {
      return this.value;
   }
}
