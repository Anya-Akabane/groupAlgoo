package data_structure;


public class MyMap<K, V> {
    private MyList<MyEntry<K, V>> entries;

    public MyMap() {
        entries = new MyList<>();
    }

    public void put(K key, V value) {
        for (int i = 0; i < entries.size(); i++) {
            MyEntry<K, V> entry = entries.get(i);
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }
        entries.add(new MyEntry<>(key, value));
    }

    public V get(K key) {
        for (int i = 0; i < entries.size(); i++) {
            MyEntry<K, V> entry = entries.get(i);
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        for (int i = 0; i < entries.size(); i++) {
            MyEntry<K, V> entry = entries.get(i);
            if (entry.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public void remove(K key) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).key.equals(key)) {
                entries.remove(entries.get(i));
                return;
            }
        }
    }

    public MyList<MyEntry<K, V>> entrySet() {
        return entries;
    }

    public void clear() {
        entries = new MyList<>();
    }

    public void putAll(MyMap<K, V> other) {
        MyList<MyEntry<K, V>> otherEntries = other.entrySet();
        for (int i = 0; i < otherEntries.size(); i++) {
            MyEntry<K, V> entry = otherEntries.get(i);
            this.put(entry.key, entry.value);
        }
    }
}
