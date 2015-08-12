package epar.util;

import java.util.Iterator;

public class NEStack<T> extends Stack<T> {

	private T first;
	private Stack<T> rest;

	public NEStack(T first, Stack<T> rest) {
		this.first = first;
		this.rest = rest;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public T getFirst() {
		return first;
	}

	@Override
	public Stack<T> getRest() {
		return rest;
	}

	@Override
	public Iterator<T> iterator() {
		final Stack<T> stack = this;

		return new Iterator<T>() {

			Stack<T> next = stack;

			@Override
			public boolean hasNext() {
				return !next.isEmpty();
			}

			@Override
			public T next() {
				T result = next.getFirst();
				next = next.getRest();
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	@Override
	public int size() {
		return 1 + rest.size();
	}
}
