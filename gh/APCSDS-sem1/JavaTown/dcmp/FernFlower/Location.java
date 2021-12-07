
public class Location {

   private int row;
   private int col;
   private int address;


   public Location(int var1, int var2) {
      this.row = var1;
      this.col = var2;
      this.address = 2 * var1 + var2 + 1;
   }

   public int row() {
      return this.row;
   }

   public int col() {
      return this.col;
   }

   public String toString() {
      return "#" + this.address;
   }
}
