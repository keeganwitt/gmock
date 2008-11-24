package org.gmock.internal.times

class AnyTimesTest extends GroovyTestCase {

    AnyTimes times = new AnyTimes()

    void testStillRemain() {
        assert times.stillRemain(0)
        assert times.stillRemain(1)
        assert times.stillRemain(5)
        assert times.stillRemain(10)
    }

    void testIsCase() {
        assert 0 in times
        assert 1 in times
        assert 10 in times
    }

    void testToString() {
        assertEquals "any times", times.toString()
    }

    void testMerge() {
        def st = new StrictTimes(5)
        assertEquals st, times.merge(st)
    }

}
