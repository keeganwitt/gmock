package org.gmock.sample;

public interface Strategy {

    public void onGet(String key);

    public void onPut(String key);

    public String getKeyToRemove();

}
