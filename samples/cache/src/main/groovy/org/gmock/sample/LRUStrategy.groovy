package org.gmock.sample

class LRUStrategy implements Strategy {

    List history = new LinkedList()

    void onAccess(String key) {
        history.remove key
        history << key
    }

    String getKeyToRemove() {
        def key = history.first()
        history.remove key
        return key
    }

}
