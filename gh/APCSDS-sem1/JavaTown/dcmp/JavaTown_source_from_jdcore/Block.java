import java.util.ArrayList;

public class Block implements Statement
{
  private ArrayList<Statement> statements;
  
  public Block(ArrayList<Statement> paramArrayList)
  {
    statements = paramArrayList;
  }
  
  public ArrayList<Statement> getStatements()
  {
    return statements;
  }
  
  public void exec(Environment paramEnvironment)
  {
    for (Statement localStatement : statements) {
      localStatement.exec(paramEnvironment);
    }
  }
}
