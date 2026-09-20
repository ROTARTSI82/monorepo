import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Iterator;

public class TextBox
{
  public static final int SMALL_FONT = 12;
  public static final int LARGE_FONT = 14;
  public static final int ROUND = 0;
  public static final int SQUARE = 1;
  private int shape;
  private Color backColor;
  private int left;
  private int top;
  private java.util.List<TextLine> lines = new java.util.ArrayList();
  private int width;
  private int height;
  
  public TextBox(int paramInt, Color paramColor)
  {
    this(paramInt, paramColor, 0, 0);
  }
  
  public TextBox(int paramInt1, Color paramColor, int paramInt2, int paramInt3)
  {
    this(paramInt1, paramColor, paramInt2, paramInt3, -1, -1);
  }
  
  public TextBox(int paramInt1, Color paramColor, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    shape = paramInt1;
    backColor = paramColor;
    left = paramInt2;
    top = paramInt3;
    width = paramInt4;
    height = paramInt5;
  }
  


  public void draw(Graphics2D paramGraphics2D)
  {
    Font localFont1 = paramGraphics2D.getFont();
    Object localObject1; int i; int j; Object localObject3; if (width == -1)
    {
      localObject1 = lines.iterator();
      i = 0;
      j = 0;
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (TextLine)((Iterator)localObject1).next();
        Font localFont2 = localFont1.deriveFont(((TextLine)localObject2).getStyle(), ((TextLine)localObject2).getSize());
        paramGraphics2D.setFont(localFont2);
        localObject3 = paramGraphics2D.getFontMetrics();
        int m = ((FontMetrics)localObject3).stringWidth(((TextLine)localObject2).getText());
        if (m > i)
          i = m;
        j += ((FontMetrics)localObject3).getAscent() + ((FontMetrics)localObject3).getDescent() - 2;
      }
      
      j += 4;
      i += 4;
    }
    else
    {
      i = width;
      j = height;
    }
    

    if (shape == 0) {
      localObject1 = new java.awt.geom.RoundRectangle2D.Double(left, top, i, j, 10.0D, 10.0D);
    } else
      localObject1 = new java.awt.geom.Rectangle2D.Double(left, top, i, j);
    paramGraphics2D.setPaint(backColor);
    paramGraphics2D.fill((java.awt.Shape)localObject1);
    paramGraphics2D.setPaint(Color.BLACK);
    paramGraphics2D.draw((java.awt.Shape)localObject1);
    Object localObject2 = lines.iterator();
    int k = top;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (TextLine)((Iterator)localObject2).next();
      Font localFont3 = localFont1.deriveFont(((TextLine)localObject3).getStyle(), ((TextLine)localObject3).getSize());
      paramGraphics2D.setFont(localFont3);
      FontMetrics localFontMetrics = paramGraphics2D.getFontMetrics();
      k += localFontMetrics.getAscent();
      String str = ((TextLine)localObject3).getText();
      paramGraphics2D.setPaint(((TextLine)localObject3).getColor());
      paramGraphics2D.drawString(str, left + 2, k);
      k += localFontMetrics.getDescent() - 2;
    }
    
    paramGraphics2D.setFont(localFont1);
  }
  
  public void addText(Color paramColor, int paramInt1, int paramInt2, String paramString)
  {
    lines.add(new TextLine(paramColor, paramInt1, paramInt2, paramString));
  }
  
  public void setPosition(int paramInt1, int paramInt2)
  {
    left = paramInt1;
    top = paramInt2;
  }
}
