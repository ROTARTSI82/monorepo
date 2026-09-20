"""
Bringing a Gun to a Trainer Fight
=================================

Uh-oh -- you've been cornered by one of Commander Lambdas elite bunny trainers!
Fortunately, you grabbed a beam weapon from an abandoned storeroom while you were running through the station,
so you have a chance to fight your way out. But the beam weapon is potentially dangerous to you as well
as to the bunny trainers: its beams reflect off walls, meaning you'll have to be very careful where
you shoot to avoid bouncing a shot toward yourself!

Luckily, the beams can only travel a certain maximum distance before becoming too weak to cause damage.
You also know that if a beam hits a corner, it will bounce back in exactly the same direction.
And of course, if the beam hits either you or the bunny trainer, it will stop immediately (albeit painfully).

Write a function solution(dimensions, your_position, trainer_position, distance) that gives an array of 2 integers
of the width and height of the room, an array of 2 integers of your x and y coordinates in the room, an array
of 2 integers of the trainer's x and y coordinates in the room, and returns an integer of the number of distinct
directions that you can fire to hit the elite trainer, given the maximum distance that the beam can travel.

The room has integer dimensions [1 < x_dim <= 1250, 1 < y_dim <= 1250]. You and the elite trainer are both positioned
on the integer lattice at different distinct positions (x, y) inside the room such that [0 < x < x_dim, 0 < y < y_dim].
Finally, the maximum distance that the beam can travel before becoming harmless will be given as an integer
1 < distance <= 10000.

For example, if you and the elite trainer were positioned in a room with dimensions [3, 2], your_position [1, 1],
trainer_position [2, 1], and a maximum shot distance of 4, you could shoot in seven different directions to hit the
elite trainer (given as vector bearings from your location):
[1, 0], [1, 2], [1, -2], [3, 2], [3, -2], [-3, 2], and [-3, -2].
As specific examples, the shot at bearing [1, 0] is the straight line horizontal shot of distance 1,
the shot at bearing [-3, -2] bounces off the left wall and then the bottom wall before hitting the elite trainer with
a total shot distance of sqrt(13), and the shot at bearing [1, 2] bounces off just the top wall before hitting the
elite trainer with a total shot distance of sqrt(5).

Languages
=========

To provide a Java solution, edit Solution.java
To provide a Python solution, edit solution.py

Test cases
==========
Your code should pass the following test cases.
Note that it may also be run against hidden test cases not shown here.

-- Java cases --
Input:
Solution.solution([3,2], [1,1], [2,1], 4)
Output:
    7

Input:
Solution.solution([300,275], [150,150], [185,100], 500)
Output:
    9

-- Python cases --
Input:
solution.solution([3,2], [1,1], [2,1], 4)
Output:
    7

Input:
solution.solution([300,275], [150,150], [185,100], 500)
Output:
    9

Use verify [file] to test your solution and see how it does. When you are finished editing your code,
use submit [file] to submit your answer. If your solution passes the test cases,
it will be removed from your home folder.

"""
import math
from collections import deque

def gcd(a,b):
    while b:
        a,b = b, a%b
    return max(a,1)

