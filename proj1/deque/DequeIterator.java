package deque;

import java.util.Iterator;

public class DequeIterator<T> implements Iterator<T> {

    private final Deque<T> deque;
    private int index;

    public DequeIterator(Deque<T> tempDeque) {
        deque = tempDeque;
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return deque.get(index) != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            return null;
        }
        T item = deque.get(index);
        index++;
        return item;
    }
}
