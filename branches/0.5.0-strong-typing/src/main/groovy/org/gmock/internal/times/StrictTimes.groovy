package org.gmock.internal.times

class StrictTimes {

    def times

    StrictTimes(int times) {
        this.times = times
    }

    def stillRemain(int called) {
        return called < times
    }

    def isCase(int called) {
        return called == times
    }

    String toString() {
        times.toString()
    }

    def merge(StrictTimes times) {
        new StrictTimes(times.times + this.times)  
    }

}
