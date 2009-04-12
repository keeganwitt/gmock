package org.gmock.sample

class RandomStrategy implements Strategy {

    List history = []

    void onGet(String key) {
        onAccess(key)
    }

    void onPut(String key) {
        onAccess(key)
    }

    private onAccess(String key) {
        history << key
    }

    String getKeyToRemove() {
        def rand = new Random()
        def i = rand.nextInt(history.size())
        def key = history[i]
        history -= key
        return key
    }

}
