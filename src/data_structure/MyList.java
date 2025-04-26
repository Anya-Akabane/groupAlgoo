package data_structure;

public class MyList<T> {
    private Object[] data;
    private int size;

    public MyList() {
        data = new Object[10];
        size = 0;
    }

    public void add(T value) {
        ensureCapacity();
        data[size++] = value;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return (T) data[index];
    }

    public void set(int index, T value) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        data[index] = value;
    }

    public void remove(T value) {
        for (int i = 0; i < size; i++) {
            if (data[i].equals(value)) {
                for (int j = i; j < size - 1; j++) {
                    data[j] = data[j + 1];
                }
                size--;
                return;
            }
        }
    }

    public boolean contains(T value) {
    for (int i = 0; i < size; i++) {
        if (data[i].equals(value)) {
            return true;
        }
    }
    return false;
}


    public int size() {
        return size;
    }

    public void sort(Comparator<T> comparator) {
        // Simple bubble sort for small lists
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                if (comparator.compare((T) data[j], (T) data[j + 1]) > 0) {
                    Object temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;
                }
            }
        }
    }

    private void ensureCapacity() {
        if (size >= data.length) {
            Object[] newData = new Object[data.length * 2];
            for (int i = 0; i < data.length; i++) {
                newData[i] = data[i];
            }
            data = newData;
        }
    }

    

    public interface Comparator<T> {
        int compare(T o1, T o2);
    }
}