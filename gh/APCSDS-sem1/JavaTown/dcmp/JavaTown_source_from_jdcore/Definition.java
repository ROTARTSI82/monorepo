import java.util.List;

public class Definition implements Statement
{
  private String name;
  private List decls;
  
  public Definition(String paramString, List paramList)
  {
    name = paramString;
    decls = paramList;
  }
  
  public void exec(Environment paramEnvironment)
  {
    World.addClass(new Type(name, decls));
  }
}
