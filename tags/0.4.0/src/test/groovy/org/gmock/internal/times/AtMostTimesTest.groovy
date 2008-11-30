package org.gmock.internal.times

class AtMostTimesTest extends GroovyTestCase {

    void testStillRemain() {
        def times = new AtMostTimes(0)
        assertFalse times.stillRemain(0)
        assertFalse times.stillRemain(1)

        times = new AtMostTimes(1)
        assert times.stillRemain(0)
        assertFalse times.stillRemain(1)

        times = new AtMostTimes(3)
        assert times.stillRemain(2)
        assertFalse times.stillRemain(3)
    }

    void testIsCase() {
        def times = new AtMostTimes(0)
        assert 0 in times
        assertFalse 1 in times

        times = new AtMostTimes(1)
        assert 0 in times
        assert 1 in times
        assertFalse 2 in times

        times = new AtMostTimes(5)
        assert 5 in times
        assertFalse 6 in times
    }

    void testToString() {
        def times = new AtMostTimes(1)
        assertEquals "at most 1", times.toString()

        times = new AtMostTimes(5)
        assertEquals "at most 5", times.toString()
    }

    void testMerge() {
        def times = new AtMostTimes(5)
        def st = new StrictTimes(2)
        def result = times.merge(st)
        assertEquals AtMostTimes, result.class
        assertEquals 7, result.times
    }

}
