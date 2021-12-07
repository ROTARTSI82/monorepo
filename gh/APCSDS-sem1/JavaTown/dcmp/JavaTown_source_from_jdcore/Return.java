public class Return implements Statement
{
  private Expression exp;
  
  public Return(Expression paramExpression)
  {
    exp = paramExpression;
  }
  
  public void exec(Environment paramEnvironment)
  {
    Object localObject = exp.eval(paramEnvironment);
    throw new ReturnException(localObject);
  }
}
