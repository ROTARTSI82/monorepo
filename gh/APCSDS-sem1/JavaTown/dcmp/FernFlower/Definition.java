import java.util.List;

public class Definition implements Statement {

   private String name;
   private List decls;


   public Definition(String var1, List var2) {
      this.name = var1;
      this.decls = var2;
   }

   public void exec(Environment var1) {
      World.addClass(new Type(this.name, this.decls));
   }
}
