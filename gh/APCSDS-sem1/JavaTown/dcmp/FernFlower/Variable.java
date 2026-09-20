
public class Variable implements Expression {

   private String name;


   public Variable(String var1) {
      this.name = var1;
   }

   public Object eval(Environment var1) {
      return var1.get(this.name);
   }
}
