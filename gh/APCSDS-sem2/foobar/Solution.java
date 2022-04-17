public class Solution {
    private final int explored[][];
    private final int map[][];
    private final int cache[][][];
    
    public Solution(int[][] map) {
        this.map = map;
        this.explored = new int[map.length][map[0].length];
        this.cache = new int[2][map.length][map[0].length];
        
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                cache[0][i][j] = 40*40;
                cache[1][i][j] = 40*40;
            }
        }
    }
    
    
    private int extend(int r, int c, boolean canRM, int exploreLevel) {
        if (r >= 0 && c >= 0 && r < map.length && c < map[0].length) {
            if (explored[r][c] < exploreLevel) {
                if (map[r][c] == 0) {
                    dfs(r, c, canRM); // sets cache
                } else if (canRM) {
                    dfs(r, c, false); // sets cache
                }
            } 
            
            return 1 + cache[exploreLevel - 1][r][c];
            
        }
        
        return 40*40;
    }
    
    public int dfs(int r, int c, boolean canRM) {
        if (r == map.length - 1 && c == map[0].length - 1) {
            // System.out.println("FOUND 0");
            cache[0][r][c] = 1;
            cache[1][r][c] = 1;
            return 1;
        }
        
        int exploreLevel = canRM ? 2 : 1;
        
        int ret = 40*40;
        
        int prev = explored[r][c];
        explored[r][c] = 100; // do not explore this square: prevent infinite recursion
        ret = Math.min(ret, extend(r + 1, c, canRM, exploreLevel));
        ret = Math.min(ret, extend(r - 1, c, canRM, exploreLevel));
        ret = Math.min(ret, extend(r, c + 1, canRM, exploreLevel));
        ret = Math.min(ret, extend(r, c - 1, canRM, exploreLevel));
        explored[r][c] = Math.max(prev, exploreLevel);
        
        // System.out.println("RET " + ret);
        
        if (!canRM) cache[0][r][c] = ret;
        cache[1][r][c] = ret;
        return ret;
    }
    
    public static int solution(int[][] map) {
        return new Solution(map).dfs(0, 0, true);
    }
}
