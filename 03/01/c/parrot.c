#include <stdlib.h>

typedef char const *(*PTRFUN)();

struct Parrot {
  PTRFUN *vtable;
  char const *name;
};

char const *name(void* this)
{
  return ((struct Parrot*) this)->name;
}
char const *greet(void)
{
  return "parrot greet";
}
char const *menu(void)
{
  return "parrot menu";
}

PTRFUN parrot_vtable[3] = {
  (PTRFUN) name, 
  (PTRFUN) greet,
  (PTRFUN) menu
};

void construct(void *object, char const* name)
{
  struct Parrot *parrot = (struct Parrot *) object;
  parrot->name = name;
  parrot->vtable = parrot_vtable;
}

void *create(char const* name) 
{
  struct Parrot* p = (struct Parrot*) malloc(sizeof(struct Parrot));
  construct(p, name);
  return p;
}

int size()
{
  return sizeof(struct Parrot);
}