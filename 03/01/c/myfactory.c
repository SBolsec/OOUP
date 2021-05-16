#include "myfactory.h"

#include <dlfcn.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

typedef void* (*FUNCREATE)(char const*);
typedef size_t (*FUNSIZE)();
typedef void (*FUNCONSTRUCT)(void*, char const*);

void *myfactory(char const *libname, char const *ctorarg)
{
  int count = strlen(libname);
  char file[100] = {};
  strcat(file, "./");
  strncat(file, libname, count);
  strcat(file, ".so");

  void *handle = dlopen(file, RTLD_LAZY);
  if (!handle)
    return NULL;
  
  dlerror();    /* Clear any existing error */

  FUNCREATE create = (FUNCREATE) dlsym(handle, "create");

  if (dlerror() != NULL)
    return NULL;

  return create(ctorarg);
}

int objectsize(char const *libname) 
{
  int count = strlen(libname);
  char file[100] = {};
  strcat(file, "./");
  strncat(file, libname, count);
  strcat(file, ".so");

  void *handle = dlopen(file, RTLD_LAZY);
  if (!handle)
    return NULL;
  
  dlerror();    /* Clear any existing error */

  FUNSIZE f = (FUNSIZE) dlsym(handle, "size");

  if (dlerror() != NULL)
    return NULL;

  return f();
}

void myfactorystack(void *object, char const *libname, char const *ctorarg)
{
  int count = strlen(libname);
  char file[100] = {};
  strcat(file, "./");
  strncat(file, libname, count);
  strcat(file, ".so");

  void *handle = dlopen(file, RTLD_LAZY);
  if (!handle)
    return NULL;
  
  dlerror();    /* Clear any existing error */

  FUNCONSTRUCT construct = (FUNCONSTRUCT) dlsym(handle, "construct");

  if (dlerror() != NULL)
    return NULL;

  return construct(object, ctorarg);
}