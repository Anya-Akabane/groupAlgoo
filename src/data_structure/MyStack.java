package data_structure;

public class MyStack<T> {
    private MyList<T> list;

    public MyStack() {
        list = new MyList<>();
    }

    public void push(T value) {
        list.add(value);
    }

    public T pop() {
        if (list.size() == 0) throw new RuntimeException("Stack is empty");
        T value = list.get(list.size() - 1);
        list.remove(value);
        return value;
    }

    public boolean isEmpty() {
        return list.size() == 0;
    }
}