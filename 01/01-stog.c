#include <stdio.h>
#include <stdlib.h>

char const* dogGreet(void) {
    return "vau!";
}
char const* dogMenu(void) {
    return "kuhanu govedinu";
}
char const* catGreet(void) {
    return "mijau!";
}
char const* catMenu(void) {
    return "konzerviranu tunjevinu";
}

typedef char const* (*PTRFUN) ();

struct Animal {
    char const* name;
    PTRFUN* methods;
};

PTRFUN dogMethods[2] = { &dogGreet, &dogMenu };
PTRFUN catMethods[2] = { &catGreet, &catMenu };

void animalPrintGreeting(struct Animal animal) {
    printf("%s pozdravlja: %s\n", animal.name, animal.methods[0]());
}
void animalPrintMenu(struct Animal animal) {
    printf("%s voli %s\n", animal.name, animal.methods[1]());
}
void constructDog(struct Animal* animal, char const* name) {
    animal->name = name;
    animal->methods = dogMethods;
}
void constructCat(struct Animal* animal, char const* name) {
    animal->name = name;
    animal->methods = catMethods;
}
void createDog(struct Animal* dog, char const* name) {
    constructDog(dog, name);
}
void createCat(struct Animal* cat, char const* name) {
    constructCat(cat, name);
}

void testAnimals(void) {
    struct Animal p1; createDog(&p1, "Hamlet");
    struct Animal p2; createCat(&p2, "Ofelija");
    struct Animal p3; createDog(&p3, "Polonije");

    animalPrintGreeting(p1);
    animalPrintGreeting(p2);
    animalPrintGreeting(p3);

    animalPrintMenu(p1);
    animalPrintMenu(p2);
    animalPrintMenu(p3);
}

int main(void) {
    testAnimals();
    return 0;
}