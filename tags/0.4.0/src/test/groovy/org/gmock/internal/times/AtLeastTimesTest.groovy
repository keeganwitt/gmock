package org.gmock.internal.times

class AtLeastTimesTest extends GroovyTestCase {

    void testStillRemain() {
        def times = new AtLeastTimes(0)
        assert times.stillRemain(0)
        assert times.stillRemain(5)

        times = new AtLeastTimes(1)
        assert times.stillRemain(0)
        assert times.stillRemain(5)
    }

    void testIsCase() {
        def times = new AtLeastTimes(0)
        assert 0 in times
        assert 1 in times
        assert 10 in times

        times = new AtLeastTimes(1)
        assertFalse 0 in times
        assert 1 in times
        assert 10 in times

        times = new AtLeastTimes(5)
        assertFalse 0 in times
        assertFalse 1 in times
        assertFalse 4 in times
        assert 5 in times
        assert 10 in times
    }

    void testToString() {
        def times = new AtLeastTimes(0)
        assertEquals "at least 0", times.toString()

        times = new AtLeastTimes(1)
        assertEquals "at least 1", times.toString()
    }

    void testMerge() {
        def times = new AtLeastTimes(3)
        def st = new StrictTimes(5)
        def result = times.merge(st)
        assertEquals AtLeastTimes, result.class
        assertEquals 8, result.times
    }

}
