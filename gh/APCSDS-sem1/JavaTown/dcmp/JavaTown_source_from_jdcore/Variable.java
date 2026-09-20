public class Variable implements Expression
{
  private String name;
  
  public Variable(String paramString)
  {
    name = paramString;
  }
  
  public Object eval(Environment paramEnvironment)
  {
    return paramEnvironment.get(name);
  }
}
