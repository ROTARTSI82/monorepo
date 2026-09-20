import math

def gcd(a,b):
    while b:
        a,b = b, a%b
    return a

def frac_reduce(x, y):
    d = gcd(abs(x), abs(y))
    return (x // d, y // d) if d != 0 else (x, y)

def solution(dimensions, your_position, trainer_position, distance):

    angles = set()
    anti_angles = set()

    def calc_pos(cx, cy):
        if cx % 2 == 0:
            ou_x = dimensions[0] * cx
            tr_x = dimensions[0] * cx + trainer_position[0] - your_position[0]
        else:
            ou_x = dimensions[0] * (cx+1) - 2*your_position[0]
            tr_x = dimensions[0] * (cx+1) - trainer_position[0] - your_position[0]

        if cy % 2 == 0:
            ou_y = dimensions[0] * cy
            tr_y = dimensions[1] * cy + trainer_position[1] - your_position[1]
        else:
            ou_y = dimensions[0] * (cy+1) - 2*your_position[1]
            tr_y = dimensions[1] * (cy+1) - trainer_position[1] - your_position[1]
        return ou_x, ou_y, tr_x, tr_y

    # this function adds the angle to the target in the "room" in the infinite grid
    # with coords relative to the "real" room (being 0, 0)
    def consider_meta_room(cx, cy):
        ou_x, ou_y, tr_x, tr_y = calc_pos(cx, cy)

        dist_ou = math.hypot(ou_x, ou_y)
        dist_tr = math.hypot(tr_x, tr_y)


        # print (cx, cy, dist_tr, dist_ou)

        # assumes that angles in anti_angles are always from
        # objects closer. We must be within the distance limit,
        # and you cannot block the target within this room, and
        # another room before you cannot have blocked this target
        if dist_tr < distance:
            ang = frac_reduce(tr_x, tr_y)
            anti_ang = frac_reduce(ou_x, ou_y)
            if (not (ang == anti_ang and dist_tr > dist_ou)) and not ang in anti_angles:
                angles.add(ang)
        else:
            anti_ang = frac_reduce(ou_x, ou_y)
        anti_angles.add(anti_ang)


    to_consider = []
    def add_consideration(cx, cy):
        ou_x, ou_y, tr_x, tr_y = calc_pos(cx, cy)
        dist = math.hypot(tr_x, tr_y)

        if dist < distance:
            to_consider.append((cx, cy, dist))
            return True
        return False

    # we call consider_meta_room in concentric rings outwards until it never returns true
    flag = True
    ringNo = 1
    add_consideration(0,0)
    while flag:
        flag = False
        for i in range(2 * ringNo):
            flag |= add_consideration(ringNo, ringNo - i)
            flag |= add_consideration(ringNo - i, -ringNo)
            flag |= add_consideration(-ringNo, -ringNo + i)
            flag |= add_consideration(-ringNo + i, ringNo)
        ringNo += 1

    to_consider = sorted(to_consider, key=lambda x : x[2])

    for i in to_consider:
        consider_meta_room(i[0], i[1])

    return len(angles)


"""
Verifying solution...
Test 1 passed!
Test 2 passed!
Test 3 failed  [Hidden]
Test 4 passed! [Hidden]
Test 5 failed  [Hidden]
Test 6 passed! [Hidden]
Test 7 passed! [Hidden]
Test 8 passed! [Hidden]
Test 9 passed! [Hidden]
Test 10 passed! [Hidden]
"""