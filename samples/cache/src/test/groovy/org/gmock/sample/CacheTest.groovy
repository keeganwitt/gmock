package org.gmock.sample

import org.gmock.GMockTestCase
import static org.hamcrest.Matchers.anything

class CacheTest extends GMockTestCase {

    def respository
    def strategy
    Cache cache

    void setUp() {
        respository = mock(Respository)
        strategy = mock(Strategy)
        cache = new Cache(respository, strategy, 3)
    }

    void testGet() {
        respository.get("key").returns("value").once()
        strategy.onAccess("key").times(2)
        play {
            assertEquals "value", cache.get("key")
            assertEquals "value", cache.get("key")
        }
    }

    void testPut() {
        respository.get("key").never()
        strategy.onAccess("key").stub()
        play {
            cache.put "key", 1
            assertEquals 1, cache.get("key")
        }
    }

    void testFlush() {
        respository.put("key", 2)
        strategy.onAccess("key").stub()
        play {
            cache.put "key", 2
            cache.flush()
        }
    }

    void testGetButNotFound() {
        respository.get("not exist").raises(NotFoundException, "not exist")
        strategy.onAccess("not exist")
        play {
            assertNull cache.get("not exist")
        }
    }

    void testGetAndSwapOutDirtyItem() {
        strict {
            loose {
                with(respository) {
                    get("key 1").returns(1)
                    get("key 3").returns(3)
                    get("key 4").returns(4)
                }
            }
            strategy.getKeyToRemove().returns("key 2")
            respository.put("key 2", 2)
        }
        strategy.onAccess(anything()).times(4)
        play {
            assertEquals 1, cache.get("key 1")
            cache.put "key 2", 2
            assertEquals 3, cache.get("key 3")

            assertEquals 4, cache.get("key 4") // "key 2" should be swapped out here
        }
    }

}
