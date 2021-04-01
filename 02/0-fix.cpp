#include <iostream>
#include <assert.h>
#include <stdlib.h>
#include <list>

struct Point
{
    int x;
    int y;
};

class Shape
{
public:
    virtual void draw() = 0;
    virtual void move(Point *) = 0;
};

class Circle : public Shape
{
private:
    double side;
    Point center;

public:
    virtual void draw()
    {
        std::cerr << "in drawCircle\n";
    }
    virtual void move(Point *transition)
    {
        center.x = center.x + transition->x;
        center.y = center.y + transition->y;
        std::cerr << "in moveCircle\n";
    }
};

class Square : public Shape
{
private:
    double side;
    Point center;

public:
    virtual void draw()
    {
        std::cerr << "in drawSquare\n";
    }
    virtual void move(Point *transition)
    {
        center.x = center.x + transition->x;
        center.y = center.y + transition->y;
        std::cerr << "in moveSquare\n";
    }
};

class Rhomb : public Shape
{
private:
    double side;
    Point center;

public:
    virtual void draw()
    {
        std::cerr << "in drawRhomb\n";
    }
    virtual void move(Point *transition)
    {
        center.x = center.x + transition->x;
        center.y = center.y + transition->y;
        std::cerr << "in moveRhomb\n";
    }
};

void drawShapes(const std::list<Shape *> &fig)
{
    std::list<Shape *>::const_iterator it;
    for (it = fig.begin(); it != fig.end(); ++it)
    {
        (*it)->draw();
    }
}

void moveShapes(const std::list<Shape *> &fig, Point *translation)
{
    std::list<Shape *>::const_iterator it;
    for (it = fig.begin(); it != fig.end(); ++it)
    {
        (*it)->move(translation);
    }
}

int main()
{
    std::list<Shape *> shapes;
    shapes.push_back(new Circle());
    shapes.push_back(new Square());
    shapes.push_back(new Square());
    shapes.push_back(new Circle());
    shapes.push_back(new Rhomb());

    drawShapes(shapes);

    Point *point = new Point();
    point->x = 1;
    point->y = -4;
    moveShapes(shapes, point);
}

/*
Ovo rjsešenje rješava probleme prethodnog.
Methode draw i move se apstrahiraju te se koriste preko
zajedničkog sučelja definiranog u matičnom razredu Shape.
Sada kada se stvara nova implementacija baznog razreda nema
potrebe prilagođavati postojeće metode kako bi podržale dodani 
razred, nego je jedino potrebno implementirati sve metode sučelja
razreda Shape u tom novom razredu. Time je promjene potrebno
unijeti samo na jednom mjestu.
*/