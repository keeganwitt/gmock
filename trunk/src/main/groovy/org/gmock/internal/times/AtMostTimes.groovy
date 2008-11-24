package org.gmock.internal.times

class AtMostTimes {

    def times

    AtMostTimes(int times) {
        this.times = times
    }

    def stillRemain(int called) {
        return called < times
    }

    def isCase(int called) {
        return called <= times
    }

    String toString() {
        "at most $times"
    }

    def merge(StrictTimes times) {
        new AtMostTimes(times.times + this.times)
    }

}
