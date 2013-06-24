package org.gmock.sample

import org.gmock.GMockTestCase

class ExpiringCacheTest extends GMockTestCase {

    Repository repository
    Cache mockCache
    Strategy strategy
    Cache cache

    void setUp() {
        repository = mock(Repository)
        mockCache = mock(Cache)
        strategy = mock(Strategy)
        mockCache.underlyingRepository.returns(repository)
        play {
            cache = new ExpiringCache(mockCache, strategy, 3)
        }
    }

    void testGetFromCache() {
        mockCache.getFromCache("key").returns(1)
        play {
            assertEquals 1, cache.getFromCache("key")
        }
    }

    void testRemoveFromCache() {
        mockCache.removeFromCache("key")
        play {
            cache.removeFromCache("key")
        }
    }

    void testFlush() {
        mockCache.flush()
        play {
            cache.flush()
        }
    }

    void testGetSize() {
        mockCache.size.returns(2)
        play {
            assertEquals 2, cache.size
        }
    }

    void testPutToCacheAndAlreadyExists() {
        mockCache.getFromCache("key").returns(2)
        mockCache.putToCache("key", 1)
        play {
            cache.putToCache("key", 1)
        }
    }

    void testPutToCacheAndNotExistsButSizeNotExceed() {
        with(mockCache) {
            getFromCache("key").returns(null)
            size.returns(2)
            putToCache("key", 1)
        }
        play {
            cache.putToCache("key", 1)
        }
    }

    void testPutToCacheAndNotExistsButSizeExceed() {
        with(mockCache) {
            getFromCache("key").returns(null)
            size.returns(4).returns(3).returns(2)
            removeFromCache("test 1")
            removeFromCache("test 2")
            putToCache("key", 1)
        }
        strategy.getKeyToRemove().returns("test 1").returns("test 2")
        play {
            cache.putToCache("key", 1)
        }
    }

    void testPutAndAlreadyExists() {
        mockCache.getFromCache("key").returns(1)
        mockCache.put("key", 2)
        strategy.onPut("key")
        play {
            cache.put("key", 2)
        }
    }

    void testPutAndNotExistsButSizeNotExceed() {
        with(mockCache) {
            getFromCache("key").returns(null)
            size.returns(2)
            put("key", 1)
        }
        strategy.onPut("key")
        play {
            cache.put("key", 1)
        }
    }

    void testPutAndNotExistsButSizeExceed() {
        with(mockCache) {
            getFromCache("key").returns(null)
            size.returns(4).returns(3).returns(2)
            removeFromCache("test 1")
            removeFromCache("test 2")
            put("key", 1)
        }
        with(strategy) {
            getKeyToRemove().returns("test 1").returns("test 2")
            onPut("key")
        }
        play {
            cache.put("key", 1)
        }
    }

    void testGet() {
        mockCache.getFromCache("key").returns(1)
        strategy.onGet("key")
        play {
            assertEquals 1, cache.get("key")
        }
    }

    void testGetNotFound() {
        with(mockCache) {
            getFromCache("key").returns(null).times(2)
            size.returns(2)
            putToCache("key", 1)
        }
        repository.get("key").returns(1)
        strategy.onGet("key")
        play {
            assertEquals 1, cache.get("key")
        }
    }

}
