package org.gmock.sample;

public interface Repository {

    public Object get(String key) throws NotFoundException;

    public void put(String key, Object value);

}
