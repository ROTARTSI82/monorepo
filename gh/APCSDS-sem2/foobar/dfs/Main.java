package dfs;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * You're awfully close to destroying the LAMBCHOP doomsday device and freeing Commander Lambda's bunny workers,
 * but once they're free of the work duties the bunnies are going to need to escape Lambda's space station via the
 * escape pods as quickly as possible. Unfortunately, the halls of the space station are a maze of corridors and dead
 * ends that will be a deathtrap for the escaping bunnies. Fortunately, Commander Lambda has put you in charge of a
 * remodeling project that will give you the opportunity to make things a little easier for the bunnies. Unfortunately
 * (again), you can't just remove all obstacles between the bunnies and the escape pods - at most you can remove one
 * wall per escape pod path, both to maintain structural integrity of the station and to avoid arousing Commander
 * Lambda's suspicions.   You have maps of parts of the space station, each starting at a work area exit and ending
 * at the door to an escape pod. The map is represented as a matrix of 0s and 1s, where 0s are passable space
 * and 1s are impassable walls. The door out of the station is at the top left (0,0) and the door into an escape
 * pod is at the bottom right (w-1,h-1).
 *
 * Write a function solution(map) that generates the length of the shortest path from the station door to the escape
 * pod, where you are allowed to remove one wall as part of your remodeling plans. The path length is the total number
 * of nodes you pass through, counting both the entrance and exit nodes. The starting and ending positions are always
 * passable (0). The map will always be solvable, though you may or may not need to remove a wall. The height and
 * width of the map can be from 2 to 20. Moves can only be made in cardinal directions; no diagonal moves are allowed.
 */

public class Main
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("11 = " + Solution.solution(new int[][]{{0, 0, 0, 0, 0, 0}, {1, 1, 1, 1, 1, 0}, {0, 0, 0, 0, 0, 0}, {0, 1, 1, 1, 1, 1}, {0, 1, 1, 1, 1, 1}, {0, 0, 0, 0, 0, 0}}));
        System.out.println("7 = " + Solution.solution(new int[][]{{0, 1, 1, 0}, {0, 0, 0, 1}, {1, 1, 0, 0}, {1, 1, 1, 0}}));

        StringBuilder out = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 4096; i++)
        {
            int[][] map = null;

            int ans = 1600;
            while (ans == 1600)
            {
                int w = 2 + r.nextInt(19);
                int h = 2 + r.nextInt(19);
                map = new int[h][w];

                for (int x = 0; x < w; x++)
                    for (int y = 0; y < h; y++)
                        map[y][x] = r.nextInt(2);
                ans = Solution.solution(map);
            }

            out.append("solution([");
            for (int[] ro : map)
            {
                out.append("[");
                for (int v : ro)
                {
                    out.append(v);
                    out.append(",");
                }
                out.append("],");
            }
            out.append("]) = ");
            out.append(ans);
            out.append('\n');
        }

        FileWriter write = new FileWriter("javaout.txt");
        write.write(out.toString());
        write.flush();
    }
}
