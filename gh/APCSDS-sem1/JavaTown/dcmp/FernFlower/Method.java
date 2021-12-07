import java.util.Iterator;
import java.util.List;

public class Method extends Declaration {

   public static final int PUBLIC = 0;
   public static final int PRIVATE = 1;
   private int access;
   private String name;
   private List params;
   private Block body;


   public Method(int var1, String var2, List var3, Block var4) {
      this.access = var1;
      this.name = var2;
      this.params = var3;
      this.body = var4;
   }

   public String getParam(int var1) {
      return (String)this.params.get(var1);
   }

   public Iterator body() {
      return this.body.getStatements().iterator();
   }

   public String getName() {
      return this.name;
   }

   public int getParamCount() {
      return this.params.size();
   }

   public boolean isPublic() {
      return this.access == 0;
   }
}
