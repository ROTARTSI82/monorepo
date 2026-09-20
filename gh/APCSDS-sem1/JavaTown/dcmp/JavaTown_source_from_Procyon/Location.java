// 
// Decompiled by Procyon v0.5.36
// 

public class Location
{
    private int row;
    private int col;
    private int address;
    
    public Location(final int row, final int col) {
        this.row = row;
        this.col = col;
        this.address = 2 * row + col + 1;
    }
    
    public int row() {
        return this.row;
    }
    
    public int col() {
        return this.col;
    }
    
    @Override
    public String toString() {
        return "#" + this.address;
    }
}
