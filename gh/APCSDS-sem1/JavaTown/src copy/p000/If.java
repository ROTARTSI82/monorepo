package p000;

public class If implements Statement {

   private Expression pred;
   private Statement cons;
   private Statement alt;


   public If(Expression var1, Statement var2, Statement var3) {
      this.pred = var1;
      this.cons = var2;
      this.alt = var3;
   }

   public void exec(Environment var1) {
      boolean var2 = ((Boolean)this.pred.eval(var1)).booleanValue();
      if(var2) {
         this.cons.exec(var1);
      } else {
         this.alt.exec(var1);
      }

   }
}
