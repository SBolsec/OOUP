package hr.ooup.lab3.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ListIterator<T> implements Iterator<T> {
    private List<T> list;
    private int current;

    public ListIterator(List<T> list) {
        this.list = list;
    }

    @Override
    public boolean hasNext() {
        return current < list.size();
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        return list.get(current++);
    }
}
