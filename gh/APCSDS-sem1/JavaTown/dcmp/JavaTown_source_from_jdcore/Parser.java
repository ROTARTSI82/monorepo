import java.util.ArrayList;

public class Parser
{
  private Scanner scanner;
  private String token;
  private java.util.Stack<String> stack;
  
  public Parser(Scanner paramScanner)
  {
    scanner = paramScanner;
    stack = new java.util.Stack();
    token = paramScanner.next();
  }
  
  public ArrayList<Statement> parseStatements()
  {
    ArrayList localArrayList = new ArrayList();
    while (!token.equals(" "))
      localArrayList.add(parseStatement());
    return localArrayList;
  }
  


  private void eat(String paramString)
  {
    if ((token.equals(paramString)) || ((token.equals(" ")) && (paramString.equals(";"))))
    {
      if (stack.isEmpty())
      {
        token = scanner.next();
      }
      else
      {
        token = ((String)stack.pop());

      }
      

    }
    else
    {
      throw new JavaTownParseException("expected " + paramString + " but found " + token);
    }
  }
  
  private void barf(String paramString)
  {
    stack.push(token);
    token = paramString;
  }
  

  private Statement parseStatement()
  {
    System.out.println("parseStatement:  " + token);
    if (token.equals("public"))
    {
      eat("public");
      eat("class");
      localObject1 = token;
      eat((String)localObject1);
      eat("{");
      localObject2 = parseDeclarations();
      eat("}");
      return new Definition((String)localObject1, (java.util.List)localObject2);
    }
    if (token.equals("return"))
    {
      eat("return");
      localObject1 = parseExpression();
      eat(";");
      return new Return((Expression)localObject1);
    }
    if (token.equals("if"))
      return parseIf();
    if (token.equals("{")) {
      return parseBlock();
    }
    
    Object localObject1 = token;
    eat((String)localObject1);
    Object localObject3; if (token.equals("="))
    {
      localObject2 = localObject1;
      eat("=");
      localObject3 = parseExpression();
      eat(";");
      return new Assignment((String)localObject2, (Expression)localObject3);
    }
    

    Object localObject2 = token;
    eat((String)localObject2);
    if (token.equals("="))
    {
      localObject3 = localObject2;
      eat("=");
      Expression localExpression = parseExpression();
      eat(";");
      return new Assignment((String)localObject3, localExpression);
    }
    


    barf((String)localObject2);
    barf((String)localObject1);
    
    System.out.println(token + " " + stack);
    
    try
    {
      localObject3 = (Invocation)parseExpression();
      eat(";");
      return localObject3;
    }
    catch (ClassCastException localClassCastException)
    {
      localClassCastException.printStackTrace();
      throw new JavaTownParseException("invalid syntax");
    }
  }
  



  private ArrayList<Declaration> parseDeclarations()
  {
    ArrayList localArrayList = new ArrayList();
    while (!token.equals("}"))
      localArrayList.add(parseDeclaration());
    return localArrayList;
  }
  
  private Declaration parseDeclaration()
  {
    String str1 = token;
    eat(str1);
    String str2 = token;
    eat(str2);
    String str3;
    if ((token.equals(";")) || (token.equals("("))) {
      str3 = str2;
    }
    else
    {
      str3 = token;
      eat(str3);
    }
    
    if (token.equals("("))
    {

      if ((!str1.equals("public")) && (!str1.equals("private")))
        throw new JavaTownParseException("illegal start of declaration:  " + str1);
      eat("(");
      ArrayList localArrayList = new ArrayList();
      while (!token.equals(")"))
      {
        str2 = token;
        eat(str2);
        if ((token.equals(",")) || (token.equals(")"))) {
          localArrayList.add(str2);
        }
        else
        {
          localArrayList.add(token);
          eat(token);
        }
        
        if (token.equals(",")) {
          eat(",");
        } else if (!token.equals(")"))
          throw new JavaTownParseException("expected ) but found " + token);
      }
      eat(")");
      return new Method(str1.equals("public") ? 0 : 1, str3, localArrayList, parseBlock());
    }
    



    if (!str1.equals("private"))
      throw new JavaTownParseException("expected private but found " + token);
    eat(";");
    return new Field(str3);
  }
  


  private Block parseBlock()
  {
    eat("{");
    
    ArrayList localArrayList = new ArrayList();
    while (!token.equals("}"))
      localArrayList.add(parseStatement());
    eat("}");
    return new Block(localArrayList);
  }
  

  private Expression parseExpression()
  {
    Object localObject = parseAnded();
    while (token.equals("||"))
    {
      eat("||");
      localObject = new BinOp('|', (Expression)localObject, parseAnded());
    }
    
    return localObject;
  }
  

