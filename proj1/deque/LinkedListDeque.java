package deque;

import java.util.Iterator;

public class LinkedListDeque<T> {

    private Node<T> sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node<>(null, null, null);
        sentinel.setPrev(sentinel);
        sentinel.setNext(sentinel);
        size = 0;
    }

    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item, sentinel, sentinel.getNext());
        sentinel.setNext(newNode);
        size++;
    }

    public void addLast(T item) {
        Node<T> newNode = new Node<>(item, sentinel.getPrev(), sentinel);
        sentinel.setPrev(newNode);
        size++;
    }

    public boolean isEmpty() {
        return size != 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {

    }

    public T removeFirst() {
        Node<T> node = sentinel.getNext();
        sentinel.setNext(node.getNext());
        size--;
        return node.getValue();
    }

    public T removeLast() {
        Node<T> node = sentinel.getPrev();
        sentinel.setPrev(node.getPrev());
        size--;
        return node.getValue();
    }

    public T get(int index) {
        Node<T> node = sentinel;
        for (int i = 0; i < size; i++) {
            node = node.getNext();
            if (i == index) {
                return node.getValue()
            }
        }
        return null;
    }

    public Iterator<T> iterator() {

    }

    public boolean equals(Object o) {

    }

}
