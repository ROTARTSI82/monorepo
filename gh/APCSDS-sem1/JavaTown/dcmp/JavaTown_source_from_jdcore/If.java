
public class If
  implements Statement
{
  private Expression pred;
  private Statement cons;
  private Statement alt;
  
  public If(Expression paramExpression, Statement paramStatement1, Statement paramStatement2)
  {
    pred = paramExpression;
    cons = paramStatement1;
    alt = paramStatement2;
  }
  
  public void exec(Environment paramEnvironment)
  {
    boolean bool = ((Boolean)pred.eval(paramEnvironment)).booleanValue();
    
    if (bool) {
      cons.exec(paramEnvironment);
    } else {
      alt.exec(paramEnvironment);
    }
  }
}
