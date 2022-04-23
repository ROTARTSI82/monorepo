cache = []
storecache = []
storemap = []

from copy import deepcopy

def pp(matrix):
    print('\n'.join(['\t'.join([str(cell) for cell in row]) for row in matrix]))

def solution(map):
    # finds the solution without any changes
    global cache, storecache, storemap
    tmp = [[0 for x in range(len(map[0]))] for y in range(len(map))]
    cache = deepcopy(tmp)
    current_shortest = min_length(map, 0, 0, 0)

    # iteratively goes through every single new matrix
    for y in range(len(map)):
        for x in range(len(map[0])):
            if map[y][x] == 1:
                map[y][x] = 0
                # finds the shortest map in that new matrix and updates current shortest accordingly
                cache = deepcopy(tmp)
                new = min_length(deepcopy(map), 0, 0, 0)

#                 map[y][x] = 5

                if new < current_shortest:
                    current_shortest = new
                    storecache = deepcopy(cache)
                    storemap = deepcopy(map)
#
#                     pp (cache)
#                     print('----- ' + str(new))
#                     pp (map)
#                     print ('\n\n\n\n')
                map[y][x] = 1

    return current_shortest


# goes through all find the minimum length of a map
def min_length(map, x, y, d):
    global cache
    if cache[y][x] != 0 and cache[y][x] < 3600:
        return cache[y][x]

    if x == len(map[0]) - 1 and y == len(map) - 1:
        cache[y][x] = 1
        return 1
    else:
#         cache[y][x] = 100000
        map[y][x]=1
        minimum = 100000
        minimum = get_neighbors(map, x, y,d)
#         for neighbors in neighborlmao:
#             xcoord = neighbors[0]
#             ycoord = neighbors[1]
#             minimum = min(minimum, min_length(map, xcoord, ycoord))
        map[y][x] = 0

        cache[y][x] = minimum + 1

        return minimum + 1


def get_neighbors(map, xcoord, ycoord, d):
    global cache
    ret = 100000
    if xcoord < len(map[0]) - 1 and map[ycoord][xcoord+1] == 0:
        ret = min(ret, min_length(map, xcoord + 1, ycoord, d+1))
    if xcoord > 0 and map[ycoord][xcoord-1] == 0:
        ret = min(ret, min_length(map, xcoord - 1, ycoord, d+1))
    if ycoord < len(map)-1 and map[ycoord+1][xcoord] == 0:
        ret = min(ret, min_length(map, xcoord, ycoord + 1, d+1))
    if ycoord > 0 and map[ycoord-1][xcoord] == 0:
        ret = min(ret, min_length(map, xcoord, ycoord - 1, d+1))

#     print ("neighbors of %s %s = %s" % (xcoord, ycoord, ret) )
    return ret



with open("diff.txt", 'r') as fp:
    cases = fp.read().split('\n')

out = ""
for i in cases:
    case = i.split('=')
#     print(case[1])
    case[1] = case[1].split(" ")[1]

    varun = eval(case[0])
#     print (case[1])
    me = eval(case[1])
    if varun != me:
        print ("%s  VARUN: %s" % (i, varun))
        print ("cache: ")
        pp (storecache)
        print ("----\nmap:")
        usedmap = eval(case[0].split('(')[1].split(")")[0])
        pp ([[usedmap[r][i] - 2 * storemap[r][i] for i in range(len(storemap[0]))] for r in range(len(storemap))])
        print("\n\n\n\n")
    else:
        print ("PASS")


# print(solution([[0,0,0,0,],[1,0,1,0,],[1,0,0,0,],[1,1,1,0,],[0,1,0,1,],[0,1,0,0,],[1,0,0,0,],[1,0,0,0,],[1,1,0,0,],[1,1,0,0,],[0,1,0,0,],[1,1,0,1,],[1,1,0,0,],[1,1,0,0,],[1,0,0,1
# ,],[0,0,0,0,],[1,0,1,1,],[1,0,0,0,],[0,1,0,0,],[1,0,0,0,],]))
#
# print(25)



# import random
#
# full = ""
#
# def fuzz():
#     global full
#     width = random.randint(2,20)
#     height = random.randint(2,20)
#
#     tmap = [[random.randint(0, 1) for x in range(width)] for y in range(height)]
#     ans = solution(tmap)
#     full += "solution(%r) = %s\n" % (tmap, ans)
#
#
# for i in range(4096):
#     fuzz()
#
# with open('out.txt', 'w') as fp:
#     fp.write(full)