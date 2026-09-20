
public class This implements Expression {

   public Object eval(Environment var1) {
      return var1.getOwner();
   }
}
