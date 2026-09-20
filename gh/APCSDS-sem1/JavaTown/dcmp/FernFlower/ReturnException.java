
public class ReturnException extends RuntimeException {

   private Object value;


   public ReturnException(Object var1) {
      this.value = var1;
   }

   public Object getValue() {
      return this.value;
   }
}
