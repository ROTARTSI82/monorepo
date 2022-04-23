package nebula;

import java.util.Arrays;
import java.util.HashMap;

public class Solution {

    private static class RowCache {
        public HashMap<Long, Integer> cache = new HashMap<>();
    }

    private final boolean[][] expectedOut;
    private final long[] current;
    private final RowCache[] cache;
    private final int nCols;
    private final int maxInd;

    public int numIters = 0;

    public Solution(boolean[][] g) {
        this.expectedOut = g;

        nCols = g[0].length + 1;

        maxInd = nCols * (g.length + 1);

        current = new long[g.length + 1];
        Arrays.fill(current, 0);

        cache = new RowCache[g.length + 1];
        for (int i = 0; i < g.length + 1; i++)
            cache[i] = new RowCache();
    }

    public int solve(int ind) {
        numIters++;

        if (ind == maxInd)
            return 1;

        int r = ind / nCols;
        int c = ind % nCols;

        long dreamMask = 0xFFFFFFFFFFFFFFFFL; // 64 1s in binary
        dreamMask <<= c; // relevant bits in the row above the current row
        long key = current[r] & ~dreamMask; // this is what the point of damascus
        if (r > 0) {
            key |= current[r - 1] & dreamMask;
            key |= (current[r - 1] & (1L << (c-1))) << (52-c);
        }

        key |= ((long) ind) << 53;

        Integer cHit = cache[r].cache.get(key);
        if (cHit != null) return cHit;

        int ret = 0;
        current[r] &= ~(1L << c);
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

            current[r] |= 1L << c; // set to true for 2nd iteration
        }

        cache[r].cache.put(key, ret);

        return ret;
    }

    public static int solution(boolean[][] g) {
        Solution sol = new Solution(g);
        int ret = sol.solve(0);
//        System.out.println("Solved in " + sol.numIters + " its");
        return ret;
    }
}

