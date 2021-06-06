// gcc -Wall -Wextra -Wpedantic -g3 -O0 -fsanitize=address -fno-omit-frame-pointer png2rim.c $(pkg-config --cflags --libs zlib) -I. -lm

#include <zlib.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <memory.h>

#define STB_IMAGE_IMPLEMENTATION
#include <stb_image.h>


int main(int argc, char **argv) {
    int x,y,n;
    unsigned char *data = stbi_load(argv[1], &x, &y, &n, 4);

    if (n != 4) {
        printf("Image must be rgba\n");
        return 1;
    }

    unsigned long comp_size = x * y * 4;
    unsigned long dat_size = comp_size;

    uint8_t *compressed = malloc(comp_size);
    compress2(compressed, &comp_size, data, dat_size, Z_BEST_COMPRESSION);
    stbi_image_free(data);

    uint8_t *final_dat = malloc(comp_size + 8);
    (*(uint32_t *) final_dat) =  htonl(((uint32_t) x));
    (*(uint32_t *) (final_dat + 4)) = htonl(((uint32_t) y));

    memcpy(final_dat + 8, compressed, comp_size);

    free(compressed);

    FILE *fp = fopen(argv[2], "wb");
    fwrite(final_dat, 1, comp_size + 8, fp);
    fclose(fp);

    free(final_dat);
}
