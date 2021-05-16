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

void construct(void *object, char const *name)
{
  struct Tiger *tiger = (struct Tiger *) object;
  tiger->name = name;
  tiger->vtable = tiger_vtable;
}

void* create(char const* name) 
{
  struct Tiger* t = (struct Tiger*) malloc(sizeof(struct Tiger));
  construct(t, name);
  return t;
}

int size()
{
  return sizeof(struct Tiger);
}