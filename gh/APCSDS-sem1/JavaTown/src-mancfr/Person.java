/*
 * Decompiled with CFR 0.151.
 */
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;

public class Person {
    private int top;
    private int left;
    private Environment env;
    private Location loc;
    private LinkedList<Environment> stack = new LinkedList();
    private TextBox speech;
    private boolean isWaiting = false;
    private String type;

    public Person(String string, Location location, int n, int n2) {
        this.loc = location;
        this.left = n;
        this.top = n2;
        this.type = string;
        this.env = new Environment(string, null, this);
        Iterator<Field> iterator = World.getClass(string).fields();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            String string2 = field.getName();
            this.env.define(string2, null);
        }
    }

    public Environment getEnvironment() {
        return this.env;
    }

    public synchronized void draw(Graphics2D graphics2D) {
        Object object;
        TextBox textBox = new TextBox(1, Color.GREEN.darker().darker().darker(), this.left, this.top, 85, 60);
        textBox.addText(Color.YELLOW, 0, 14, this.env.getName());
        Iterator iterator = this.env.variables();
        while (iterator.hasNext()) {
            object = (String)iterator.next();
            Object object2 = this.env.get((String)object);
            if (object2 instanceof String) {
                object2 = "\"" + object2 + "\"";
            }
            textBox.addText(Color.WHITE, 0, 12, (String)object + ":  " + object2);
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
            Object object3;
            object = this.stack.iterator();
            int n = 0;
            while (((Iterator<Environment>) object).hasNext()) { // modified
                object3 = (Environment)((Iterator<Environment>) object).next(); // modified
                TextBox textBox2 = new TextBox(0, Color.WHITE, this.left + 105 + n, this.top - n);
                textBox2.addText(Color.BLACK, 0, 14, ((Environment)object3).getName());
                iterator = ((Environment)object3).variables();
                while (iterator.hasNext()) {
                    String string = (String)iterator.next();
                    Object object4 = ((Environment)object3).get(string);
                    if (object4 instanceof String) {
                        object4 = "\"" + object4 + "\"";
                    }
                    textBox2.addText(Color.BLACK, 0, 12, string + ":  " + object4);
                }
                if (this.isWaiting || ((Iterator<Environment>) object).hasNext()) { // modified
                    textBox2.addText(Color.BLACK, 0, 12, "...");
                }
                textBox2.draw(graphics2D);
                n += 5;
            }
            if (this.stack.size() > 0) {
                object3 = new Ellipse2D.Double(this.left + 100, this.top + 5, 5.0, 5.0);
                graphics2D.setPaint(Color.WHITE);
                graphics2D.fill((Shape)object3);
                graphics2D.setPaint(Color.BLACK);
                graphics2D.draw((Shape)object3);
            }
        } else {
            this.speech.draw(graphics2D);
            graphics2D.setPaint(Color.BLACK);
            graphics2D.draw(new Line2D.Double(this.left + 100, this.top + 10, this.left + 105, this.top + 5));
        }
    }

    public void setLeft(int n) {
        this.left = n;
    }

    public void setTop(int n) {
        this.top = n;
    }

    public void setLocation(Location location) {
        this.loc = location;
    }

    public String toString() {
        return this.loc.toString();
    }

    public Iterator stack() {
        return this.stack.iterator();
    }

    public Location getLocation() {
        return this.loc;
    }

    public synchronized void pushFrame(Environment environment) {
        this.stack.addLast(environment);
    }

    public synchronized void popFrame() {
        this.stack.removeLast();
    }

    public void say(String string) {
        this.speech = new TextBox(0, Color.WHITE, this.left + 105, this.top);
        this.speech.addText(Color.BLUE, 0, 14, string);
    }

    public void sayNothing() {
        this.speech = null;
    }

    public void setWaiting(boolean bl) {
        this.isWaiting = bl;
    }

    public boolean isWaiting() {
        return this.isWaiting;
    }

    public void resetComputation() {
        this.stack = new LinkedList();
        this.speech = null;
        this.isWaiting = false;
    }

    public String getType() {
        return this.type;
    }
}

