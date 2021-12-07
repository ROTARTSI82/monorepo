import java.util.ArrayList;
import java.util.Iterator;

public class Block implements Statement {

   private ArrayList statements;


   public Block(ArrayList var1) {
      this.statements = var1;
   }

   public ArrayList getStatements() {
      return this.statements;
   }

   public void exec(Environment var1) {
      Iterator var2 = this.statements.iterator();

      while(var2.hasNext()) {
         Statement var3 = (Statement)var2.next();
         var3.exec(var1);
      }

   }
}
