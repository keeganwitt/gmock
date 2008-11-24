package org.gmock.internal

import static junit.framework.Assert.*
import org.gmock.internal.times.StrictTimes

class ExpectationCollection {

    def expectations = []

    void add(expectation){
        expectations << expectation
    }

    def findMatching(signature){
        expectations.find { it.canCall(signature)}
    }

    void verify(){
        if (expectations.find { !it.isVerified()} ){
            fail("Expectation not matched on verify:\n${callState()}")
        }
    }

    def callState(){
        def callState = new CallState()
        expectations.each {
            callState.append(it)
        }
        return callState
    }

    def empty(){
        expectations.size() == 0
    }

    void checkTimes(expectation) {
        def last = expectations.reverse().find {
            !it.is(expectation) && it.signature == expectation.signature
        }
        if (last != null && !(last.times instanceof StrictTimes)) {
            expectations.remove(expectation)
            throw new IllegalStateException("last method called on mock already has a non-fixed count set.")
        }
    }

}
