package org.gmock.internal.times

class StrictTimesTest extends GroovyTestCase {

    void testStillRemain() {
        def times = new StrictTimes(0)
        assertFalse times.stillRemain(0)
        assertFalse times.stillRemain(1)

        times = new StrictTimes(1)
        assert times.stillRemain(0)
        assertFalse times.stillRemain(1)

        times = new StrictTimes(5)
        assert times.stillRemain(4)
        assertFalse times.stillRemain(5)
    }

    void testIsCase() {
        def times = new StrictTimes(0)
        assert 0 in times
        assertFalse 1 in times

        times = new StrictTimes(1)
        assertFalse 0 in times
        assert 1 in times
        assertFalse 2 in times
    }

    void testToString() {
        def times = new StrictTimes(1)
        assertEquals "1", times.toString()

        times = new StrictTimes(5)
        assertEquals "5", times.toString()
    }

    void testMerge() {
        def times = new StrictTimes(3)
        def st = new StrictTimes(4)
        def result = times.merge(st)
        assertEquals StrictTimes, result.class
        assertEquals 7, result.times
    }

}
