package mirror;

import java.util.ArrayList;
import java.util.HashSet;

public class Solution
{
    private HashSet<Pair> angles = new HashSet<>();
    private HashSet<Pair> antiAngles = new HashSet<>();
    private ArrayList<Pair> toConsider = new ArrayList<>();

    private int dimX, dimY, youX, youY, targX, targY, maxDist;

    private int ouX, ouY, trX, trY;

    private static class Pair
    {
        int x, y;
        public Pair(int x, int y)
        {
            this.x = x; this.y = y;
        }

        @Override
        public int hashCode()
        {
            return x ^ (0x1400e8ca + y);
        }

        @Override
        public boolean equals(Object rhs)
        {
            if (rhs instanceof Pair)
            {
                Pair r = (Pair) rhs;
                return r.x == x && r.y == y;
            }
            return false;
        }
    }

    private Solution(int[] dim, int[] you, int[] targ, int max)
    {
        maxDist = max;
        dimX = dim[0]; dimY = dim[1];
        targX = targ[0]; targY = targ[1];
        youX = you[0]; youY = you[1];
    }

    private int gcd(int a, int b)
    {
        while (b != 0)
        {
            int tmp = b;
            b = a % b;
            a = tmp;
        }

        return Math.max(a, 1);
    }

    private Pair fracReduce(int x, int y)
    {
        int d = gcd(Math.abs(x), Math.abs(y));
        return new Pair(x / d, y / d);
    }

    private void calcPos(int cx, int cy)
    {
        if (cx % 2 == 0)
        {
            ouX = dimX * cx;
            trX = dimX * cx + trX - youX;
        }
        else
        {
            ouX = dimX * (cx+1) - 2*youX;
            trX = dimX * (cx+1) - trX - youX;
        }

        if (cy % 2 == 0)
        {
            ouY = dimX * cy;
            trY = dimY;
        }
    }

    private int solve()
    {
        return 0;
    }

    public static int solution(int[] dimensions, int[] your_position, int[] trainer_position, int distance)
    {
        return new Solution(dimensions, your_position, trainer_position, distance).solve();
    }
}