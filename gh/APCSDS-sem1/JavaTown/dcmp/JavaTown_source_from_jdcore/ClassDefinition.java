import java.util.Set;

public class ClassDefinition extends Statement
{
  private String name;
  
  public ClassDefinition(String paramString, Set paramSet)
  {
    name = paramString;
  }
  
  public void exec(Environment paramEnvironment)
  {
    Office.addClass(new WorkerClass(name));
  }
}
