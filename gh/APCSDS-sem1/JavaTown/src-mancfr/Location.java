/*
 * Decompiled with CFR 0.151.
 */
public class Location {
    private int row;
    private int col;
    private int address;

    public Location(int n, int n2) {
        this.row = n;
        this.col = n2;
        this.address = 2 * n + n2 + 1;
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

