package org.gmock.sample;

public interface Respository {

    public Object get(String key) throws NotFoundException;

    public void put(String key, Object value);

}
