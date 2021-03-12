#include <iostream>

class B
{
public:
    virtual int prva() = 0;
    virtual int druga(int) = 0;
};

class D : public B
{
public:
    virtual int prva() { return 42; }
    virtual int druga(int x) { return prva() + x; }
};

typedef int (*PFUN1)(B *);
typedef int (*PFUN2)(B *, int);

void function(B *pb)
{
    void **vptr = *(void ***)pb;

    PFUN1 f1 = (PFUN1)vptr[0];
    PFUN2 f2 = (PFUN2)vptr[1];

    std::cout << "Prva: " << f1(pb) << std::endl;
    std::cout << "Druga: " << f2(pb, 10) << std::endl;
}

int main()
{
    B *pb = new D();
    function(pb);
    return 0;
}