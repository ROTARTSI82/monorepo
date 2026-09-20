public class Assignment implements Statement
{
  private String var;
  private Expression exp;
  
  public Assignment(String paramString, Expression paramExpression)
  {
    var = paramString;
    exp = paramExpression;
  }
  
  public void exec(Environment paramEnvironment)
  {
    paramEnvironment.set(var, exp.eval(paramEnvironment));
    World.redraw();
  }
}
