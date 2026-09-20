public class IntExp implements Expression
{
  private int value;
  
  public IntExp(int paramInt)
  {
    value = paramInt;
  }
  
  public Object eval(Environment paramEnvironment)
  {
    return new Integer(value);
  }
}
