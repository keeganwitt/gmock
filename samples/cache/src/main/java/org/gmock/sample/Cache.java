package org.gmock.sample;

public interface Cache extends Repository {

    public Object getFromCache(String key);

    public void putToCache(String key, Object value);

    public void removeFromCache(String key);

    public void flush();

    public int getSize();

    public Repository getUnderlyingRepository();

}
