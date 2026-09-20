import java.awt.Color;

public class TextLine
{
  private Color color;
  private int style;
  private int size;
  private String text;
  
  public TextLine(Color paramColor, int paramInt1, int paramInt2, String paramString)
  {
    color = paramColor;
    style = paramInt1;
    size = paramInt2;
    text = paramString;
  }
  
  public Color getColor()
  {
    return color;
  }
  
  public int getStyle()
  {
    return style;
  }
  
  public int getSize()
  {
    return size;
  }
  
  public String getText()
  {
    return text;
  }
}
