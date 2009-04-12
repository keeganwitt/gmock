package org.gmock.sample

import org.gmock.GMockTestCase

class LRUStrategyTest extends GMockTestCase {

    private testOnAccess(String operation) {
        mock(LinkedList, constructor()) {
            remove("key")
            it << "key"
        }
        play {
            def ls = new LRUStrategy()
            ls."on$operation" "key"
        }
    }

    void testOnPut() {
        testOnAccess("Put")
    }

    void testOnGet() {
        testOnAccess("Get")
    }

    void testGetKeyToRemove() {
        mock(LinkedList, constructor()) {
            first().returns("key")
            remove("key")
        }
        play {
            def ls = new LRUStrategy()
            assertEquals "key", ls.getKeyToRemove()
        }
    }

}
