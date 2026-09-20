public class Null implements Expression {
  public Null() {}
  
  public Object eval(Environment paramEnvironment) {
    return null;
  }
}
