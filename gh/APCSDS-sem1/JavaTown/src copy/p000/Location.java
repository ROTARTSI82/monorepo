package p000;

/* renamed from: Location */
public class Location {
    private int address;
    private int col;
    private int row;

    public Location(int i, int i2) {
        this.row = i;
        this.col = i2;
        this.address = (i * 2) + i2 + 1;
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
