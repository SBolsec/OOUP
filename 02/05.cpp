#include <iostream>
#include <vector>
#include <string>
#include <fstream>

// Classes for generating the numbers
class IzvorBrojeva
{
public:
    virtual int generateNumber() = 0;
};

class TipkovnickiIzvor : public IzvorBrojeva
{
public:
    virtual int generateNumber()
    {
        std::cout << "Unesite broj: ";
        std::string input;
        std::cin >> input;

        try
        {
            return std::stoi(input);
        }
        catch (const std::exception &e)
        {
            return -1;
        }
    }
};

class DatotecniIzvor : public IzvorBrojeva
{
private:
    std::ifstream file;

public:
    DatotecniIzvor(std::string fileName)
    {
        try
        {
            file.open(fileName);
        }
        catch (const std::exception &e)
        {
            std::cerr << e.what() << '\n';
            throw e;
        }
    }

    virtual int generateNumber()
    {
        if (!file.is_open())
            return -1;

        try
        {
            std::string word;
            file >> word;
            return std::stoi(word);
        }
        catch (const std::exception &e)
        {
            file.close();
            return -1;
        }
    }

    ~DatotecniIzvor()
    {
        if (file.is_open())
            file.close();
    }
};

class SlijedBrojeva
{
protected:
    std::vector<int> brojevi;

public:
};

int main()
{
    IzvorBrojeva *izvor = new DatotecniIzvor("/data/projekti/OOUP/02/05-brojevi.txt");
    int a1 = izvor->generateNumber();
    int a2 = izvor->generateNumber();
    int a3 = izvor->generateNumber();
    int a4 = izvor->generateNumber();
    int a5 = izvor->generateNumber();
    int a6 = izvor->generateNumber();
    int a7 = izvor->generateNumber();
    int a8 = izvor->generateNumber();
    int a9 = izvor->generateNumber();
    delete izvor;
    return 0;
}