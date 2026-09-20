import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D.Double;
import java.util.Iterator;
import java.util.LinkedList;

public class Person {

   private int top;
   private int left;
   private Environment env;
   private Location loc;
   private LinkedList stack = new LinkedList();
   private TextBox speech;
   private boolean isWaiting = false;
   private String type;


   public Person(String var1, Location var2, int var3, int var4) {
      this.loc = var2;
      this.left = var3;
      this.top = var4;
      this.type = var1;
      this.env = new Environment(var1, (Environment)null, this);
      Iterator var5 = World.getClass(var1).fields();

      while(var5.hasNext()) {
         Field var6 = (Field)var5.next();
         String var7 = var6.getName();
         this.env.define(var7, (Object)null);
      }

   }

   public Environment getEnvironment() {
      return this.env;
   }

   public synchronized void draw(Graphics2D var1) {
      TextBox var2 = new TextBox(1, Color.GREEN.darker().darker().darker(), this.left, this.top, 85, 60);
      var2.addText(Color.YELLOW, 0, 14, this.env.getName());

      Iterator var3;
      String var4;
      Object var5;
      for(var3 = this.env.variables(); var3.hasNext(); var2.addText(Color.WHITE, 0, 12, var4 + ":  " + var5)) {
         var4 = (String)var3.next();
         var5 = this.env.get(var4);
         if(var5 instanceof String) {
            var5 = "\"" + var5 + "\"";
         }
      }

      var2.draw(var1);
      var1.setPaint(Color.YELLOW);
      var1.fill(new Double((double)(this.left + 88), (double)(this.top + 10), 14.0D, 14.0D));
      var1.setPaint(Color.BLACK);
      var1.draw(new Double((double)(this.left + 88), (double)(this.top + 10), 14.0D, 14.0D));
      var1.fill(new java.awt.geom.Rectangle2D.Double((double)(this.left + 92), (double)(this.top + 16), 2.0D, 2.0D));
      var1.fill(new java.awt.geom.Rectangle2D.Double((double)(this.left + 97), (double)(this.top + 16), 2.0D, 2.0D));
      var1.draw(new java.awt.geom.Line2D.Double((double)(this.left + 93), (double)(this.top + 20), (double)(this.left + 97), (double)(this.top + 20)));
      var1.draw(new java.awt.geom.Line2D.Double((double)(this.left + 95), (double)(this.top + 24), (double)(this.left + 95), (double)(this.top + 50)));
      var1.draw(new java.awt.geom.Line2D.Double((double)(this.left + 95), (double)(this.top + 24), (double)(this.left + 88), (double)(this.top + 31)));
      var1.draw(new java.awt.geom.Line2D.Double((double)(this.left + 95), (double)(this.top + 24), (double)(this.left + 102), (double)(this.top + 31)));
      if(this.speech == null) {
         Iterator var10 = this.stack.iterator();

         for(int var11 = 0; var10.hasNext(); var11 += 5) {
            Environment var6 = (Environment)var10.next();
            TextBox var7 = new TextBox(0, Color.WHITE, this.left + 105 + var11, this.top - var11);
            var7.addText(Color.BLACK, 0, 14, var6.getName());

            String var8;
            Object var9;
            for(var3 = var6.variables(); var3.hasNext(); var7.addText(Color.BLACK, 0, 12, var8 + ":  " + var9)) {
               var8 = (String)var3.next();
               var9 = var6.get(var8);
               if(var9 instanceof String) {
                  var9 = "\"" + var9 + "\"";
               }
            }

            if(this.isWaiting || var10.hasNext()) {
               var7.addText(Color.BLACK, 0, 12, "...");
            }

            var7.draw(var1);
         }

         if(this.stack.size() > 0) {
            Double var12 = new Double((double)(this.left + 100), (double)(this.top + 5), 5.0D, 5.0D);
            var1.setPaint(Color.WHITE);
            var1.fill(var12);
            var1.setPaint(Color.BLACK);
            var1.draw(var12);
         }
      } else {
         this.speech.draw(var1);
         var1.setPaint(Color.BLACK);
         var1.draw(new java.awt.geom.Line2D.Double((double)(this.left + 100), (double)(this.top + 10), (double)(this.left + 105), (double)(this.top + 5)));
      }

   }

   public void setLeft(int var1) {
      this.left = var1;
   }

   public void setTop(int var1) {
      this.top = var1;
   }

   public void setLocation(Location var1) {
      this.loc = var1;
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

   public synchronized void pushFrame(Environment var1) {
      this.stack.addLast(var1);
   }

   public synchronized void popFrame() {
      this.stack.removeLast();
   }

   public void say(String var1) {
      this.speech = new TextBox(0, Color.WHITE, this.left + 105, this.top);
      this.speech.addText(Color.BLUE, 0, 14, var1);
   }

   public void sayNothing() {
      this.speech = null;
   }

   public void setWaiting(boolean var1) {
      this.isWaiting = var1;
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
