package org.gmock.internal.recorder

import org.gmock.GMockTestCase
import org.gmock.internal.times.*

class BaseRecorderTest extends GMockTestCase {

    def mockExpectation = mock()
    BaseRecorder recorder = new BaseRecorder(mockExpectation)

    void testStub() {
        mockExpectation.times.sets(match { it.class == AnyTimes })
        play {
            recorder.stub()
        }
    }

    void testTimesRange() {
        mockExpectation.times.sets(match { it instanceof RangeTimes && it.range == 1..2 })
        play {
            recorder.times(1..2)
        }
    }

    void testTimesNumber() {
        mockExpectation.times.sets(match { it instanceof StrictTimes && it.times == 5 })
        play {
            recorder.times(5)
        }
    }

    void testNever() {
        mockExpectation.times.sets(match { it.class == NeverTimes })
        play {
            recorder.never()
        }
    }

    void testOnce() {
        mockExpectation.times.sets(match { it instanceof StrictTimes && it.times == 1 })
        play {
            recorder.once()
        }
    }

    void testAtLeastOnce() {
        mockExpectation.times.sets(match { it instanceof AtLeastTimes && it.times == 1 })
        play {
            recorder.atLeastOnce()
        }
    }

    void testAtLeast() {
        mockExpectation.times.sets(match { it instanceof AtLeastTimes && it.times == 5 })
        play {
            recorder.atLeast(5)
        }
    }

    void testAtMostOnce() {
        mockExpectation.times.sets(match { it instanceof AtMostTimes && it.times == 1 })
        play {
            recorder.atMostOnce()
        }
    }

    void testAtMost() {
        mockExpectation.times.sets(match { it instanceof AtMostTimes && it.times == 3 })
        play {
            recorder.atMost(3)
        }
    }

}
