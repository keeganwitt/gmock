package org.gmock.sample

import org.gmock.GMockTestCase

class RandomStrategyTest extends GMockTestCase {

    RandomStrategy rs

    void setUp() {
        rs = new RandomStrategy()
    }

    void testOnGetAndOnPut() {
        rs.onGet("key 1")
        rs.onPut("key 2")
        rs.onPut("key 3")
        rs.onGet("key 1")
        assertEquals(["key 1", "key 2", "key 3", "key 1"], rs.history)
    }

    void testGetKeyToRemove() {
        mock(Random, constructor()).nextInt(5).returns(2)
        play {
            ["key 1", "key 2", "key 1", "key 3", "key 2"].each {
                rs.onGet it
            }
            assertEquals "key 1", rs.getKeyToRemove()
            assertEquals(["key 2", "key 3", "key 2"], rs.history)
        }
    }

}
