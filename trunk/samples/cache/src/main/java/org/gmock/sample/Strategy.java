package org.gmock.sample;

public interface Strategy {

    public void onAccess(String key);

    public String getKeyToRemove();

}
