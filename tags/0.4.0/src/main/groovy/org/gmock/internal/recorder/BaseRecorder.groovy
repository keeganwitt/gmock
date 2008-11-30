package org.gmock.internal.recorder

import org.gmock.internal.times.*

class BaseRecorder {

    def expectation

    BaseRecorder(expectation) {
        this.expectation = expectation
    }

    def stub() {
        expectation.times = new AnyTimes()
        return null
    }

    def times(IntRange range) {
        expectation.times = new RangeTimes(range)
        return null
    }

    def times(int times) {
        expectation.times = new StrictTimes(times)
        return null
    }

    def never() {
        expectation.times = new NeverTimes()
        return null
    }

    def once() {
        return times(1)
    }

    def atLeastOnce() {
        return atLeast(1)
    }

    def atLeast(int times) {
        expectation.times = new AtLeastTimes(times)
        return null
    }

    def atMostOnce() {
        return atMost(1)
    }

    def atMost(int times) {
        expectation.times = new AtMostTimes(times)
        return null
    }

}
