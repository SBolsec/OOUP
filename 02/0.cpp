#include <iostream>
#include <assert.h>
#include <stdlib.h>

struct Point
{
    int x;
    int y;
};
struct Shape
{
    enum EType
    {
        circle,
        square,
        rhomb
    };
    EType type_;
};
struct Circle
{
    Shape::EType type_;
    double radius_;
    Point center_;
};
struct Square
{
    Shape::EType type_;
    double side_;
    Point center_;
};
struct Rhomb
{
    Shape::EType type_;
    double side_;
    Point center_;
};
void drawSquare(struct Square *)
{
    std::cerr << "in drawSquare\n";
}
void drawCircle(struct Circle *)
{
    std::cerr << "in drawCircle\n";
}
void drawRhomb(struct Rhomb *)
{
    std::cerr << "in drawRhomb\n";
}
void drawShapes(Shape **shapes, int n)
{
    for (int i = 0; i < n; ++i)
    {
        struct Shape *s = shapes[i];
        switch (s->type_)
        {
        case Shape::square:
            drawSquare((struct Square *)s);
            break;
        case Shape::circle:
            drawCircle((struct Circle *)s);
            break;
        case Shape::rhomb:
            drawRhomb((struct Rhomb *)s);
            break;
        default:
            assert(0);
            exit(0);
        }
    }
}
void moveSquare(struct Square *square, Point *translation)
{
    square->center_.x = square->center_.x + translation->x;
    square->center_.y = square->center_.y + translation->y;
    std::cerr << "in moveSquare\n";
}
void moveCircle(struct Circle *circle, Point *translation)
{
    circle->center_.x = circle->center_.x + translation->x;
    circle->center_.y = circle->center_.y + translation->y;
    std::cerr << "in moveCircle\n";
}
void moveRhomb(struct Rhomb *rhomb, Point *translation)
{
    rhomb->center_.x = rhomb->center_.x + translation->x;
    rhomb->center_.y = rhomb->center_.y + translation->y;
    std::cerr << "in moveRhomb\n";
}
void moveShapes(Shape **shapes, int n, Point *translation)
{
    for (int i = 0; i < n; i++)
    {
        struct Shape *s = shapes[i];
        switch (s->type_)
        {
        case Shape::square:
            moveSquare((struct Square *)s, translation);
            break;
        case Shape::circle:
            moveCircle((struct Circle *)s, translation);
            break;
        case Shape::rhomb:
            moveRhomb((struct Rhomb *)s, translation);
            break;
        default:
            assert(0);
            exit(0);
        }
    }
}
int main()
{
    Shape *shapes[4];
    shapes[0] = (Shape *)new Circle;
    shapes[0]->type_ = Shape::circle;
    shapes[1] = (Shape *)new Square;
    shapes[1]->type_ = Shape::square;
    shapes[2] = (Shape *)new Square;
    shapes[2]->type_ = Shape::square;
    shapes[3] = (Shape *)new Circle;
    shapes[3]->type_ = Shape::circle;
    shapes[4] = (Shape *)new Rhomb;
    shapes[4]->type_ = Shape::rhomb;

    drawShapes(shapes, 5);

    Point *point = new Point();
    point->x = 1;
    point->y = -4;
    moveShapes(shapes, 5, point);
}