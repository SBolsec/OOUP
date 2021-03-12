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

PTRFUN dogMethods[2] = {&dogGreet, &dogMenu};
PTRFUN catMethods[2] = {&catGreet, &catMenu};

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
    char *numberArray = calloc(n + 1, sizeof(char));
    for (int i = n - 1; i >= 0; --i, number /= 10)
    {
        numberArray[i] = (number % 10) + '0';
    }
    numberArray[n] = '\0';
    return numberArray;
}

struct Animal *createNDogs(int n)
{
    struct Animal *dogs = malloc(n * sizeof(struct Animal));

    for (int i = 0; i < n; i++)
    {
        char *c = toArray(i);
        dogs[i] = *(createDog(c));
    }

    return dogs;
}

void testAnimals(void)
{
    int n = 250;
    struct Animal *dogs = createNDogs(n);
    for (int i = 0; i < n; i++)
    {
        animalPrintGreeting(&dogs[i]);
        animalPrintMenu(&dogs[i]);
    }
}

int main(void)
{
    testAnimals();
    return 0;
}