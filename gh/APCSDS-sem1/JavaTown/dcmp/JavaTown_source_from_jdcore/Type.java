import java.util.Iterator;

public class Type
{
  private String name;
  private java.util.List<Field> fields = new java.util.ArrayList();
  private java.util.Set<Method> methods = new java.util.HashSet();
  
  public Type(String paramString, java.util.List paramList)
  {
    name = paramString;
    Iterator localIterator = paramList.iterator();
    int i = 0;
    while (localIterator.hasNext())
    {
      Declaration localDeclaration = (Declaration)localIterator.next();
      if ((localDeclaration instanceof Field)) {
        fields.add((Field)localDeclaration);
      } else if ((localDeclaration instanceof Method))
      {
        Method localMethod = (Method)localDeclaration;
        if (localMethod.getName().equals(paramString))
          i = 1;
        methods.add(localMethod);
      }
      else {
        throw new JavaTownException("Invalid declaration:  " + localDeclaration);
      }
    }
    if (i == 0) {
      methods.add(new Method(0, paramString, new java.util.ArrayList(), new Block(new java.util.ArrayList())));
    }
  }
  
  public String getName() {
    return name;
  }
  
  public Iterator<Field> fields()
  {
    return fields.iterator();
  }
  
  public Method getMethod(String paramString, int paramInt)
  {
    Iterator localIterator = methods.iterator();
    while (localIterator.hasNext())
    {
      Method localMethod = (Method)localIterator.next();
      if ((localMethod.getName().equals(paramString)) && (localMethod.getParamCount() == paramInt)) {
        return localMethod;
      }
    }
    throw new JavaTownException("No method " + paramString + " with " + paramInt + " parameters");
  }
}
