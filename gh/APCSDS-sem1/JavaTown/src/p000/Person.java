package p000;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;

/* renamed from: Person */
public class Person {
    private Environment env;
    private boolean isWaiting = false;
    private int left;
    private Location loc;
    private TextBox speech;
    private LinkedList<Environment> stack = new LinkedList<>();
    private int top;
    private String type;

    public Person(String str, Location location, int i, int i2) {
        this.loc = location;
        this.left = i;
        this.top = i2;
        this.type = str;
        this.env = new Environment(str, (Environment) null, this);
        Iterator<Field> fields = World.getClass(str).fields();
        while (fields.hasNext()) {
            this.env.define(fields.next().getName(), (Object) null);
        }
    }

    public Environment getEnvironment() {
        return this.env;
    }

    public synchronized void draw(Graphics2D graphics2D) {
        TextBox textBox = new TextBox(1, Color.GREEN.darker().darker().darker(), this.left, this.top, 85, 60);
        textBox.addText(Color.YELLOW, 0, 14, this.env.getName());
        Iterator variables = this.env.variables();
        while (variables.hasNext()) {
            String str = (String) variables.next();
            Object obj = this.env.get(str);
            if (obj instanceof String) {
                obj = "\"" + obj + "\"";
            }
            textBox.addText(Color.WHITE, 0, 12, str + ":  " + obj);
        }
        textBox.draw(graphics2D);
        graphics2D.setPaint(Color.YELLOW);
        graphics2D.fill(new Ellipse2D.Double((double) (this.left + 88), (double) (this.top + 10), 14.0d, 14.0d));
        graphics2D.setPaint(Color.BLACK);
        graphics2D.draw(new Ellipse2D.Double((double) (this.left + 88), (double) (this.top + 10), 14.0d, 14.0d));
        graphics2D.fill(new Rectangle2D.Double((double) (this.left + 92), (double) (this.top + 16), 2.0d, 2.0d));
        graphics2D.fill(new Rectangle2D.Double((double) (this.left + 97), (double) (this.top + 16), 2.0d, 2.0d));
        graphics2D.draw(new Line2D.Double((double) (this.left + 93), (double) (this.top + 20), (double) (this.left + 97), (double) (this.top + 20)));
        graphics2D.draw(new Line2D.Double((double) (this.left + 95), (double) (this.top + 24), (double) (this.left + 95), (double) (this.top + 50)));
        graphics2D.draw(new Line2D.Double((double) (this.left + 95), (double) (this.top + 24), (double) (this.left + 88), (double) (this.top + 31)));
        graphics2D.draw(new Line2D.Double((double) (this.left + 95), (double) (this.top + 24), (double) (this.left + 102), (double) (this.top + 31)));
        if (this.speech == null) {
            Iterator it = this.stack.iterator();
            int i = 0;
            while (it.hasNext()) {
                Environment environment = (Environment) it.next();
                TextBox textBox2 = new TextBox(0, Color.WHITE, this.left + 105 + i, this.top - i);
                textBox2.addText(Color.BLACK, 0, 14, environment.getName());
                Iterator variables2 = environment.variables();
                while (variables2.hasNext()) {
                    String str2 = (String) variables2.next();
                    Object obj2 = environment.get(str2);
                    if (obj2 instanceof String) {
                        obj2 = "\"" + obj2 + "\"";
                    }
                    textBox2.addText(Color.BLACK, 0, 12, str2 + ":  " + obj2);
                }
                if (this.isWaiting || it.hasNext()) {
                    textBox2.addText(Color.BLACK, 0, 12, "...");
                }
                textBox2.draw(graphics2D);
                i += 5;
            }
            if (this.stack.size() > 0) {
                Ellipse2D.Double doubleR = new Ellipse2D.Double((double) (this.left + 100), (double) (this.top + 5), 5.0d, 5.0d);
                graphics2D.setPaint(Color.WHITE);
                graphics2D.fill(doubleR);
                graphics2D.setPaint(Color.BLACK);
                graphics2D.draw(doubleR);
            }
        } else {
            this.speech.draw(graphics2D);
            graphics2D.setPaint(Color.BLACK);
            graphics2D.draw(new Line2D.Double((double) (this.left + 100), (double) (this.top + 10), (double) (this.left + 105), (double) (this.top + 5)));
        }
    }

    public void setLeft(int i) {
        this.left = i;
    }

    public void setTop(int i) {
        this.top = i;
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

    public void say(String str) {
        this.speech = new TextBox(0, Color.WHITE, this.left + 105, this.top);
        this.speech.addText(Color.BLUE, 0, 14, str);
    }

    public void sayNothing() {
        this.speech = null;
    }

    public void setWaiting(boolean z) {
        this.isWaiting = z;
    }

    public boolean isWaiting() {
        return this.isWaiting;
    }

    public void resetComputation() {
        this.stack = new LinkedList<>();
        this.speech = null;
        this.isWaiting = false;
    }

    public String getType() {
        return this.type;
    }
}
