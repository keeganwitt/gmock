package org.gmock.internal

import static junit.framework.Assert.*

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



}