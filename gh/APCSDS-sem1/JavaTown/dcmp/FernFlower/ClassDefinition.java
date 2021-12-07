import java.util.Set;

public class ClassDefinition extends Statement {

   private String name;


   public ClassDefinition(String var1, Set var2) {
      this.name = var1;
   }

   public void exec(Environment var1) {
      Office.addClass(new WorkerClass(this.name));
   }
}
