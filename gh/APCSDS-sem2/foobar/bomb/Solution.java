import java.math.BigInteger;
public class Solution {
    
    
    public static String solution(String x, String y) {
        BigInteger bx = new BigInteger(x), by = new BigInteger(y);
        
        BigInteger n = BigInteger.ZERO;
        BigInteger one = BigInteger.valueOf(1);
        while (bx.compareTo(BigInteger.ONE) > 0 && by.compareTo(BigInteger.ONE) > 0) {
            int cmp = bx.compareTo(by);
            if (cmp == 0) return "impossible";
            
            if (cmp > 0) {
                bx = bx.subtract(by);
            } else {
                by = by.subtract(bx);
            }
            
            n = n.add(BigInteger.ONE);
        }
        
        if (bx.compareTo(BigInteger.ONE) < 0 || by.compareTo(BigInteger.ONE) < 0)
            return "impossible";
        
        return n.add(bx.subtract(BigInteger.ONE).add(by.subtract(BigInteger.ONE))).toString();
    }
}
