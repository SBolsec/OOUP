#include <iostream>
#include <stdlib.h>
#include <cstring>
#include <string>
#include <vector>
#include <set>

template <typename Iterator, typename Predicate>
Iterator mymax(Iterator first, Iterator last, Predicate pred)
{
    Iterator max = first;
    first++;
    while (first != last)
    {
        if (pred(*first, *max))
        {
            max = first;
        }
        ++first;
    }
    return max;
}

int gt_int(int a, int b)
{
    return a > b ? 1 : 0;
}

int gt_char(char a, char b)
{
    return a > b ? 1 : 0;
}

int gt_str(const char *a, const char *b)
{
    return strcmp(a, b) > 0 ? 1 : 0;
}

int gt_string(std::string a, std::string b)
{
    return a.compare(b) > 0 ? 1 : 0;
}

int main()
{
    int arr_int[] = {1, 3, 5, 7, 4, 6, 9, 2, 0};
    char arr_char[] = "Suncana strana ulice";
    const char *arr_str[] = {
        "Gle", "malu", "vocku", "poslije", "kise",
        "Puna", "je", "kapi", "pa", "ih", "njise"};
    std::vector<std::string> strings {
        "Gle", "malu", "vocku", "poslije", "kise",
        "Puna", "je", "kapi", "pa", "ih", "njise"};
    std::vector<int> intvector{1, 3, 5, 7, 4, 6, 9, 2, 0};
    std::set<int> intset{1, 3, 5, 7, 4, 6, 9, 2, 0};

    const int *maxint = mymax(&arr_int[0], &arr_int[sizeof(arr_int) / sizeof(*arr_int)], gt_int);
    const char *maxchar = mymax(&arr_char[0], &arr_char[sizeof(arr_char) / sizeof(*arr_char)], gt_char);
    const char **maxstring = mymax(&arr_str[0], &arr_str[sizeof(arr_str) / sizeof(*arr_str)], gt_str);
    std::vector<std::string>::iterator it1 = mymax(strings.begin(), strings.end(), gt_string);
    std::vector<int>::iterator it2 = mymax(intvector.begin(), intvector.end(), gt_int);
    std::set<int>::iterator it3 = mymax(intset.begin(), intset.end(), gt_int);

    std::cout << "Max int: " << *maxint << std::endl;
    std::cout << "Max char: " << *maxchar << std::endl;
    std::cout << "Max cstring: " << *maxstring << std::endl;
    std::cout << "Max vector<string>: " << *it1 << std::endl;
    std::cout << "Max vector<int>: " << *it2 << std::endl;
    std::cout << "Max set<int>: " << *it3 << std::endl;

    return 0;
}