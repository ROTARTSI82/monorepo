package graphPair;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class Solution {
    private static HashMap<Long, Boolean> cache = new HashMap<>();
    
    private boolean mat[][];
    
    public static int gcd(int a, int b) {
        return a == 0 ? b : gcd(b % a, a);
    }
    
    static int count = 0;
    
    public static boolean cycles(int a, int b) {
        // System.out.println(++count);
        if (a == b) return false;
        
        // int d = gcd(a, b);
        // a /= d;
        // b /= d;
        
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
        int num = (int) (Math.log((double) (a+b) / (double) (2*b)) / Math.log(2));
        num = Math.max(num, 1);
        int pow = (int) Math.pow(2, num);
        
        int na = a - (pow - 1) * b;
        int nb = pow * b;
        // System.out.printf("(%d, %d) -> (%d, %d)\n", a, b, na, nb);
        boolean ret = cycles(na, nb);
        
        // boolean ret = cycles(a - b, 2 * b);
        cache.put(packed, ret);
        
        return ret;
    }
    
    public Solution(boolean[][] mat) {
        this.mat = mat;
    }
    
    public int solve(boolean[] avail) {
        for (int i = 0; i < avail.length; i++) {
            
        }
        
        return 0;
    }
    
    public static int solution(int[] list) {
        boolean mat[][] = new boolean[list.length][list.length];
        ArrayList<Integer> avail = new ArrayList<>();
        int conOf[] = new int[list.length];
        
        int d = list.length;
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list.length; j++) {
                if (i != j && cycles(list[i], list[j])) {
                    mat[i][j] = true;
                    if (!avail.contains(i)) avail.add(i);
                    
                    conOf[i]++;
                }
            }
        }
        
        Collections.sort(avail, new Comparator<Integer>(){
            public int compare(Integer o1, Integer o2){
                return Integer.valueOf(conOf[o1]).compareTo(conOf[o2]);
            }
        });
        
        // IDEA:
        // Use avail instead as a queue,
        // with the elements with the least
        // connections appearing first.
        // then, we just pair the front of the queue
        // with the closest eligible element
        
        HashSet<Integer> seenFirsts = new HashSet<>();
        
        int paired = 0;
        
        while (avail.size() > 1) {
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
                    break;
                avail.add(avail.remove(0));
            } else {
                avail.remove(secondInd);
                avail.remove(0);
                paired += 2;
            }
            
            seenFirsts.add(first);
        }
        
        
        /* while (avail.size() > 1) {
            int min = -1;
            int mv = 999999;
            
            for (int i : avail)
                if (conOf[i] < mv) { min = i; mv = conOf[i]; }
            
            int min2 = -1;
            mv = 9999999;
            for (int i = 0; i < list.length; i++) {
                if (i == min || !mat[min][i] || !avail.contains(i)) continue;
                
                if (conOf[i] < mv) { min2 = i; mv = conOf[i]; }
            }
            
            avail.remove(avail.indexOf(min));
            // Remove the min and keep going
            // IDEA: Instead, move it to the end of the queue
            if (min2 == -1) continue;
            
            avail.remove(avail.indexOf(min2));
            
            paired += 2;
        } */
        
        return list.length - paired;
    }
}
