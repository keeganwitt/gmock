package org.gmock.sample;

public class ExpiringCache extends AbstractCache {

    private Cache cache;

    private Strategy strategy;

    private int capacity;

    public ExpiringCache(Cache cache, Strategy strategy, int capacity) {
        super(cache.getUnderlyingRepository());
        this.cache = cache;
        this.strategy = strategy;
        this.capacity = capacity;
    }

    public Object getFromCache(String key) {
        return cache.getFromCache(key);
    }

    public void putToCache(String key, Object value) {
        checkSize(key);
        cache.putToCache(key, value);
    }

    public void removeFromCache(String key) {
        cache.removeFromCache(key);
    }

    public void flush() {
        cache.flush();
    }

    public int getSize() {
        return cache.getSize();
    }

    public void put(String key, Object value) {
        checkSize(key);
        cache.put(key, value);
        strategy.onPut(key);
    }

    public Object get(String key) throws NotFoundException {
        Object value = super.get(key);
        strategy.onGet(key);
        return value;
    }

    private void checkSize(String key) {
        if (null == getFromCache(key)) {
            while (getSize() >= capacity) {
                String keyToRemove = strategy.getKeyToRemove();
                removeFromCache(keyToRemove);
            }
        }
    }

}
