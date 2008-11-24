package org.gmock.internal.times

class NeverTimes {

    def stillRemain(int called) {
        return false
    }

    def isCase(int called) {
        return called == 0
    }

    String toString() {
        "never"
    }

    def merge(StrictTimes times) {
        times
    }

}
