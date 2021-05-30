//
// Created by grant on 5/30/21.
//

#include "common.h"

void *full_read_file(const char *filename, long *size) {
    FILE *fp = fopen(filename, "r");
    fseek(fp, 0L, SEEK_END);
    *size = ftell(fp);
    rewind(fp);

    void *ret = malloc((*size) + 8);
    fread(ret, 1, (*size) + 8, fp);
    fclose(fp);

    return ret;
}
