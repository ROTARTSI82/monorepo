import java.util.Iterator;
import java.util.List;

public class Method extends Declaration
{
  public static final int PUBLIC = 0;
  public static final int PRIVATE = 1;
  private int access;
  private String name;
  private List params;
  private Block body;
  
  public Method(int paramInt, String paramString, List paramList, Block paramBlock)
  {
    access = paramInt;
    name = paramString;
    params = paramList;
    body = paramBlock;
  }
  
  public String getParam(int paramInt)
  {
    return (String)params.get(paramInt);
  }
  
  public Iterator body()
  {
    return body.getStatements().iterator();
  }
  
  public String getName()
  {
    return name;
  }
  
  public int getParamCount()
  {
    return params.size();
  }
  
  public boolean isPublic()
  {
    return access == 0;
  }
}
