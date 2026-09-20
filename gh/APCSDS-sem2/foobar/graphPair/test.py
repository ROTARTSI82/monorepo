
cache = {}

def cycles(a, b):
    if a == b: return False
    if (a + b) % 2 == 1: return True
    if (b > a):
        tmp = a
        a = b
        b = tmp
    k = (a,b)
    if k in cache:
        return cache[k]

    cache[k] = True
    ret = cycles(a - b, 2 * b)
    cache[k] = ret

    return ret
