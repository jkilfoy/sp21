package deque;

public class Node<T> {

    private T value;
    private Node<T> prev;
    private Node<T> next;

    public Node(T _value, Node<T> _prev, Node<T> _next) {
        value = _value;
        prev = _prev;
        next = _next;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T _value) {
        value = _value;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> _prev) {
        prev = _prev;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> _next) {
        next = _next;
    }

}
