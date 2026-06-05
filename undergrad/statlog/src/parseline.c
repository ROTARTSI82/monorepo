#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>

enum { ITEM_INT, ITEM_STR };

struct parseline_t {
  int type;
  int idx;
  union {
    long int_val;
    struct {
      char *str;
      char *endptr;
    };
  };
};

void parseline(struct parseline_t *spec, int nspec, char *line, ssize_t size) {
  char *lineptr = line;
  struct parseline_t *specptr = spec;
  int idx = 0;
  while (lineptr < line + size && specptr < spec + nspec) {
    while (isspace(*lineptr) && ++lineptr < line + size)
      ;
    if (lineptr >= line + size)
      break;

    if (idx == specptr->idx) {
      if (specptr->type == ITEM_INT) {
        specptr->int_val = strtol(lineptr, &lineptr, 10);
      } else if (specptr->type == ITEM_STR) {
        specptr->str = lineptr;
        while (++lineptr < line + size && !isspace(*lineptr))
          ;
        specptr->endptr = lineptr;
      }
      specptr++;
    } else {
      while (++lineptr < line + size && !isspace(*lineptr))
        ;
    }
    idx++;
  }
}
