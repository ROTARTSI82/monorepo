import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Environment {

   private List vars = new ArrayList();
   private Map values = new HashMap();
   private Environment parent;
   private String name;
   private Person owner;


   public Environment(String var1, Environment var2, Person var3) {
      this.name = var1;
      this.parent = var2;
      this.owner = var3;
   }

   public void set(String var1, Object var2) {
      if(this.values.containsKey(var1)) {
         this.values.put(var1, var2);
      } else if(this.parent != null && this.parent.containsVar(var1)) {
         this.parent.set(var1, var2);
      } else {
         this.vars.add(var1);
         this.values.put(var1, var2);
      }

   }

   public void define(String var1, Object var2) {
      this.vars.add(var1);
      this.values.put(var1, var2);
   }

   public Iterator variables() {
      return this.vars.iterator();
   }

   public Object get(String var1) {
      if(this.containsVar(var1)) {
         return this.values.get(var1);
      } else if(this.parent == null) {
         throw new JavaTownException("Unbound variable:  " + var1);
      } else {
         return this.parent.get(var1);
      }
   }

   public boolean containsVar(String var1) {
      return this.values.containsKey(var1);
   }

   public String getName() {
      return this.name;
   }

   public Person getOwner() {
      return this.owner;
   }

   public void clear() {
      this.vars = new ArrayList();
      this.values = new HashMap();
   }
}
