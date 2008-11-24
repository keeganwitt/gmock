package org.gmock.internal.times

class AtLeastTimes {

    def times

    AtLeastTimes(int times) {
        this.times = times
    }

    def stillRemain(int called) {
        return true
    }

    def isCase(int called) {
        return called >= times
    }

    String toString() {
        "at least $times"
    }

    def merge(StrictTimes times) {
        new AtLeastTimes(times.times + this.times)
    }

}