def frac_reduce(x, y):
    d = gcd(abs(x), abs(y))
    return (x // d, y // d)

def solution(dimensions, your_position, trainer_position, distance):
    def calc_pos(cx, cy):
        if cx % 2 == 0:
            ou_x = dimensions[0] * cx
            tr_x = dimensions[0] * cx + trainer_position[0] - your_position[0]
        else:
            ou_x = dimensions[0] * (cx+1) - 2*your_position[0]
            tr_x = dimensions[0] * (cx+1) - trainer_position[0] - your_position[0]

        if cy % 2 == 0:
            ou_y = dimensions[1] * cy
            tr_y = dimensions[1] * cy + trainer_position[1] - your_position[1]
        else:
            ou_y = dimensions[1] * (cy+1) - 2*your_position[1]
            tr_y = dimensions[1] * (cy+1) - trainer_position[1] - your_position[1]
        return ou_x, ou_y, tr_x, tr_y


    angles = set()
    anti_angles = set()

    rt_x, rt_y = trainer_position[0] - your_position[0], trainer_position[1] - your_position[1]
    d2t = math.hypot(rt_x, rt_y)
    if d2t <= distance:
        angles.add(frac_reduce(rt_x, rt_y))

    # this function adds the angle to the target in the "room" in the infinite grid
    # with coords relative to the "real" room (being 0, 0)
    def consider_meta_room(cx, cy):
        ou_x, ou_y, tr_x, tr_y = calc_pos(cx, cy)

        dist_ou = math.hypot(ou_x, ou_y)
        dist_tr = math.hypot(tr_x, tr_y)

        anti_ang = frac_reduce(ou_x, ou_y)
        ang = frac_reduce(tr_x, tr_y)
        # print (cx, cy, dist_tr, dist_ou)

        # assumes that angles in anti_angles are always from
        # objects closer. We must be within the distance limit,
        # and you cannot block the target within this room, and
        # another room before you cannot have blocked this target
        if dist_tr <= distance and (dist_tr < dist_ou or ang != anti_ang) and not ang in anti_angles:
            angles.add(ang)
        anti_angles.add(anti_ang)

    to_consider = []
    def add_consideration(cx, cy):
        ou_x, ou_y, tr_x, tr_y = calc_pos(cx, cy)
        dist = math.hypot(tr_x, tr_y)
        dist2 = math.hypot(ou_x, ou_y)

        if dist <= distance or dist2 <= distance:
            to_consider.append((cx, cy, dist))
            return True
        return False

    # add_consideration(0,0)
    bfs = deque([(-1, 0), (1, 0), (0, 1), (0, -1)])
    # seen = {(-1, 0), (1, 0), (0, 1), (0, -1)}
    while len(bfs) > 0:
        next = bfs.popleft()
        if add_consideration(next[0], next[1]):
            def requeue(dx,dy):
                p = (next[0] + dx, next[1] + dy)
                if abs(p[0]) >= abs(next[0]) and abs(p[1]) >= abs(next[1]) and not p in bfs:
                    bfs.append(p)
                    # seen.add(p)
            requeue(1,0)
            requeue(-1,0)
            requeue(0,1)
            requeue(0,-1)

    to_consider.sort(key=lambda x : x[2])

    for i in to_consider:
        consider_meta_room(i[0], i[1])

    return len(angles)

print(solution([3,2], [1,1], [2,1], 4), 7)
print(solution([300,275], [150,150], [185,100], 500), 9)


"""
The room has integer dimensions [1 < x_dim <= 1250, 1 < y_dim <= 1250]. You and the elite trainer are both positioned
on the integer lattice at different distinct positions (x, y) inside the room such that [0 < x < x_dim, 0 < y < y_dim].
Finally, the maximum distance that the beam can travel before becoming harmless will be given as an integer
1 < distance <= 10000.
"""


import random
import datetime

info = []

avg = datetime.timedelta()

for i in range(4096):
    dim = [random.randint(2,1250), random.randint(2,1250)]
    yp = [random.randint(1, dim[0] - 1), random.randint(1, dim[1] - 1)]
    tp = yp[:]
    while tp[0] == yp[0] and tp[1] == yp[1]:
        tp = [random.randint(1, dim[0] - 1), random.randint(1, dim[1] - 1)]
    d = random.randint(2, 10000)

    start = datetime.datetime.now()
    ans = solution(dim, yp, tp, d)
    diff = datetime.datetime.now() - start
    avg += diff

    s = "solution(%s, %s, %s, %s) = %s in %s" % (str(dim), str(yp), str(tp), d, ans, str(diff))
    print (str(i) + s)
    info.append((s, diff))

info = sorted(info, key=lambda x : x[1])
tot = ""
for i in info:
    tot += i[0] + "\n"

print ("average case: " + str(avg / 4096))

with open("out3.txt", 'w') as fp:
     fp.write(tot)
