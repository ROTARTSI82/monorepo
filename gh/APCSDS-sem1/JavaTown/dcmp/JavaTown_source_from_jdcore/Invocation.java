import java.util.List;

public class Invocation implements Statement, Expression
{
  private Expression target;
  private String message;
  private List args;
  
  public Invocation(Expression paramExpression, String paramString, List paramList)
  {
    target = paramExpression;
    message = paramString;
    args = paramList;
  }
  
  public void exec(Environment paramEnvironment)
  {
    eval(paramEnvironment);
  }
  
  public Object eval(Environment paramEnvironment)
  {
    Object localObject = target.eval(paramEnvironment);
    if (localObject == null)
      throw new JavaTownException("Null Pointer Exception:\nAttempt to pass message to null");
    if (!(localObject instanceof Person)) {
      throw new JavaTownException("Attempt to pass message to primitive:  " + localObject);
    }
    return World.invoke((Person)localObject, message, args, paramEnvironment);
  }
}
