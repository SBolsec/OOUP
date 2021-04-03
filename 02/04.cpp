#include <iostream>
#include <vector>
#include <random>
#include <algorithm>

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
            result.push_back((int)sample);
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

// Classes for calculating the percentile
class Percentile
{
protected:
    std::vector<int> numbers;
public:
    virtual void setNumbers(std::vector<int> numbers) { this->numbers = numbers; }
    virtual void setNumbersSorted(std::vector<int> numbers) {
        this->numbers = numbers;
        sort(this->numbers.begin(), this->numbers.end());
    }
    virtual double calculate(int percentile) = 0;
};

class NearestRankPercentile : public Percentile
{
public:
    virtual double calculate(int percentile)
    {
        double n_p = percentile * this->numbers.size() / 100 + 0.5;
        return this->numbers.at((size_t)n_p);
    }
};

class LinearInterpolationPercentile : public Percentile
{
public:
    virtual double calculate(int percentile)
    {
        size_t n = this->numbers.size();

        // calculate the percent ranks
        std::vector<double> percent_rank;
        for (size_t i = 1; i <= n; i++)
        {
            double res = 100 * (i - 0.5) / n;
            percent_rank.push_back(res);
        }

        // find the percentile rank

        // is P < P1?
        if (percentile < percent_rank.front())
        {
            return (double)this->numbers.front();
        }
        // is P > Pn?
        if (percentile > percent_rank.back())
        {
            return (double)this->numbers.back();
        }
        // is there a percent rank equal to P?
        std::vector<double>::iterator it = percent_rank.begin();
        for (size_t i = 0; it != percent_rank.end(); it++)
        {
            if (percentile == *it.base())
            {
                return (double)this->numbers.at(i);
            }
        }
        // else calculate the value
        int k = -1;
        for (size_t i = 0; i < percent_rank.size(); i++)
        {
            if (percent_rank.at(i) > percentile)
            {
                k = i - 1;
                break;
            }
        }
        return (double)(this->numbers.at(k) + n * (percentile - percent_rank.at(k)) * (this->numbers.at(k + 1) - this->numbers.at(k)) / 100);
    }
};

// Primary class
class DistributionTester
{
private:
    NumberGenerator *numberGenerator;
    Percentile *percentile;

public:
    DistributionTester(NumberGenerator *ng, Percentile *p) : numberGenerator{ng}, percentile{p} {}

    void setNumberGenerator(NumberGenerator *numberGenerator)
    {
        this->numberGenerator = numberGenerator;
    }
    void setPercentile(Percentile *percentile)
    {
        this->percentile = percentile;
    }

    void testDistribution()
    {
        std::vector<int> numbers = numberGenerator->generateNumbers();
        percentile->setNumbersSorted(numbers);
        for (int i = 10; i <= 90; i += 10)
        {
            double res = percentile->calculate(i);
            std::cout << i << ". percentile: " << res << std::endl;
        }
    }
};

// Starting point
int main()
{
    NumberGenerator *fng = new FibonacciNumberGenerator(10);
    NumberGenerator *sng = new SequentiallNumberGenerator(15, 50, 5);
    NumberGenerator *rng = new RandomNumberGenerator(10, 10, 10);
    Percentile *p1 = new NearestRankPercentile();
    Percentile *p2 = new LinearInterpolationPercentile();

    DistributionTester *dt = new DistributionTester(sng, p1);
    std::cout << "== Sequential, nearest ==\n";
    dt->testDistribution();

    std::cout << "== Sequential, linear interpolation ==\n";
    dt->setPercentile(p2);
    dt->testDistribution();

    std::cout << "== Fibonacci, nearest ==\n";
    dt->setNumberGenerator(fng);
    dt->setPercentile(p1);
    dt->testDistribution();

    std::cout << "== Fibonacci, linear interpolation ==\n";
    dt->setPercentile(p2);
    dt->testDistribution();

    std::cout << "== Random, nearest ==\n";
    dt->setNumberGenerator(rng);
    dt->setPercentile(p1);
    dt->testDistribution();

    std::cout << "== Random, linear interpolation ==\n";
    dt->setPercentile(p2);
    dt->testDistribution();

    return 0;
}