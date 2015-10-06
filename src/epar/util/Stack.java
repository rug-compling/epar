package epar.util;

import java.util.NoSuchElementException;

public abstract class Stack<T> implements Iterable<T> {

    public Stack<T> push(T element) {
        return new NEStack<>(element, this);
    }

    public abstract boolean isEmpty();

    public abstract T getFirst() throws NoSuchElementException;

    public abstract Stack<T> getRest() throws NoSuchElementException;

    public T get(int index, T defaultValue) {
        if (isEmpty()) {
            return defaultValue;
        }

        if (index == 0) {
            return getFirst();
        }

        return getRest().get(index - 1, defaultValue);
    }

    public abstract int size();

}
