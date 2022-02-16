package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private Node<T> sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node<>(null, null, null);
        sentinel.setPrev(sentinel);
        sentinel.setNext(sentinel);
        size = 0;
    }

    public void addFirst(T item) {
        Node<T> oldFirst = sentinel.getNext();
        Node<T> newNode = new Node<>(item, sentinel, oldFirst);
        sentinel.setNext(newNode);
        oldFirst.setPrev(newNode);
        size++;
    }

    public void addLast(T item) {
        Node<T> oldLast = sentinel.getPrev();
        Node<T> newNode = new Node<>(item, oldLast, sentinel);
        sentinel.setPrev(newNode);
        oldLast.setNext(newNode);
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        StringBuilder result = new StringBuilder();
        String prefix = "";
        for (T el : this) {
            result.append(prefix).append(el.toString());
            prefix = " ";
        }
        System.out.println(result);
    }

    public T removeFirst() {
        if (isEmpty()) return null;
        Node<T> node = sentinel.getNext();
        Node<T> newFirst = node.getNext();
        sentinel.setNext(newFirst);
        newFirst.setPrev(sentinel);
        size--;
        return node.getValue();
    }

    public T removeLast() {
        if (isEmpty()) return null;
        Node<T> node = sentinel.getPrev();
        Node<T> newLast = node.getPrev();
        sentinel.setPrev(newLast);
        newLast.setNext(sentinel);
        size--;
        return node.getValue();
    }

    public T get(int index) {
        Node<T> node = sentinel;
        for (int i = 0; i < size; i++) {
            node = node.getNext();
            if (i == index) {
                return node.getValue();
            }
        }
        return null;
    }

    public T getRecursive(int index) {
        if (isEmpty() || index < 0) return null;
        return getRecursiveHelper(index, sentinel.getNext()).getValue();
    }

    private Node<T> getRecursiveHelper(int index, Node<T> curr) {
        if (index == 0) return curr;
        return getRecursiveHelper(--index, curr.getNext());
    }

    public Iterator<T> iterator() {
        return new DequeIterator<>(this);
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) return false;
        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
        if (size() != other.size()) return false;
        for (int i = 0; i < size(); i++) {
            if (!get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

}
