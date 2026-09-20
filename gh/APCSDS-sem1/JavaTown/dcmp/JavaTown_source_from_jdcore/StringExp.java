public class StringExp implements Expression
{
  private String value;
  
  public StringExp(String paramString)
  {
    value = paramString;
  }
  
  public Object eval(Environment paramEnvironment)
  {
    return value;
  }
}
