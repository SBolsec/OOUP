#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h> 

typedef void const* (*PTRVOID) ();

typedef struct {
    PTRVOID *vtf;
    int lower_bound;
    int upper_bound;
} Unary_Function;

typedef double (*PTRDOUBLE) (Unary_Function*, double);
typedef bool (*PTRBOOL) (Unary_Function*, Unary_Function*, double);

double negative_value_at(Unary_Function* fn, double x) {
    PTRDOUBLE function = (PTRDOUBLE) fn->vtf[0];
    return -1 * function(fn, x);
}

void tabulate(Unary_Function *fn) {
    PTRDOUBLE function = (PTRDOUBLE) fn->vtf[0];
    for (int x = fn->lower_bound; x <= fn->upper_bound; x++) {
        printf("f(%d)=%lf\n", x, function(fn, (double) x));
    }
}

bool same_functions_for_ints(Unary_Function *f1, Unary_Function *f2, double tolerance) {
    if (f1->lower_bound != f2->lower_bound) return false;
    if (f1->upper_bound != f2->upper_bound) return false;
    PTRDOUBLE func1 = (PTRDOUBLE) f1->vtf[0];
    PTRDOUBLE func2 = (PTRDOUBLE) f2->vtf[0];
    for (int x = f1->lower_bound; x <= f1->upper_bound; x++) {
        double delta = func1(f1, (double) x) - func2(f2, (double) x);
        if (delta < 0) delta = -delta;
        if (delta > tolerance) return false;
    }
    return true;
}

PTRVOID Unary_Function_vtable[] = {
    (PTRDOUBLE) NULL, // value_at
    (PTRDOUBLE) &negative_value_at,
    (PTRVOID) &tabulate
};

Unary_Function* createUnaryFunction(int lb, int ub) {
    Unary_Function *u = malloc(sizeof(Unary_Function));
    u->vtf = Unary_Function_vtable;
    u->lower_bound = lb;
    u->upper_bound = ub;
    return u;
}

typedef struct {
    PTRVOID *vtf;
    int lower_bound;
    int upper_bound;
} Square;

double Square_value_at(Square* fn, double x) {
    return x*x;
}

PTRVOID Square_vtable[] = {
    (PTRDOUBLE) &Square_value_at,
    (PTRDOUBLE) &negative_value_at,
    (PTRVOID) &tabulate
};

Square* createSquare(int lb, int ub) {
    Square *s = malloc(sizeof(Square));
    s->vtf = Square_vtable;
    s->lower_bound = lb;
    s->upper_bound = ub;
    return s;
}

typedef struct {
    PTRVOID *vtf;
    int lower_bound;
    int upper_bound;
    double a;
    double b;
} Linear;

double Linear_value_at(Linear* fn, double x) {
    return fn->a * x + fn->b;
}

PTRVOID Linear_vtable[] = {
    (PTRDOUBLE) &Linear_value_at,
    (PTRDOUBLE) &negative_value_at,
    (PTRVOID) &tabulate
};

Linear* createLinear(int lb, int ub, double a_coef, double b_coef) {
    Linear *l = malloc(sizeof(Linear));
    l->vtf = Linear_vtable;
    l->lower_bound = lb;
    l->upper_bound = ub;
    l->a = a_coef;
    l->b = b_coef;
    return l;
}

int main(void) {
    Unary_Function *f1 = (Unary_Function *) createSquare(-2, 2);
    f1->vtf[2](f1);
    Unary_Function *f2 = (Unary_Function *) createLinear(-2, 2, 5, -2);
    f2->vtf[2](f2);

    printf("f1==f2: %s\n", same_functions_for_ints(f1, f2, 1E-6) ? "DA" : "NE");
    PTRDOUBLE neg_val = (PTRDOUBLE) f2->vtf[1];
    printf("neg_val f2(1) = %lf\n", neg_val(f2, 1.0));

    free(f1);
    free(f2);

    return 0;
}