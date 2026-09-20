import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Environment
{
  private List<String> vars = new java.util.ArrayList();
  private Map<String, Object> values = new HashMap();
  private Environment parent;
  private String name;
  private Person owner;
  
  public Environment(String paramString, Environment paramEnvironment, Person paramPerson)
  {
    name = paramString;
    parent = paramEnvironment;
    owner = paramPerson;
  }
  
  public void set(String paramString, Object paramObject)
  {
    if (values.containsKey(paramString)) {
      values.put(paramString, paramObject);
    } else if ((parent != null) && (parent.containsVar(paramString))) {
      parent.set(paramString, paramObject);
    }
    else {
      vars.add(paramString);
      values.put(paramString, paramObject);
    }
  }
  
  public void define(String paramString, Object paramObject)
  {
    vars.add(paramString);
    values.put(paramString, paramObject);
  }
  
  public java.util.Iterator variables()
  {
    return vars.iterator();
  }
  
  public Object get(String paramString)
  {
    if (containsVar(paramString))
      return values.get(paramString);
    if (parent == null) {
      throw new JavaTownException("Unbound variable:  " + paramString);
    }
    return parent.get(paramString);
  }
  
  public boolean containsVar(String paramString)
  {
    return values.containsKey(paramString);
  }
  
  public String getName()
  {
    return name;
  }
  
  public Person getOwner()
  {
    return owner;
  }
  

  public void clear()
  {
    vars = new java.util.ArrayList();
    values = new HashMap();
  }
}
