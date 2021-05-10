package hr.ooup.lab3.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListRangeIterator<T> implements Iterator<T> {
    private Iterator<T> original;
    private int leftToTake;

    public ListRangeIterator(Iterator<T> original, int index1, int index2) {
        this.original = original;
        this.leftToTake = index2 - index1;

        // skip to index1
        while (index1 > 0 && original.hasNext()) {
            original.next();
            index1--;
        }
    }

    @Override
    public boolean hasNext() {
        return leftToTake > 0 && original.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        return original.next();
    }
}
