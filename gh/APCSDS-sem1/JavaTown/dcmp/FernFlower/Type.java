import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Type {

   private String name;
   private List fields = new ArrayList();
   private Set methods = new HashSet();


   public Type(String var1, List var2) {
      this.name = var1;
      Iterator var3 = var2.iterator();
      boolean var4 = false;

      while(var3.hasNext()) {
         Declaration var5 = (Declaration)var3.next();
         if(var5 instanceof Field) {
            this.fields.add((Field)var5);
         } else {
            if(!(var5 instanceof Method)) {
               throw new JavaTownException("Invalid declaration:  " + var5);
            }

            Method var6 = (Method)var5;
            if(var6.getName().equals(var1)) {
               var4 = true;
            }

            this.methods.add(var6);
         }
      }

      if(!var4) {
         this.methods.add(new Method(0, var1, new ArrayList(), new Block(new ArrayList())));
      }

   }

   public String getName() {
      return this.name;
   }

   public Iterator fields() {
      return this.fields.iterator();
   }

   public Method getMethod(String var1, int var2) {
      Iterator var3 = this.methods.iterator();

      Method var4;
      do {
         if(!var3.hasNext()) {
            throw new JavaTownException("No method " + var1 + " with " + var2 + " parameters");
         }

         var4 = (Method)var3.next();
      } while(!var4.getName().equals(var1) || var4.getParamCount() != var2);

      return var4;
   }
}
