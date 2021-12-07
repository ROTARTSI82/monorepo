
public class Return implements Statement {

   private Expression exp;


   public Return(Expression var1) {
      this.exp = var1;
   }

   public void exec(Environment var1) {
      Object var2 = this.exp.eval(var1);
      throw new ReturnException(var2);
   }
}
