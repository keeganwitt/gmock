package org.gmock.sample

import org.gmock.GMockTestCase

class LRUStrategyTest extends GMockTestCase {

    void testOnAccess() {
        mock(LinkedList, constructor()) {
            remove("key")
            leftShift("key")
        }
        play {
            def ls = new LRUStrategy()
            ls.onAccess "key"
        }
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
