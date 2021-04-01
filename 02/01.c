#include <stdio.h>
#include <stdlib.h>
#include <string.h>

const void *mymax(const void *base, size_t nmeb, size_t size, int (*compar)(const void *, const void *))
{
    const void *max = base;
    for (size_t i = 1; i < nmeb; ++i)
    {
        const void *current = ((const char *)base) + (i*size);
        if (compar(current, max) == 1) 
        {
            max = current;
        }
    }
    return max;
}

int gt_int(const void *a, const void *b)
{
    int x = *(int *)a;
    int y = *(int *)b;

    return x > y ? 1 : 0;
}

int gt_char(const void *a, const void *b)
{
    char x = *(char *)a;
    char y = *(char *)b;

    return x > y ? 1 : 0;
}

int gt_str(const void *a, const void *b)
{
    const char **x = (const char **)a;
    const char **y = (const char **)b;
    return strcmp(*x, *y) > 0 ? 1 : 0;
}

int main(void)
{
    int arr_int[] = {1, 3, 5, 7, 4, 6, 9, 2, 0};
    char arr_char[] = "Suncana strana ulice";
    const char *arr_str[] = {
        "Gle", "malu", "vocku", "poslije", "kise",
        "Puna", "je", "kapi", "pa", "ih", "njise"};

    int res1 = *(int *)mymax(arr_int, sizeof(arr_int) / sizeof(int), sizeof(int), gt_int);
    char res2 = *(char *)mymax(arr_char, sizeof(arr_char) / sizeof(char), sizeof(char), gt_char);
    char *res3 = *(const char **)mymax(arr_str, sizeof(arr_str) / sizeof(char *), sizeof(char*), gt_str);

    printf("Max int: %d\n", res1);
    printf("Max char: %c\n", res2);
    printf("Max str: %s\n", res3);

    return 0;
}