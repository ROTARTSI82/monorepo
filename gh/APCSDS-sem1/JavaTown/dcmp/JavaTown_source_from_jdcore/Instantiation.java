import java.util.List;

public class Instantiation implements Expression
{
  private String name;
  private List args;
  
  public Instantiation(String paramString, List paramList)
  {
    name = paramString;
    args = paramList;
  }
  
  public Object eval(Environment paramEnvironment)
  {
    return World.instantiate(name, args, paramEnvironment);
  }
}
