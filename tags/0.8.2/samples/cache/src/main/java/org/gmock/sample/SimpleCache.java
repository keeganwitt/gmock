package org.gmock.sample;

import java.util.Map;
import java.util.HashMap;

public class SimpleCache extends AbstractCache {

    private Map<String, Item> cache = new HashMap<String, Item>();

    public SimpleCache(Repository repository) {
        super(repository);
    }

    public void put(String key, Object value) {
        cache.put(key, new Item(value, true));
    }

    public Object getFromCache(String key) {
        Item item = cache.get(key);
        return null == item ? null : item.value;
    }

    public void putToCache(String key, Object value) {
        cache.put(key, new Item(value, false));
    }

    public void removeFromCache(String key) {
        writeBack(key);
        cache.remove(key);
    }

    public void flush() {
        for (String key : cache.keySet()) {
            writeBack(key);
        }
    }

    public int getSize() {
        return cache.size();
    }

    private void writeBack(String key) {
        Item item = cache.get(key);
        if (null != item && item.dirty) {
            repository.put(key, item.value);
            item.dirty = false;
        }
    }

    private static class Item {

        Object value;

        boolean dirty;

        Item(Object value, boolean dirty) {
            this.value = value;
            this.dirty = dirty;
        }

    }

}
