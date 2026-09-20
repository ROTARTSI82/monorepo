import java.util.List;

public class Instantiation implements Expression {

   private String name;
   private List args;


   public Instantiation(String var1, List var2) {
      this.name = var1;
      this.args = var2;
   }

   public Object eval(Environment var1) {
      return World.instantiate(this.name, this.args, var1);
   }
}
