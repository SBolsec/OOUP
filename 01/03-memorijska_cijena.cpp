#include <iostream>

class CoolClass
{
public:
    virtual void set(int x) { x_ = x; };
    virtual int get() { return x_; };

private:
    int x_;
};

class PlainOldClass
{
public:
    void set(int x) { x_ = x; };
    int get() { return x_; };

private:
    int x_;
};

int main()
{
    std::cout << "Sizeof PlainOldClass: " << sizeof(PlainOldClass) << std::endl;
    std::cout << "Sizeof CoolClass: " << sizeof(CoolClass) << std::endl;

    return 0;
}

/* 
x86: PlainOldClass vrati 4, a CoolClass vrati 8
x64: PlainOldClass vrati 4, a CoolClass vrati 16

Veličina PlainOldClass-a je 4 zato jer se samo sprema
privatna varijabla x_ koja je tipa "int" koji zauzima
4 okteta. Ovo vrijedi i kod x84 i x64.

Budući da CoolClass ima virtualne metode (potrebna barem
jedna, ako ih ima više rezultat je isti), nakon 
kompajliranja imati će pokazivač na tablicu virtualnih metoda.

x64: Pokazivač na tablicu virtualnih funkcija zauzima
8 okteta, te će se "padding" i "allignment" računati 
prema tome. Sljedeće na red dolazi privatna varijabla 
x_ koja je "int" i zauzima 4 okteta. Ovo bi nas sada navelo
da je veličina CoolClass-a 12 okteta, no zbog veličine 
pokazivača na tablicu virtualnih funkcija nakon varijable x_
dolazi "padding" kako bi se ispunio prostor kako bi sve bilo
8 oktetno "alignano", te tako završavamo s veličinom od 
16 okteta.

x86: Pokazivač na tablicu virtualnih funkcija zauzima
4 okteta, nakon toga dolazi privatna varijabla x_ koja
je "int" te također zauzima 4 okteta. U ovom slučaju 
sve je već 4 oktetno "alignano" pa nema potrebe za 
"paddingom", te tako završavamo s veličinom od 8 okteta.
*/