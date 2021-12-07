import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.LinkedList;

// 
// Decompiled by Procyon v0.5.36
// 

public class Person
{
    private int top;
    private int left;
    private Environment env;
    private Location loc;
    private LinkedList<Environment> stack;
    private TextBox speech;
    private boolean isWaiting;
    private String type;
    
    public Person(final String type, final Location loc, final int left, final int top) {
        this.stack = new LinkedList<Environment>();
        this.isWaiting = false;
        this.loc = loc;
        this.left = left;
        this.top = top;
        this.type = type;
        this.env = new Environment(type, null, this);
        final Iterator<Field> fields = World.getClass(type).fields();
        while (fields.hasNext()) {
            this.env.define(fields.next().getName(), null);
        }
    }
    
    public Environment getEnvironment() {
        return this.env;
    }
    
    public synchronized void draw(final Graphics2D graphics2D) {
        final TextBox textBox = new TextBox(1, Color.GREEN.darker().darker().darker(), this.left, this.top, 85, 60);
        textBox.addText(Color.YELLOW, 0, 14, this.env.getName());
        final Iterator variables = this.env.variables();
        while (variables.hasNext()) {
            final String str = variables.next();
            Object o = this.env.get(str);
            if (o instanceof String) {
                o = "\"" + o + "\"";
            }
            textBox.addText(Color.WHITE, 0, 12, str + ":  " + o);
        }
        textBox.draw(graphics2D);
        graphics2D.setPaint(Color.YELLOW);
        graphics2D.fill(new Ellipse2D.Double(this.left + 88, this.top + 10, 14.0, 14.0));
        graphics2D.setPaint(Color.BLACK);
        graphics2D.draw(new Ellipse2D.Double(this.left + 88, this.top + 10, 14.0, 14.0));
        graphics2D.fill(new Rectangle2D.Double(this.left + 92, this.top + 16, 2.0, 2.0));
        graphics2D.fill(new Rectangle2D.Double(this.left + 97, this.top + 16, 2.0, 2.0));
        graphics2D.draw(new Line2D.Double(this.left + 93, this.top + 20, this.left + 97, this.top + 20));
        graphics2D.draw(new Line2D.Double(this.left + 95, this.top + 24, this.left + 95, this.top + 50));
        graphics2D.draw(new Line2D.Double(this.left + 95, this.top + 24, this.left + 88, this.top + 31));
        graphics2D.draw(new Line2D.Double(this.left + 95, this.top + 24, this.left + 102, this.top + 31));
        if (this.speech == null) {
            final Iterator<Environment> iterator = (Iterator<Environment>)this.stack.iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final Environment environment = iterator.next();
                final TextBox textBox2 = new TextBox(0, Color.WHITE, this.left + 105 + n, this.top - n);
                textBox2.addText(Color.BLACK, 0, 14, environment.getName());
                final Iterator variables2 = environment.variables();
                while (variables2.hasNext()) {
                    final String str2 = variables2.next();
                    Object o2 = environment.get(str2);
                    if (o2 instanceof String) {
                        o2 = "\"" + o2 + "\"";
                    }
                    textBox2.addText(Color.BLACK, 0, 12, str2 + ":  " + o2);
                }
                if (this.isWaiting || iterator.hasNext()) {
                    textBox2.addText(Color.BLACK, 0, 12, "...");
                }
                textBox2.draw(graphics2D);
                n += 5;
            }
            if (this.stack.size() > 0) {
                final Ellipse2D.Double double1 = new Ellipse2D.Double(this.left + 100, this.top + 5, 5.0, 5.0);
                graphics2D.setPaint(Color.WHITE);
                graphics2D.fill(double1);
                graphics2D.setPaint(Color.BLACK);
                graphics2D.draw(double1);
            }
        }
        else {
            this.speech.draw(graphics2D);
            graphics2D.setPaint(Color.BLACK);
            graphics2D.draw(new Line2D.Double(this.left + 100, this.top + 10, this.left + 105, this.top + 5));
        }
    }
    
    public void setLeft(final int left) {
        this.left = left;
    }
    
    public void setTop(final int top) {
        this.top = top;
    }
    
    public void setLocation(final Location loc) {
        this.loc = loc;
    }
    
    @Override
    public String toString() {
        return this.loc.toString();
    }
    
    public Iterator stack() {
        return this.stack.iterator();
    }
    
    public Location getLocation() {
        return this.loc;
    }
    
    public synchronized void pushFrame(final Environment e) {
        this.stack.addLast(e);
    }
    
    public synchronized void popFrame() {
        this.stack.removeLast();
    }
    
    public void say(final String s) {
        (this.speech = new TextBox(0, Color.WHITE, this.left + 105, this.top)).addText(Color.BLUE, 0, 14, s);
    }
    
    public void sayNothing() {
        this.speech = null;
    }
    
    public void setWaiting(final boolean isWaiting) {
        this.isWaiting = isWaiting;
    }
    
    public boolean isWaiting() {
        return this.isWaiting;
    }
    
    public void resetComputation() {
        this.stack = new LinkedList<Environment>();
        this.speech = null;
        this.isWaiting = false;
    }
    
    public String getType() {
        return this.type;
    }
}
