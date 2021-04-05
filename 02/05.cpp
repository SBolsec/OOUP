#include <iostream>
#include <list>
#include <vector>
#include <string>
#include <algorithm>
#include <fstream>
#include <chrono>
#include <ctime>

// For sleep function
#ifdef _WIN32
#include <Windows.h>
#else
#include <unistd.h>
#endif

class IzvorBrojeva
{
public:
    virtual int generateNumber() = 0;
};

class Akcija
{
public:
    virtual void update() = 0;
};

class SlijedBrojeva
{
private:
    IzvorBrojeva *source;
    std::list<Akcija *> actions;
    std::vector<int> numbers;

public:
    SlijedBrojeva(IzvorBrojeva *source) : source{source} {}

    std::vector<int> getNumbers() { return this->numbers; }

    void attach(Akcija *action)
    {
        actions.push_back(action);
    }
    void dettach(Akcija *action)
    {
        actions.remove(action);
    }
    void notify()
    {
        std::list<Akcija *>::iterator it = actions.begin();
        while (it != actions.end())
        {
            (*it)->update();
            ++it;
        }
    }

    void kreni()
    {
        while (true)
        {
            int number = source->generateNumber();
            if (number == -1)
                break;
            numbers.push_back(number);
            notify();
            sleep(1);
        }
    }
};

// Implementations of number sources
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

// Implementations of actions
class Zapisnik : public Akcija
{
private:
    SlijedBrojeva *slijedBrojeva;
    std::ofstream file;

public:
    Zapisnik(SlijedBrojeva *slijedBrojeva) : slijedBrojeva{slijedBrojeva}
    {
        this->slijedBrojeva->attach(this);
        try
        {
            file.open("log.txt");
        }
        catch (const std::exception &e)
        {
            std::cerr << e.what() << '\n';
            throw e;
        }
    }
    ~Zapisnik()
    {
        this->slijedBrojeva->dettach(this);
        if (file.is_open())
            file.close();
    }

    virtual void update()
    {
        if (!file.is_open()) return;

        std::vector<int> numbers = slijedBrojeva->getNumbers();
        std::vector<int>::iterator it = numbers.begin();
        while (it != numbers.end())
        {
            file << *it.base() << " ";
            ++it;
        }

        std::time_t time = std::chrono::system_clock::to_time_t(std::chrono::system_clock::now());
        file << std::ctime(&time);
        file.flush();
    }
};

class IspisiSumu : public Akcija
{
private:
    SlijedBrojeva *slijedBrojeva;

public:
    IspisiSumu(SlijedBrojeva *slijedBrojeva) : slijedBrojeva{slijedBrojeva}
    {
        this->slijedBrojeva->attach(this);
    }
    ~IspisiSumu()
    {
        this->slijedBrojeva->dettach(this);
    }

    virtual void update()
    {
        std::vector<int> numbers = slijedBrojeva->getNumbers();
        std::vector<int>::iterator it = numbers.begin();
        int sum = 0;
        while (it != numbers.end())
        {
            sum += *it.base();
            ++it;
        }
        std::cout << "Suma brojeva: " << sum << std::endl;
    }
};

class IspisiProsjek : public Akcija
{
private:
    SlijedBrojeva *slijedBrojeva;

public:
    IspisiProsjek(SlijedBrojeva *slijedBrojeva) : slijedBrojeva{slijedBrojeva}
    {
        this->slijedBrojeva->attach(this);
    }
    ~IspisiProsjek()
    {
        this->slijedBrojeva->dettach(this);
    }

    virtual void update()
    {
        std::vector<int> numbers = slijedBrojeva->getNumbers();
        std::vector<int>::iterator it = numbers.begin();
        int sum = 0;
        while (it != numbers.end())
        {
            sum += *it.base();
            ++it;
        }
        std::cout << "Prosjek brojeva: " << (double)sum / numbers.size() << std::endl;
    }
};

class IspisiMedijan : public Akcija
{
private:
    SlijedBrojeva *slijedBrojeva;

public:
    IspisiMedijan(SlijedBrojeva *slijedBrojeva) : slijedBrojeva{slijedBrojeva}
    {
        this->slijedBrojeva->attach(this);
    }
    ~IspisiMedijan()
    {
        this->slijedBrojeva->dettach(this);
    }

    virtual void update()
    {
        std::vector<int> numbers = slijedBrojeva->getNumbers();
        sort(numbers.begin(), numbers.end());
        int n = numbers.size();
        double median;

        if (n == 1)
            median = numbers.front();
        else if (n % 2 != 0)
            median = numbers.at(n / 2);
        else
            median = (numbers.at(n / 2) + numbers.at(n / 2 - 1)) / 2.0;

        std::cout << "Medijan brojeva: " << median << std::endl;
    }
};

// Starting point
int main()
{
    SlijedBrojeva *slijedBrojeva = new SlijedBrojeva(new DatotecniIzvor("./02/05-brojevi.txt"));
    Akcija *a1 = new IspisiSumu(slijedBrojeva);
    Akcija *a2 = new IspisiProsjek(slijedBrojeva);
    Akcija *a3 = new IspisiMedijan(slijedBrojeva);
    Akcija *a4 = new Zapisnik(slijedBrojeva);
    slijedBrojeva->kreni();

    return 0;
}