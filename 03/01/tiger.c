#include <stdlib.h>

typedef char const *(*PTRFUN)();

struct Tiger {
  PTRFUN *vtable;
  char const *name;
};

char const *name(void* this)
{
  return ((struct Tiger*) this)->name;
}
char const *greet(void)
{
  return "tiger greet";
}
char const *menu(void)
{
  return "tiger menu";
}

PTRFUN tiger_vtable[] = {
  (PTRFUN) name,
  (PTRFUN) greet,
  (PTRFUN) menu
};

void* create(char const* name) 
{
  struct Tiger* p = (struct Tiger*) malloc(sizeof(struct Tiger));
  p->name = name;
  p->vtable = tiger_vtable;
  return p;
}