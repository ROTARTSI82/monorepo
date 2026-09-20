public class Not implements Expression
{
  private Expression exp;
  
  public Not(Expression paramExpression)
  {
    exp = paramExpression;
  }
  
  public Object eval(Environment paramEnvironment)
  {
    Object localObject = exp.eval(paramEnvironment);
    if (!(localObject instanceof Boolean))
      throw new JavaTownException("Cannot negate non-boolean value:  " + localObject);
    if (((Boolean)localObject).booleanValue()) {
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }
}
