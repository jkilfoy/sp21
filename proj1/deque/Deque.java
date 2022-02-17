package deque;

public interface Deque<T> {

    default boolean isEmpty() {
        return size() == 0;
    }

    /** Adds an item to the front of the deque */
    void addFirst(T item);

    /** Adds an item to the back of the deque */
    void addLast(T item);

    /** Returns the number of items in the deque */
    int size();

    /** Prints the items in the deque from first to last, separated by a space */
    void printDeque();

    /** Removes and returns the item at the front of the deque.
     * If no such item exists, returns null */
    T removeFirst();

    /** Removes and returns the item at the back of the deque.
     * If no such item exists, returns null */
    T removeLast();

    /** Gets the item at the given index. If no such item exists, returns null. */
    T get(int index);
    
}
