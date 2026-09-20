public class ReturnException extends RuntimeException
{
  private Object value;
  
  public ReturnException(Object paramObject)
  {
    value = paramObject;
  }
  
  public Object getValue()
  {
    return value;
  }
}
