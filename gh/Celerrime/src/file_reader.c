//
// Created by grant on 5/30/21.
//

#include "file_reader.h"

#include "stdlib.h"
#include <stdio.h>
#include <stdint.h>
#include <memory.h>


#include <netinet/in.h>

void *full_read_file(const char *filename, long *size) {
    FILE *fp = fopen(filename, "r");
    fseek(fp, 0L, SEEK_END);
    *size = ftell(fp);
    rewind(fp);

    void *ret = malloc((*size));
    fread(ret, 1, (*size), fp);
    fclose(fp);

    return ret;
}

