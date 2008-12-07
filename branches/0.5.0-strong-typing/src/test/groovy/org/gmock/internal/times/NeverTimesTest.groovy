package org.gmock.internal.times

class NeverTimesTest extends GroovyTestCase {

    NeverTimes times = new NeverTimes()

    void testStillRemain() {
        assertFalse times.stillRemain(0)
        assertFalse times.stillRemain(1)
        assertFalse times.stillRemain(5)
    }

    void testIsCase() {
        assert 0 in times
        assertFalse 1 in times
        assertFalse 2 in times
    }

    void testToString() {
        assertEquals "never", times.toString()
    }

    void testMerge() {
        def st = new StrictTimes(3)
        assertEquals st, times.merge(st)
    }

}
