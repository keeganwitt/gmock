package org.gmock.internal.times

class AnyTimes {

    def stillRemain(int called) {
        return true
    }

    def isCase(int called) {
        return true
    }

    String toString() {
        "any times"
    }

    def merge(StrictTimes times) {
        times 
    }

}
