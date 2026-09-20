public class Location
{
  private int row;
  private int col;
  private int address;
  
  public Location(int paramInt1, int paramInt2)
  {
    row = paramInt1;
    col = paramInt2;
    address = (2 * paramInt1 + paramInt2 + 1);
  }
  
  public int row()
  {
    return row;
  }
  
  public int col()
  {
    return col;
  }
  
  public String toString()
  {
    return "#" + address;
  }
}
