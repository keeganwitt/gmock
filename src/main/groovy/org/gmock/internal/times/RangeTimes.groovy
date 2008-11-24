package org.gmock.internal.times

class RangeTimes {

    def range

    RangeTimes(IntRange range) {
        this.range = range
    }

    def stillRemain(int called) {
        return called < range.to
    }

    def isCase(int called) {
        return called in range
    }

    String toString() {
        range.toString()
    }

    def merge(StrictTimes times) {
        new RangeTimes((range.from + times.times)..(range.to + times.times))
    }

}
