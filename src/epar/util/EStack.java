package epar.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class EStack<T> extends Stack<T> {

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Stack<T> push(T element) {
        return new NEStack<>(element, this);
    }

    @Override
    public T getFirst() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    @Override
    public Stack<T> getRest() throws NoSuchElementException {
        throw new NoSuchElementException();
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public int size() {
        return 0;
    }

}
