public class BinOp
  implements Expression
{
  public static final char PLUS = '+';
  public static final char MINUS = '-';
  public static final char TIMES = '*';
  public static final char DIVIDE = '/';
  public static final char MOD = '%';
  public static final char EQ = '=';
  public static final char NE = '!';
  public static final char AND = '&';
  public static final char OR = '|';
  public static final char LT = '<';
  public static final char LE = '[';
  public static final char GT = '>';
  public static final char GE = ']';
  private char op;
  private Expression exp1;
  private Expression exp2;
  
  public BinOp(char paramChar, Expression paramExpression1, Expression paramExpression2)
  {
    op = paramChar;
    exp1 = paramExpression1;
    exp2 = paramExpression2;
  }
  
  public Object eval(Environment paramEnvironment)
  {
    Object localObject1 = exp1.eval(paramEnvironment);
    





    if (((localObject1 instanceof Boolean)) && (op == '&'))
    {
      if (localObject1.equals(Boolean.FALSE)) {
        return Boolean.FALSE;
      }
      return exp2.eval(paramEnvironment);
    }
    if (((localObject1 instanceof Boolean)) && (op == '|'))
    {
      if (localObject1.equals(Boolean.TRUE)) {
        return Boolean.TRUE;
      }
      return exp2.eval(paramEnvironment);
    }
    
    Object localObject2 = exp2.eval(paramEnvironment);
    
    try
    {
      if (op == '+')
      {
        if (((localObject1 instanceof String)) || ((localObject2 instanceof String)))
          return "" + localObject1 + localObject2;
        if (((localObject1 instanceof Integer)) && ((localObject2 instanceof Integer))) {
          return new Integer(((Integer)localObject1).intValue() + ((Integer)localObject2).intValue());
        }
        throw new JavaTownException("Cannot add " + localObject1 + " and " + localObject2);
      }
      if (op == '-')
        return new Integer(((Integer)localObject1).intValue() - ((Integer)localObject2).intValue());
      if (op == '*')
        return new Integer(((Integer)localObject1).intValue() * ((Integer)localObject2).intValue());
      if (op == '/')
        return new Integer(((Integer)localObject1).intValue() / ((Integer)localObject2).intValue());
      if (op == '%')
        return new Integer(((Integer)localObject1).intValue() % ((Integer)localObject2).intValue());
      if (op == '=')
      {
        if (((localObject1 instanceof Integer)) || ((localObject1 instanceof Boolean))) {
          return localObject1.equals(localObject2) ? Boolean.TRUE : Boolean.FALSE;
        }
        return localObject1 == localObject2 ? Boolean.TRUE : Boolean.FALSE;
      }
      if (op == '!')
      {
        if (((localObject1 instanceof Integer)) || ((localObject1 instanceof Boolean))) {
          return localObject1.equals(localObject2) ? Boolean.FALSE : Boolean.TRUE;
        }
        return localObject1 == localObject2 ? Boolean.FALSE : Boolean.TRUE;
      }
      if (op == '>') {
        return ((Integer)localObject1).intValue() > ((Integer)localObject2).intValue() ? Boolean.TRUE : Boolean.FALSE;
      }
      if (op == ']') {
        return ((Integer)localObject1).intValue() >= ((Integer)localObject2).intValue() ? Boolean.TRUE : Boolean.FALSE;
      }
      if (op == '<') {
        return ((Integer)localObject1).intValue() < ((Integer)localObject2).intValue() ? Boolean.TRUE : Boolean.FALSE;
      }
      if (op == '[') {
        return ((Integer)localObject1).intValue() <= ((Integer)localObject2).intValue() ? Boolean.TRUE : Boolean.FALSE;
      }
      
      throw new JavaTownException("Invalid operation:  " + op);
    }
    catch (ArithmeticException localArithmeticException)
    {
      throw new JavaTownException(localArithmeticException.getMessage());
    }
  }
}
