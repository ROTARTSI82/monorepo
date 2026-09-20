import java.awt.Color;

public class TextLine {

   private Color color;
   private int style;
   private int size;
   private String text;


   public TextLine(Color var1, int var2, int var3, String var4) {
      this.color = var1;
      this.style = var2;
      this.size = var3;
      this.text = var4;
   }

   public Color getColor() {
      return this.color;
   }

   public int getStyle() {
      return this.style;
   }

   public int getSize() {
      return this.size;
   }

   public String getText() {
      return this.text;
   }
}
