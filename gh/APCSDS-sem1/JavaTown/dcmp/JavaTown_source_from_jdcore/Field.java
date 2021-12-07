public class Field extends Declaration
{
  private String name;
  
  public Field(String paramString)
  {
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
}
