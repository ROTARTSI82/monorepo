package nebula;

import java.util.Arrays;
import java.util.HashMap;

public class Solution {

    private final boolean[][] expectedOut;
    private final long[] current;
    private final HashMap<Long, Integer> cache = new HashMap<>();
    private final int nCols;
    private final int maxInd;

    public int numIters = 0; // DBG

    public Solution(boolean[][] g) {
        this.expectedOut = g;

        nCols = g[0].length + 1;

        maxInd = nCols * (g.length + 1);

        current = new long[g.length + 1];
        Arrays.fill(current, 0);
    }

    public int solve(int ind) {
        numIters++; // DBG

        if (ind >= maxInd)
            return 1;

        int r = ind / nCols;
        int c = ind % nCols;

        long dreamMask = 0xFFFFFFFFFFFFFFFFL; // 64 1s in binary
        dreamMask <<= c; // relevant bits in the row above the current row
        long key = current[r] & ~dreamMask; // this is what the point of damascus
        if (r > 0) {
            key |= current[r - 1] & dreamMask;
            if (c > 0) {
                key |= (current[r - 1] & (1L << (c - 1))) << (52 - c);
            }
        }

        key |= ((long) ind) << 53;

        Integer cHit = cache.get(key);
        if (cHit != null) return cHit;

        int ret = 0;
        current[r] |= 1L << c;
        for (long i = 0; i < 2; i++) {
            if (r > 0 && c > 0) {
                long ident = (current[r-1] >> (c-1)) & 0b11;
                ident |= ((current[r] >> (c-1)) & 0b11) << 2;

                boolean gotOut = (ident & (ident - 1)) == 0 && ident != 0; // only 1 bit is set
                if (expectedOut[r-1][c-1] == gotOut) {
                    // this combo works! continue searching...
                    ret += solve(ind + 1);
                }
            } else {
                ret += solve(ind + 1);
            }

            current[r] &= ~(1L << c); // set to false for 2nd iteration
        }

        cache.put(key, ret);

        return ret;
    }

    public static int solution(boolean[][] g) {

        if (g[0].length > g.length) {
            // transpose g so that left to right is shorter.
            // this is so that solve() recognizes when something doesn't work
            // quicker, as once solve() selects a value for a cell it has to
            // loop through the entire row to get to the next row to see if the values
            // it selected worked

            boolean[][] tmp = g;
            g = new boolean[tmp[0].length][tmp.length];
            for (int r = 0; r < g.length; r++)
                for (int c = 0; c < g[0].length; c++)
                    g[r][c] = tmp[c][r];
        }

        return new Solution(g).solve(0);
    }
}

