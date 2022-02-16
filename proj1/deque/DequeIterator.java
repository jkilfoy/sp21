package deque;

import java.util.Iterator;

public class DequeIterator<T> implements Iterator<T> {

    private final Deque<T> deque;

    public DequeIterator(Deque<T> _deque) {
        deque = _deque;
    }

    @Override
    public boolean hasNext() {
        return deque.get(0) != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            return null;
        }
        return deque.removeFirst();
    }
}
