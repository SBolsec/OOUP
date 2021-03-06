#include <stdio.h>

typedef char const* (*PTRFUN) ();

struct Animal {
    char* name;
    struct Animal_methods* methods;
};
struct Animal_methods {
    PTRFUN greet;
    PTRFUN menu;
};

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
void animalPrintGreeting(struct Animal* animal) {
    printf("%s pozdravlja: %s\n", animal->name, animal->methods->greet());
}
void animalPrintMenu(struct Animal* animal) {
    printf("%s voli %s\n", animal->name, animal->methods->menu());
}
void constructDog(struct Animal* animal, char const* name) {
    animal->name = name;
    animal->methods = malloc(sizeof (struct Animal_methods));
    animal->methods->greet = &dogGreet;
    animal->methods->menu = &dogMenu;
}
void constructCat(struct Animal* animal, char const* name) {
    animal->name = name;
    animal->methods = malloc(sizeof (struct Animal_methods));
    animal->methods->greet = &catGreet;
    animal->methods->menu = &catMenu;
}
struct Animal* createDog(char const* name) {
    struct Animal* animal = malloc(sizeof (struct Animal));
    constructDog(animal, name);
    return animal;
}
struct Animal* createCat(char const* name) {
    struct Animal* animal = malloc(sizeof (struct Animal));
    constructCat(animal, name);
    return animal;
}

void testAnimals(void) {
    struct Animal* p1 = createDog("Hamlet");
    struct Animal* p2 = createCat("Ofelija");
    struct Animal* p3 = createDog("Polonije");

    animalPrintGreeting(p1);
    animalPrintGreeting(p2);
    animalPrintGreeting(p3);

    animalPrintMenu(p1);
    animalPrintMenu(p2);
    animalPrintMenu(p3);

    free(p1); free(p2); free(p3);
}

int main(void) {
    testAnimals();
    return 0;
}