import java.io.Reader;

public class Scanner
{
  private Reader in;
  private char ch;
  private String recent;
  
  public Scanner(Reader paramReader)
  {
    in = paramReader;
    
    recent = "";
    
    advance();
  }
  
  private void advance()
  {
    try
    {
      int i = in.read();
      if (i == -1) {
        ch = '\000';
      } else
        ch = ((char)i);
      recent += ch;
      
      while ((recent.length() > 100) && (recent.indexOf("\n") != -1)) {
        recent = recent.substring(recent.indexOf("\n") + 1);
      }
    }
    catch (java.io.IOException localIOException) {
      throw new RuntimeException(localIOException);
    }
  }
  
  private void eat(char paramChar)
  {
    if (ch == paramChar) {
      advance();
    } else {
      throw new JavaTownParseException("expected " + paramChar + " but found " + ch);
    }
  }
  
  private boolean isLetter() {
    return ((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z')) || (ch == '_');
  }
  
  private boolean isDigit()
  {
    return (ch >= '0') && (ch <= '9');
  }
  
  public String next()
  {
    while ((ch == ' ') || (ch == '\r') || (ch == '\n') || (ch == '\t')) {
      eat(ch);
    }
    String str = "";
    
    if (ch == '/')
    {
      eat('/');
      if (ch == '/')
      {

        while ((ch != '\n') && (ch != 0))
          eat(ch);
        return next();
      }
      


      return "/";
    }
    
    if (isLetter())
    {
      while ((isLetter()) || (isDigit()))
      {
        str = str + ch;
        eat(ch);
      }
      return str;
    }
    if (isDigit())
    {
      while (isDigit())
      {
        str = str + ch;
        eat(ch);
      }
      return str;
    }
    if ("=!<>".indexOf(ch + "") != -1)
    {

      str = str + ch;
      eat(ch);
      if (ch == '=')
      {
        str = str + '=';
        eat('=');
      }
      return str;
    }
    if (ch == '|')
    {
      eat('|');
      eat('|');
      return "||";
    }
    if (ch == '&')
    {
      eat('&');
      eat('&');
      return "&&";
    }
    if (ch == 0)
    {
      eat('\000');
      return " ";
    }
    if ("{};.(),+-*/%".indexOf("" + ch) != -1)
    {
      str = str + ch;
      eat(ch);
      return str;
    }
    if (ch == '"')
    {
      str = str + '"';
      eat('"');
      while ((ch != '"') && (ch != 0))
      {
        str = str + ch;
        eat(ch);
      }
      
      if (ch == 0) {
        throw new JavaTownParseException("missing closing quotation mark on string");
      }
      str = str + '"';
      eat('"');
      return str;
    }
    
    throw new JavaTownParseException("invalid character " + ch);
  }
  
  public String getRecent()
  {
    return recent;
  }
}
