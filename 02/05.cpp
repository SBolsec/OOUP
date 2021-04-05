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
            int num = std::stoi(input);
            return num < 0 ? -1 : num;
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
            int num = std::stoi(word);
            return num < 0 ? -1 : num;
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
    for (int i = 0; i < 9; i++)
    {
        std::cout << izvor->generateNumber() << std::endl;
    }
    delete izvor;
    IzvorBrojeva *izvor1 = new TipkovnickiIzvor();
    std::cout << izvor1->generateNumber() << std::endl;
    std::cout << izvor1->generateNumber() << std::endl;
    std::cout << izvor1->generateNumber() << std::endl;
    return 0;
}