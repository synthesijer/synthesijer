#include <stdio.h>
#include <stdlib.h>

#include "util.h"

void minivm_error(char *msg)
{
  fprintf(stderr, "%s\n", msg);
  exit(1);
}
