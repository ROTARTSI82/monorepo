package triple;

public class Solution {
    
    private static int nDivide(int[] l, int ind) {
        int n = 0;
        for (int i = ind + 1; i < l.length; i++)
            if (l[i] % l[ind] == 0) n++;
        return n;
    }
    
    public static int solution(int[] l) {
        // Your code here
        int n = 0;
        for (int i = 0; i < l.length; i++) {
            for (int c = i + 1; c < l.length; c++) {
                if (l[c] % l[i] == 0) n += nDivide(l, c);
            }
        }
        
        return n;
    }
}
