package graphMatching;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.HashSet;


public class Solution {
    public static HashMap<Long, Boolean> cache = new HashMap<>();

    public static int gcd(int a, int b) {
        return a == 0 ? b : gcd(b % a, a);
    }

    public static boolean cycles(int a, int b) {
        if (a == b) return false;

        int d = gcd(a, b);
        a /= d;
        b /= d;

        if ((a + b) % 2 == 1) return true;

        // make a always the larger
        if (b > a) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        long packed = a << 32 | b;
        Boolean get = cache.get(packed);
        if (get != null)
            return get;

        // if we encounter ourselves in recursion,
        // we cycle
        cache.put(packed, true);

        // math: num is lower bound of the number of times we apply
        //  a := a - b   AND  b := 2 * b
        // so that a and b become as close together as possible
        // without a going negative. In other words, `num` is the solution to
        //      a - (2**n - 1) * b == 2**n * b
        int num = (int) (Math.log((double) (a+b) / (double) (2*b)) / Math.log(2));
        num = Math.max(num, 1);
        int pow = (int) Math.pow(2, num);

        int na = a - (pow - 1) * b;
        int nb = pow * b;
        boolean ret = cycles(na, nb);

        cache.put(packed, ret);

        return ret;
    }

    public static int solution(int[] list) {
        boolean[][] mat = new boolean[list.length][list.length];
        LinkedList<Integer> avail = new LinkedList<>();
        int[] conOf = new int[list.length];
        int[] tmpCOf = new int[list.length];

        // compute the matrix.
        // this is pretty inefficient as each connection is computed twice
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list.length; j++) {
                cache.clear();
                if (i != j && cycles(list[i], list[j])) {
                    mat[i][j] = true;
                    if (!avail.contains(i)) avail.add(i);

                    tmpCOf[i]++;
                }
            }
        }

        // tmpCOf contains the number of edges a certain node has
        // conOf will be a weighted sum of the number of edges
        // a node has and the number of edges its neighbors have
        // Thus, when sorted, avail will prioritize nodes that have
        // neighbors with fewer edges if the nodes themselves have the same
        // number of edges
        for (int i = 0; i < list.length; i++) {

            int d2Cons = 0;
            for (int j = 0; j < list.length; j++)
                if (mat[i][j]) d2Cons += tmpCOf[j];

            conOf[i] = (tmpCOf[i] << 16) + d2Cons;
        }

        avail.sort(Comparator.comparingInt(o -> conOf[o]));

        HashSet<Integer> seenFirsts = new HashSet<>();

        int paired = 0;

        while (avail.size() > 1) {
            // match the node with the minimum number of connections
            // with the first node it can be matched with (which will
            // also have the minimum connections of the nodes the first
            // can be matched with)
            int first = avail.get(0);

            int second = -1;
            int secondInd = -1;
            for (int i = 1; i < avail.size(); i++) {
                int j = avail.get(i);

                if (mat[first][j]) {
                    second = j;
                    secondInd = i;
                    break;
                }
            }

            if (second == -1) {
                if (seenFirsts.contains(first))
                    // we've already examined this node before, so we know nothing will work
                    // If we keep going, we'll just be trying the same things over and over
                    break;

                // the node with the minimum cannot be matched with any other
                // node that is available.
                // move the node we tried to the end and see if any other nodes match
                avail.add(avail.remove(0));
            } else {
                avail.remove(secondInd);
                avail.remove(0);
                paired += 2;
            }

            seenFirsts.add(first);
        }

        return list.length - paired;
    }
}