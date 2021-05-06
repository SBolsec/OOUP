#include "myfactory.h"

#include <dlfcn.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

typedef void* (*FUNCREATE)(char const*);

void *myfactory(char const *libname, char const *ctorarg)
{
  int count = 0;
  while (1)
  {
    if (libname[count] == '\0')
      break;
    count++;
  }
  int size = 2 + count + 3 + 1;
  char *file = malloc(size);
  file[0] = '.';
  file[1] = '/';
  for (int i = 2; i < 2 + count; i++)
  {
    file[i] = libname[i-2];
  }
  file[size-4] = '.';
  file[size-3] = 's';
  file[size-2] = 'o';
  file[size-1] = '\0';

  void *handle = dlopen(file, RTLD_LAZY);
  if (!handle)
  {
    return NULL;
  }
  dlerror();    /* Clear any existing error */

  FUNCREATE create = (FUNCREATE) dlsym(handle, "create");

  return create(ctorarg);
}