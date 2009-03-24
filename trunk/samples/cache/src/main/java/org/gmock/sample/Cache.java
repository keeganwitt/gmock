package org.gmock.sample;

import java.util.HashMap;
import java.util.Map;

public class Cache implements Respository {

    private Respository respository;

    private Strategy strategy;

    private int capacity;

    private Map<String, Item> cache;

    public Cache(Respository respository, Strategy strategy, int capacity) {
        this.respository = respository;
        this.strategy = strategy;
        this.capacity = capacity;
        cache = new HashMap<String, Item>();
    }

    public Object get(String key) {
        Object value;
        if (cache.containsKey(key)) {
            value = cache.get(key).value;
        } else {
            try {
                value = respository.get(key);
            } catch (NotFoundException e) {
                value = null;
            }
            putValueToCacheMap(key, value, false);
        }
        strategy.onAccess(key);
        return value;
    }

    public void put(String key, Object value) {
        putValueToCacheMap(key, value, true);
        strategy.onAccess(key);
    }

    public void flush() {
        for (Map.Entry<String, Item> entry : cache.entrySet()) {
            String key = entry.getKey();
            Item item = entry.getValue();
            putBackToRespository(key, item);
        }
    }

    private void putValueToCacheMap(String key, Object value, boolean dirty) {
        boolean newKey = !cache.containsKey(key);
        if (newKey && cache.size() >= capacity) {
            String keyToRemove = strategy.getKeyToRemove();
            Item itemToRemove = cache.get(keyToRemove);
            putBackToRespository(keyToRemove, itemToRemove);
            cache.remove(keyToRemove);
        }

        cache.put(key, new Item(value, dirty));
    }

    private void putBackToRespository(String key, Item item) {
        if (item.dirty) {
            respository.put(key, item.value);
            item.dirty = false;
        }
    }

    private class Item {

        public Object value;

        public boolean dirty;

        public Item(Object value, boolean dirty) {
            this.value = value;
            this.dirty = dirty;
        }

    }

}
