package org.gmock.sample

import org.gmock.GMockTestCase

class LRUStrategyTest extends GMockTestCase {

    void testOnAccess() {
        def list = mock(LinkedList, constructor())
        list.remove("key")
        list.leftShift("key")
        play {
            def ls = new LRUStrategy()
            ls.onAccess "key"
        }
    }

    void testGetKeyToRemove() {
        def list = mock(LinkedList, constructor())
        list.first().returns("key")
        list.remove("key")
        play {
            def ls = new LRUStrategy()
            assertEquals "key", ls.getKeyToRemove()
        }
    }

}
