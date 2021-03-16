#include <stdio.h>
#include <stdlib.h>

char const *dogGreet(void)
{
    return "vau!";
}
char const *dogMenu(void)
{
    return "kuhanu govedinu";
}
char const *catGreet(void)
{
    return "mijau!";
}
char const *catMenu(void)
{
    return "konzerviranu tunjevinu";
}

typedef char const *(*PTRFUN)();

struct Animal
{
    char const *name;
    PTRFUN *methods;
};

PTRFUN dogMethods[] = {&dogGreet, &dogMenu};
PTRFUN catMethods[] = {&catGreet, &catMenu};

void animalPrintGreeting(struct Animal *animal)
{
    printf("%s pozdravlja: %s\n", animal->name, animal->methods[0]());
}
void animalPrintMenu(struct Animal *animal)
{
    printf("%s voli %s\n", animal->name, animal->methods[1]());
}
void constructDog(struct Animal *animal, char const *name)
{
    animal->name = name;
    animal->methods = dogMethods;
}
void constructCat(struct Animal *animal, char const *name)
{
    animal->name = name;
    animal->methods = catMethods;
}
struct Animal *createDog(char const *name)
{
    struct Animal *dog = malloc(sizeof(struct Animal));
    constructDog(dog, name);
    return dog;
}
struct Animal *createCat(char const *name)
{
    struct Animal *cat = malloc(sizeof(struct Animal));
    constructCat(cat, name);
    return cat;
}

void testAnimals(void)
{
    struct Animal *p1 = createDog("Hamlet");
    struct Animal *p2 = createCat("Ofelija");
    struct Animal *p3 = createDog("Polonije");

    animalPrintGreeting(p1);
    animalPrintGreeting(p2);
    animalPrintGreeting(p3);

    animalPrintMenu(p1);
    animalPrintMenu(p2);
    animalPrintMenu(p3);

    free(p1);
    free(p2);
    free(p3);
}

// Helper function for creating dog names
// Turns integer into a character array
char *toArray(int number)
{
    int n = 0;
    int copy = number;
    while (copy != 0)
    {
        copy /= 10;
        ++n;
    }
    if (number == 0)
        n = 1;
    char *numberArray = calloc(4 + n + 1, sizeof(char));
    numberArray[0] = 'D';
    numberArray[1] = 'o';
    numberArray[2] = 'g';
    numberArray[3] = ' ';
    for (int i = 4 + n - 1; i >= 4; --i, number /= 10)
    {
        numberArray[i] = (number % 10) + '0';
    }
    numberArray[4+ n] = '\0';
    return numberArray;
}

// Function to create n dogs by using one malloc call
void createNDogs(int n)
{
    // alocate the memory for the dogs
    struct Animal *dogs = malloc(n * sizeof(struct Animal));

    // initialize the dogs
    for (int i = 0; i < n; i++)
    {
        char *c = toArray(i+1);
        dogs[i] = *(createDog(c));
    }

    // test if it's working
    for (int i = 0; i < n; i++)
    {
        animalPrintGreeting(&dogs[i]);
        animalPrintMenu(&dogs[i]);
    }
}

void createOnStack() {
    // create and initialize animals on stack
    struct Animal p1;
    p1.name = "Achiles";
    p1.methods = dogMethods;
    struct Animal p2;
    p2.name = "Persephone";
    p2.methods = catMethods;
    struct Animal p3;
    p3.name = "Theseus";
    p3.methods = dogMethods;

    // test if it's working
    animalPrintGreeting(&p1);
    animalPrintGreeting(&p2);
    animalPrintGreeting(&p3);

    animalPrintMenu(&p1);
    animalPrintMenu(&p2);
    animalPrintMenu(&p3);
}

int main(void)
{
    testAnimals();

    printf("\n=== Create animals on stack: ===\n");
    createOnStack();

    printf("\n=== Creating n(5) dogs: ===\n");
    createNDogs(5);
    return 0;
}