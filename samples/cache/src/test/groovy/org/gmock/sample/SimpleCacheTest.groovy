package org.gmock.sample

import org.gmock.GMockTestCase

class SimpleCacheTest extends GMockTestCase {

    Repository repository
    Cache cache

    void setUp() {
        repository = mock(Repository)
        cache = new SimpleCache(repository)
    }

    void testGet() {
        repository.get("key").returns("value").once()
        play {
            2.times { assertEquals "value", cache.get("key") }
        }
    }

    void testPut() {
        play {
            cache.put "key", 1
            assertEquals 1, cache.get("key")
        }
    }

    void testGetNotFound() {
        repository.get("not exists").raises(NotFoundException, "not exists")
        play {
            shouldFail(NotFoundException) {
                cache.get("not exists")
            }
        }
    }

    void testGetUnderlyingRepository() {
        assertSame repository, cache.underlyingRepository
    }

    void testGetFromAndPutToCache() {
        play {
            assertNull cache.getFromCache("key")

            cache.putToCache "key", 1
            assertEquals 1, cache.getFromCache("key")

            cache.putToCache "key", 2
            assertEquals 2, cache.getFromCache("key")

            assertNull cache.getFromCache("not exists")
        }
    }

    void testGetSize() {
        play {
            assertEquals 0, cache.size

            3.times { cache.putToCache "key 1", it  }
            assertEquals 1, cache.size

            cache.putToCache "key 2", 0
            assertEquals 2, cache.size
        }
    }

    void testRemoveFromCache() {
        repository.put "key 2", 2
        play {
            cache.putToCache "key 1", 1
            cache.put "key 2", 2
            assertEquals 2, cache.size

            cache.removeFromCache "key 1"
            cache.removeFromCache "key 2"
            assertEquals 0, cache.size
        }
    }

    void testRemoveFromCacheNotFound() {
        play {
            cache.removeFromCache "key"
        }
    }

    void testFlush() {
        repository.put "key 2", 2
        play {
            cache.putToCache "key 1", 1
            cache.put "key 2", 2
            assertEquals 2, cache.size

            cache.flush()
            assertEquals 2, cache.size
        }
    }

}