  private Expression parseAnded()
  {
    Object localObject = parsePossibleEquality();
    while (token.equals("&&"))
    {
      eat("&&");
      localObject = new BinOp('&', (Expression)localObject, parsePossibleEquality());
    }
    return localObject;
  }
  

  private Expression parsePossibleEquality()
  {
    Expression localExpression = parsePossibleComparison();
    if (token.equals("=="))
    {
      eat("==");
      return new BinOp('=', localExpression, parsePossibleComparison());
    }
    if (token.equals("!="))
    {
      eat("!=");
      return new BinOp('!', localExpression, parsePossibleComparison());
    }
    
    return localExpression;
  }
  

  private Expression parsePossibleComparison()
  {
    Expression localExpression = parsePossibleSum();
    if (token.equals("<"))
    {
      eat("<");
      return new BinOp('<', localExpression, parsePossibleSum());
    }
    if (token.equals(">"))
    {
      eat(">");
      return new BinOp('>', localExpression, parsePossibleSum());
    }
    if (token.equals("<="))
    {
      eat("<=");
      return new BinOp('[', localExpression, parsePossibleSum());
    }
    if (token.equals(">="))
    {
      eat(">=");
      return new BinOp(']', localExpression, parsePossibleSum());
    }
    
    return localExpression;
  }
  
  private ArrayList<Expression> parseArguments()
  {
    eat("(");
    ArrayList localArrayList = new ArrayList();
    while (!token.equals(")"))
    {
      localArrayList.add(parseExpression());
      if (token.equals(",")) {
        eat(",");
      } else if (!token.equals(")"))
        throw new JavaTownParseException("expected ) but found " + token);
    }
    eat(")");
    return localArrayList;
  }
  

  private Expression parsePossibleSum()
  {
    Object localObject = parseTerm();
    while ((token.equals("+")) || (token.equals("-")))
      if (token.equals("+"))
      {
        eat("+");
        localObject = new BinOp('+', (Expression)localObject, parseTerm());
      }
      else
      {
        eat("-");
        localObject = new BinOp('-', (Expression)localObject, parseTerm());
      }
    return localObject;
  }
  

  private Expression parseTerm()
  {
    Object localObject = parseFactor();
    while ((token.equals("*")) || (token.equals("/")) || (token.equals("%")))
      if (token.equals("*"))
      {
        eat("*");
        localObject = new BinOp('*', (Expression)localObject, parseFactor());
      }
      else if (token.equals("/"))
      {
        eat("/");
        localObject = new BinOp('/', (Expression)localObject, parseFactor());
      }
      else
      {
        eat("%");
        localObject = new BinOp('%', (Expression)localObject, parseFactor());
      }
    return localObject;
  }
  




  private Expression parseFactor()
  {
    if (token.equals("-"))
    {
      eat("-");
      return new BinOp('-', new IntExp(0), parseFactor());
    }
    if (token.equals("!"))
    {
      eat("!");
      return new Not(parseFactor());
    }
    if (token.equals("new"))
    {
      eat("new");
      String str = token;
      eat(str);
      return new Instantiation(str, parseArguments());
    }
    
    return parseAtom();
  }
  





  private Expression parseAtom()
  {
    if (token.equals("null"))
    {
      eat("null");
      return new Null();
    }
    if (token.equals("true"))
    {
      eat("true");
      return new BoolExp(true);
    }
    if (token.equals("false"))
    {
      eat("false");
      return new BoolExp(false);
    }
    if (token.startsWith("\""))
    {
      String str = token;
      eat(str);
      return new StringExp(str.substring(1, str.length() - 1));
    }
    

    try
    {
      int i = Integer.parseInt(token);
      eat(token);
      return new IntExp(i);
    }
    catch (NumberFormatException localNumberFormatException) {}
    
    return parseInvocation();
  }
  




  public Expression parseInvocation()
  {
    Object localObject = parseVariable();
    
    while (token.equals("."))
    {
      eat(".");
      String str = token;
      eat(str);
      ArrayList localArrayList = parseArguments();
      localObject = new Invocation((Expression)localObject, str, localArrayList);
    }
    
    return localObject;
  }
  



  public Expression parseVariable()
  {
    if (token.equals("("))
    {
      eat("(");
      localObject = parseExpression();
      eat(")");
      return localObject;
    }
    if (token.equals("this"))
    {
      eat("this");
      return new This();
    }
    

    Object localObject = token;
    eat((String)localObject);
    return new Variable((String)localObject);
  }
  


  public If parseIf()
  {
    eat("if");
    eat("(");
    Expression localExpression = parseExpression();
    eat(")");
    Statement localStatement = parseStatement();
    Object localObject;
    if (token.equals("else"))
    {
      eat("else");
      localObject = parseStatement();
    }
    else {
      localObject = new Block(new ArrayList()); }
    return new If(localExpression, localStatement, (Statement)localObject);
  }
}
