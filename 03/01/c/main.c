#include "myfactory.h"

#include <stdio.h>
#include <stdlib.h>

typedef char const *(*PTRFUN)();

struct Animal
{
  PTRFUN *vtable;
  // vtable entries:
  // 0: char const* name(void* this);
  // 1: char const* greet();
  // 2: char const* menu();
};

// parrots and tigers defined in respective dynamic libraries

// animalPrintGreeting and animalPrintMenu similar as in lab 1
void animalPrintGreeting(struct Animal *animal)
{
  printf("%s pozdravlja: %s\n", (animal->vtable[0])(animal), (animal->vtable[1])());
}
void animalPrintMenu(struct Animal *animal)
{
  printf("%s voli %s\n", (animal->vtable[0])(animal), (animal->vtable[2])());
}

int main(int argc, char *argv[])
{
  for (int i = 0; i < argc; ++i)
  {
    struct Animal *p = (struct Animal *)myfactory(argv[i], "Modrobradi");
    if (!p)
    {
      printf("Creation of plug-in object %s failed.\n", argv[i]);
      continue;
    }

    animalPrintGreeting(p);
    animalPrintMenu(p);
    free(p);
  }

  printf("\n=== STACK ===\n");
  for (int i = 1; i < argc; i++) {
    int size = objectsize(argv[i]);
    char object[size];
    myfactorystack(object, argv[i], "Modrobradi");
    struct Animal *animal = (struct Animal *) object;
    animalPrintGreeting(animal);
    animalPrintMenu(animal);
  }
}
