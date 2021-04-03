#include <iostream>
#include <vector>
#include <random>

// Classes for generating the numbers
class NumberGenerator
{
public:
    virtual std::vector<int> generateNumbers() = 0;
};

class SequentiallNumberGenerator : public NumberGenerator
{
private:
    int lower_bound;
    int upper_bound;
    int step;

public:
    SequentiallNumberGenerator(int lb, int up, int step)
        : lower_bound{lb}, upper_bound{up}, step{step} {}

    int getLowerBound() { return this->lower_bound; }
    int getUpperBound() { return this->upper_bound; }
    int getStep() { return this->step; }
    void setLowerBound(int lb) { this->lower_bound = lb; }
    void setUpperBound(int ub) { this->upper_bound = ub; }
    void setStep(int step) { this->step = step; }

    virtual std::vector<int> generateNumbers()
    {
        std::vector<int> result;
        for (int i = this->lower_bound; i <= this->upper_bound; i += this->step)
        {
            result.push_back(i);
        }
        return result;
    }
};

class RandomNumberGenerator : public NumberGenerator
{
private:
    double mean;
    double standard_deviation;
    int n;

public:
    RandomNumberGenerator(double mean, double sd, int n)
        : mean{mean}, standard_deviation{sd}, n{n} {}

    double getMean() { return this->mean; }
    double getStandardDeviation() { return this->standard_deviation; }
    int getNumberOfElements() { return this->n; }
    void setMean(double mean) { this->mean = mean; }
    void setStandardDeviation(double sd) { this->standard_deviation = sd; }
    void setNumberOfElements(int n) { this->n = n; }

    virtual std::vector<int> generateNumbers()
    {
        // random device class instance, source of 'true' randomness for initializing random seed
        std::random_device rd;
        // Mersenne twister PRNG, initialized with seed from previous random device instance
        std::mt19937 gen(rd());
        // instance of class std::normal_distribution with specific mean and stddev
        std::normal_distribution<float> nd(this->mean, this->standard_deviation);
        
        std::vector<int> result;
        for (int i = 0; i < this->n; i++)
        {
            // get random number with normal distribution using gen as random source
            float sample = nd(gen);
            result.push_back((int) sample);
        }
        return result;
    }
};

class FibonacciNumberGenerator : public NumberGenerator
{
private:
    int n;

public:
    FibonacciNumberGenerator(int n) : n{n} {}

    int getNumberOfElements() { return this->n; }
    void setNumberOfElements(int n) { this->n = n; }

    virtual std::vector<int> generateNumbers()
    {
        if (this->n == 1)
            return std::vector<int>{1};
        std::vector<int> result{1, 1};
        int a = 1, b = 1;
        for (int i = 2; i < this->n; i++)
        {
            int next = a + b;
            result.push_back(next);
            a = b;
            b = next;
        }
        return result;
    }
};

// Primary class
class DistributionTester
{
private:
    NumberGenerator *numberGenerator;
};

// Starting point
int main()
{
    NumberGenerator *fng = new FibonacciNumberGenerator(10);
    std::vector<int> res1 = fng->generateNumbers();
    NumberGenerator *sng = new SequentiallNumberGenerator(20, 50, 4);
    std::vector<int> res2 = sng->generateNumbers();
    NumberGenerator *rng = new RandomNumberGenerator(10, 10, 10);
    std::vector<int> res3 = rng->generateNumbers();

    return 0;
}