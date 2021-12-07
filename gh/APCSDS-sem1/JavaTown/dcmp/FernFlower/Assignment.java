
public class Assignment implements Statement {

   private String var;
   private Expression exp;


   public Assignment(String var1, Expression var2) {
      this.var = var1;
      this.exp = var2;
   }

   public void exec(Environment var1) {
      var1.set(this.var, this.exp.eval(var1));
      World.redraw();
   }
}
