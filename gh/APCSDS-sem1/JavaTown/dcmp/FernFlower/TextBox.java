import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D.Double;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextBox {

   public static final int SMALL_FONT = 12;
   public static final int LARGE_FONT = 14;
   public static final int ROUND = 0;
   public static final int SQUARE = 1;
   private int shape;
   private Color backColor;
   private int left;
   private int top;
   private List lines;
   private int width;
   private int height;


   public TextBox(int var1, Color var2) {
      this(var1, var2, 0, 0);
   }

   public TextBox(int var1, Color var2, int var3, int var4) {
      this(var1, var2, var3, var4, -1, -1);
   }

   public TextBox(int var1, Color var2, int var3, int var4, int var5, int var6) {
      this.lines = new ArrayList();
      this.shape = var1;
      this.backColor = var2;
      this.left = var3;
      this.top = var4;
      this.width = var5;
      this.height = var6;
   }

   public void draw(Graphics2D var1) {
      Font var4 = var1.getFont();
      int var2;
      int var3;
      if(this.width == -1) {
         Iterator var5 = this.lines.iterator();
         var2 = 0;

         FontMetrics var8;
         for(var3 = 0; var5.hasNext(); var3 += var8.getAscent() + var8.getDescent() - 2) {
            TextLine var6 = (TextLine)var5.next();
            Font var7 = var4.deriveFont(var6.getStyle(), (float)var6.getSize());
            var1.setFont(var7);
            var8 = var1.getFontMetrics();
            int var9 = var8.stringWidth(var6.getText());
            if(var9 > var2) {
               var2 = var9;
            }
         }

         var3 += 4;
         var2 += 4;
      } else {
         var2 = this.width;
         var3 = this.height;
      }

      Object var12;
      if(this.shape == 0) {
         var12 = new Double((double)this.left, (double)this.top, (double)var2, (double)var3, 10.0D, 10.0D);
      } else {
         var12 = new java.awt.geom.Rectangle2D.Double((double)this.left, (double)this.top, (double)var2, (double)var3);
      }

      var1.setPaint(this.backColor);
      var1.fill((Shape)var12);
      var1.setPaint(Color.BLACK);
      var1.draw((Shape)var12);
      Iterator var13 = this.lines.iterator();

      FontMetrics var10;
      for(int var14 = this.top; var13.hasNext(); var14 += var10.getDescent() - 2) {
         TextLine var15 = (TextLine)var13.next();
         Font var16 = var4.deriveFont(var15.getStyle(), (float)var15.getSize());
         var1.setFont(var16);
         var10 = var1.getFontMetrics();
         var14 += var10.getAscent();
         String var11 = var15.getText();
         var1.setPaint(var15.getColor());
         var1.drawString(var11, this.left + 2, var14);
      }

      var1.setFont(var4);
   }

   public void addText(Color var1, int var2, int var3, String var4) {
      this.lines.add(new TextLine(var1, var2, var3, var4));
   }

   public void setPosition(int var1, int var2) {
      this.left = var1;
      this.top = var2;
   }
}
