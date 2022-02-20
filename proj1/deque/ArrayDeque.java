package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>  {

    private final static int INITIAL_SIZE = 8;
    private final static double INCREASE_SIZE_FACTOR = 2.0;     // double array size when increasing space
    private final static double DECREASE_SIZE_FACTOR = 4.0;     // remove empty space when less than 1/4 full
    private final static int DECREASE_MIN = 16;       // Only decrease if size is at least this much

    private T[] deque;
    private int size;
    private int firstIndex;
    private int lastIndex;

    public ArrayDeque() {
        deque = (T[]) new Object[INITIAL_SIZE];
        size = 0;
        firstIndex = 0;
        lastIndex = 0;
    }

    /**
     * Helper method for cycling out-of-bounds indices to the corresponding index in deque
     * @param index an index
     * @return the index cycled around the deque's length
     */
    private int cycle(int index) {
        int newIndex = index % deque.length;
        if (newIndex < 0) newIndex += deque.length;
        return newIndex;
    }

    @Override
    public void addFirst(T item) {
        if (size == deque.length) {
            resize(ResizeType.INCREASE);
        }
        if (!isEmpty()) {
            // Decrease and cycle firstIndex only if the array is non-empty
            firstIndex = cycle(firstIndex-1);
        }
        deque[firstIndex] = item;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == deque.length) {
            resize(ResizeType.INCREASE);
        }
        if (!isEmpty()) {
            // Increase and cycle lastIndex only if the array is non-empty
            lastIndex = cycle(lastIndex+1);
        }
        deque[lastIndex] = item;
        size++;
    }

    /**
     * Resizes the deque array. The deque cannot be empty when resizing, or else the first/last
     *  pointers will de-synchronize (ie cross each other)
     * @param resizeType Whether to increase of ddcrease the size
     */
    private void resize(ResizeType resizeType) {
        assert !isEmpty() : "Cannot resize when empty";
        int newLength = 0;
        switch (resizeType) {
            case DECREASE -> newLength = size;
            case INCREASE -> newLength = (int) (size * INCREASE_SIZE_FACTOR);
        }

        // Make an array with the new length, and Starting from firstIndex,
        // fill the front of the new array with the elements in the deque
        T[] newArray = (T[]) new Object[newLength];
        for (int i = 0; i < size; i++) {
            newArray[i] = deque[cycle(firstIndex + i)];
        }

        deque = newArray;
        firstIndex = 0;
        lastIndex = size - 1;
    }

    private enum ResizeType {
        INCREASE, DECREASE;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (T el : this) {
            sb.append(prefix).append(el.toString());
            prefix = " ";
        }
        System.out.println(sb);
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) return null;
        if (    size >= DECREASE_MIN
            &&  size - 1 < deque.length / DECREASE_SIZE_FACTOR) {
            resize(ResizeType.DECREASE);
        }
        T item = deque[firstIndex];
        size--;
        if (!isEmpty()) {
            // increase and cycle firstIndex only if the array is non-empty
            firstIndex = cycle(firstIndex+1);
        }
        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) return null;
        if (    size >= DECREASE_MIN
            &&  size-1 < deque.length/DECREASE_SIZE_FACTOR) {
            resize(ResizeType.DECREASE);
        }
        T item = deque[lastIndex];
        size--;
        if (!isEmpty()) {
            // decrease and cycle lastIndex only if the array is non-empty
            lastIndex = cycle(lastIndex-1);
        }
        return item;
    }

    @Override
    public T get(int index) {
        if (isEmpty()) return null;
        return deque[cycle(index + firstIndex)];
    }

    @Override
    public Iterator<T> iterator() {
        return new DequeIterator<>(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) return false;
        Deque<T> other = (Deque<T>) o;
        if (size() != other.size()) return false;
        for (int i = 0; i < size(); i++) {
            if (!get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }
}
