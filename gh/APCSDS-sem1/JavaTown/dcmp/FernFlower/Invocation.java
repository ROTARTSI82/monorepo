import java.util.List;

public class Invocation implements Statement, Expression {

   private Expression target;
   private String message;
   private List args;


   public Invocation(Expression var1, String var2, List var3) {
      this.target = var1;
      this.message = var2;
      this.args = var3;
   }

   public void exec(Environment var1) {
      this.eval(var1);
   }

   public Object eval(Environment var1) {
      Object var2 = this.target.eval(var1);
      if(var2 == null) {
         throw new JavaTownException("Null Pointer Exception:\nAttempt to pass message to null");
      } else if(!(var2 instanceof Person)) {
         throw new JavaTownException("Attempt to pass message to primitive:  " + var2);
      } else {
         return World.invoke((Person)var2, this.message, this.args, var1);
      }
   }
}
