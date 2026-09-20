// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Person.java

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.util.Iterator;
import java.util.LinkedList;

public class Person
{

    public Person(String s, Location location, int i, int j)
    {
        stack = new LinkedList();
        isWaiting = false;
        loc = location;
        left = i;
        top = j;
        type = s;
        env = new Environment(s, null, this);
        String s1;
        for(Iterator iterator = World.getClass(s).fields(); iterator.hasNext(); env.define(s1, null))
        {
            Field field = (Field)iterator.next();
            s1 = field.getName();
        }

    }

    public Environment getEnvironment()
    {
        return env;
    }

    public synchronized void draw(Graphics2D graphics2d)
    {
        TextBox textbox = new TextBox(1, Color.GREEN.darker().darker().darker(), left, top, 85, 60);
        textbox.addText(Color.YELLOW, 0, 14, env.getName());
        String s;
        Object obj;
        for(Iterator iterator = env.variables(); iterator.hasNext(); textbox.addText(Color.WHITE, 0, 12, (new StringBuilder()).append(s).append(":  ").append(obj).toString()))
        {
            s = (String)iterator.next();
            obj = env.get(s);
            if(obj instanceof String)
                obj = (new StringBuilder()).append("\"").append(obj).append("\"").toString();
        }

        textbox.draw(graphics2d);
        graphics2d.setPaint(Color.YELLOW);
        graphics2d.fill(new java.awt.geom.Ellipse2D.Double(left + 88, top + 10, 14D, 14D));
        graphics2d.setPaint(Color.BLACK);
        graphics2d.draw(new java.awt.geom.Ellipse2D.Double(left + 88, top + 10, 14D, 14D));
        graphics2d.fill(new java.awt.geom.Rectangle2D.Double(left + 92, top + 16, 2D, 2D));
        graphics2d.fill(new java.awt.geom.Rectangle2D.Double(left + 97, top + 16, 2D, 2D));
        graphics2d.draw(new java.awt.geom.Line2D.Double(left + 93, top + 20, left + 97, top + 20));
        graphics2d.draw(new java.awt.geom.Line2D.Double(left + 95, top + 24, left + 95, top + 50));
        graphics2d.draw(new java.awt.geom.Line2D.Double(left + 95, top + 24, left + 88, top + 31));
        graphics2d.draw(new java.awt.geom.Line2D.Double(left + 95, top + 24, left + 102, top + 31));
        if(speech == null)
        {
            Iterator iterator2 = stack.iterator();
            for(int i = 0; iterator2.hasNext(); i += 5)
            {
                Environment environment = (Environment)iterator2.next();
                TextBox textbox1 = new TextBox(0, Color.WHITE, left + 105 + i, top - i);
                textbox1.addText(Color.BLACK, 0, 14, environment.getName());
                String s1;
                Object obj1;
                for(Iterator iterator1 = environment.variables(); iterator1.hasNext(); textbox1.addText(Color.BLACK, 0, 12, (new StringBuilder()).append(s1).append(":  ").append(obj1).toString()))
                {
                    s1 = (String)iterator1.next();
                    obj1 = environment.get(s1);
                    if(obj1 instanceof String)
                        obj1 = (new StringBuilder()).append("\"").append(obj1).append("\"").toString();
                }

                if(isWaiting || iterator2.hasNext())
                    textbox1.addText(Color.BLACK, 0, 12, "...");
                textbox1.draw(graphics2d);
            }

            if(stack.size() > 0)
            {
                java.awt.geom.Ellipse2D.Double double1 = new java.awt.geom.Ellipse2D.Double(left + 100, top + 5, 5D, 5D);
                graphics2d.setPaint(Color.WHITE);
                graphics2d.fill(double1);
                graphics2d.setPaint(Color.BLACK);
                graphics2d.draw(double1);
            }
        } else
        {
            speech.draw(graphics2d);
            graphics2d.setPaint(Color.BLACK);
            graphics2d.draw(new java.awt.geom.Line2D.Double(left + 100, top + 10, left + 105, top + 5));
        }
    }

    public void setLeft(int i)
    {
        left = i;
    }

    public void setTop(int i)
    {
        top = i;
    }

    public void setLocation(Location location)
    {
        loc = location;
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

    public synchronized void pushFrame(Environment environment)
    {
        stack.addLast(environment);
    }

    public synchronized void popFrame()
    {
        stack.removeLast();
    }

    public void say(String s)
    {
        speech = new TextBox(0, Color.WHITE, left + 105, top);
        speech.addText(Color.BLUE, 0, 14, s);
    }

    public void sayNothing()
    {
        speech = null;
    }

    public void setWaiting(boolean flag)
    {
        isWaiting = flag;
    }

    public boolean isWaiting()
    {
        return isWaiting;
    }

    public void resetComputation()
    {
        stack = new LinkedList();
        speech = null;
        isWaiting = false;
    }

    public String getType()
    {
        return type;
    }

    private int top;
    private int left;
    private Environment env;
    private Location loc;
    private LinkedList stack;
    private TextBox speech;
    private boolean isWaiting;
    private String type;
}
