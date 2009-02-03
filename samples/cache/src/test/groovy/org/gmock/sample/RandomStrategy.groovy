package org.gmock.sample

import org.gmock.GMockTestCase

class RandomStrategyTest extends GMockTestCase {

    RandomStrategy rs

    void setUp() {
        rs = new RandomStrategy()
    }

    void testOnAccess() {
        rs.onAccess("key 1")
        rs.onAccess("key 2")
        rs.onAccess("key 3")
        rs.onAccess("key 1")
        assertEquals(["key 1", "key 2", "key 3", "key 1"], rs.history)
    }

    void testGetKeyToRemove() {
        mock(Random, constructor()).nextInt(5).returns(2)
        play {
            ["key 1", "key 2", "key 1", "key 3", "key 2"].each {
                rs.onAccess it
            }
            assertEquals "key 1", rs.getKeyToRemove()
            assertEquals(["key 2", "key 3", "key 2"], rs.history)
        }
    }

}
