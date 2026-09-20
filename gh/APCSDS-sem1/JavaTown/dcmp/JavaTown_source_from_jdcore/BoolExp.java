public class BoolExp implements Expression
{
  private boolean value;
  
  public BoolExp(boolean paramBoolean)
  {
    value = paramBoolean;
  }
  
  public Object eval(Environment paramEnvironment)
  {
    return value ? Boolean.TRUE : Boolean.FALSE;
  }
}
