public class This implements Expression {
  public This() {}
  
  public Object eval(Environment paramEnvironment) {
    return paramEnvironment.getOwner();
  }
}
