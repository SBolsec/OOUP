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

void *create(char const* name) 
{
  struct Parrot* p = (struct Parrot*) malloc(sizeof(struct Parrot));
  p->name = name;
  p->vtable = parrot_vtable;
  return p;
}