package deque;

public class Node<T> {

    private T value;
    private Node<T> prev;
    private Node<T> next;

    public Node(T tempValue, Node<T> tempPrev, Node<T> tempNext) {
        value = tempValue;
        prev = tempPrev;
        next = tempNext;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T tempValue) {
        value = tempValue;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> tempPrev) {
        prev = tempPrev;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> tempNext) {
        next = tempNext;
    }

}
