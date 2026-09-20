import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;

public class Person
{
  private int top;
  private int left;
  private Environment env;
  private Location loc;
  private java.util.LinkedList<Environment> stack = new java.util.LinkedList();
  private TextBox speech;
  private boolean isWaiting = false;
  private String type;
  
  public Person(String paramString, Location paramLocation, int paramInt1, int paramInt2)
  {
    loc = paramLocation;
    left = paramInt1;
    top = paramInt2;
    type = paramString;
    
    env = new Environment(paramString, null, this);
    
    Iterator localIterator = World.getClass(paramString).fields();
    while (localIterator.hasNext())
    {
      Field localField = (Field)localIterator.next();
      String str = localField.getName();
      env.define(str, null);
    }
  }
  
  public Environment getEnvironment()
  {
    return env;
  }
  

  public synchronized void draw(Graphics2D paramGraphics2D)
  {
    TextBox localTextBox1 = new TextBox(1, Color.GREEN.darker().darker().darker(), left, top, 85, 60);
    
    localTextBox1.addText(Color.YELLOW, 0, 14, env.getName());
    Iterator localIterator = env.variables();
    Object localObject1; while (localIterator.hasNext())
    {
      localObject1 = (String)localIterator.next();
      Object localObject2 = env.get((String)localObject1);
      if ((localObject2 instanceof String))
        localObject2 = "\"" + localObject2 + "\"";
      localTextBox1.addText(Color.WHITE, 0, 12, (String)localObject1 + ":  " + localObject2);
    }
    
    localTextBox1.draw(paramGraphics2D);
    

    paramGraphics2D.setPaint(Color.YELLOW);
    paramGraphics2D.fill(new java.awt.geom.Ellipse2D.Double(left + 88, top + 10, 14.0D, 14.0D));
    paramGraphics2D.setPaint(Color.BLACK);
    paramGraphics2D.draw(new java.awt.geom.Ellipse2D.Double(left + 88, top + 10, 14.0D, 14.0D));
    paramGraphics2D.fill(new java.awt.geom.Rectangle2D.Double(left + 92, top + 16, 2.0D, 2.0D));
    paramGraphics2D.fill(new java.awt.geom.Rectangle2D.Double(left + 97, top + 16, 2.0D, 2.0D));
    paramGraphics2D.draw(new java.awt.geom.Line2D.Double(left + 93, top + 20, left + 97, top + 20));
    paramGraphics2D.draw(new java.awt.geom.Line2D.Double(left + 95, top + 24, left + 95, top + 50));
    paramGraphics2D.draw(new java.awt.geom.Line2D.Double(left + 95, top + 24, left + 88, top + 31));
    paramGraphics2D.draw(new java.awt.geom.Line2D.Double(left + 95, top + 24, left + 102, top + 31));
    

    if (speech == null)
    {
      localObject1 = stack.iterator();
      int i = 0;
      Object localObject3; while (((Iterator)localObject1).hasNext())
      {
        localObject3 = (Environment)((Iterator)localObject1).next();
        TextBox localTextBox2 = new TextBox(0, Color.WHITE, left + 105 + i, top - i);
        localTextBox2.addText(Color.BLACK, 0, 14, ((Environment)localObject3).getName());
        localIterator = ((Environment)localObject3).variables();
        while (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          Object localObject4 = ((Environment)localObject3).get(str);
          if ((localObject4 instanceof String))
            localObject4 = "\"" + localObject4 + "\"";
          localTextBox2.addText(Color.BLACK, 0, 12, str + ":  " + localObject4);
        }
        
        if ((isWaiting) || (((Iterator)localObject1).hasNext())) {
          localTextBox2.addText(Color.BLACK, 0, 12, "...");
        }
        localTextBox2.draw(paramGraphics2D);
        i += 5;
      }
      
      if (stack.size() > 0)
      {
        localObject3 = new java.awt.geom.Ellipse2D.Double(left + 100, top + 5, 5.0D, 5.0D);
        paramGraphics2D.setPaint(Color.WHITE);
        paramGraphics2D.fill((java.awt.Shape)localObject3);
        paramGraphics2D.setPaint(Color.BLACK);
        paramGraphics2D.draw((java.awt.Shape)localObject3);
      }
    }
    else
    {
      speech.draw(paramGraphics2D);
      paramGraphics2D.setPaint(Color.BLACK);
      paramGraphics2D.draw(new java.awt.geom.Line2D.Double(left + 100, top + 10, left + 105, top + 5));
    }
  }
  
  public void setLeft(int paramInt)
  {
    left = paramInt;
  }
  
  public void setTop(int paramInt)
  {
    top = paramInt;
  }
  
  public void setLocation(Location paramLocation)
  {
    loc = paramLocation;
  }
  
  public String toString()
  {
    return loc.toString();
  }
  
  public Iterator stack()
  {
    return stack.iterator();
  }
  
  public Location getLocation()
  {
    return loc;
  }
  
  public synchronized void pushFrame(Environment paramEnvironment)
  {
    stack.addLast(paramEnvironment);
  }
  
  public synchronized void popFrame()
  {
    stack.removeLast();
  }
  
  public void say(String paramString)
  {
    speech = new TextBox(0, Color.WHITE, left + 105, top);
    speech.addText(Color.BLUE, 0, 14, paramString);
  }
  
  public void sayNothing()
  {
    speech = null;
  }
  
  public void setWaiting(boolean paramBoolean)
  {
    isWaiting = paramBoolean;
  }
  
  public boolean isWaiting()
  {
    return isWaiting;
  }
  
  public void resetComputation()
  {
    stack = new java.util.LinkedList();
    speech = null;
    isWaiting = false;
  }
  
  public String getType()
  {
    return type;
  }
}
