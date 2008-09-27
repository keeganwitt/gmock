package org.gmock

import static junit.framework.Assert.*

class Mock {

    def expectations = []
    def replay = false


    def methodMissing(String name, args) {
        def signature = new MethodSignature(name, args)
        if (replay){
            def expectation = expectations.find { it.canCall(signature)}
            if (expectation){
                return expectation.doReturn()
            } else {
                def callState = callState().toString()
                if (callState){ callState = "\n$callState" }
                fail("Unexpected method call '${signature}'$callState")

            }
            return result
        } else {
            def expectation = new MethodExpectation(name, args)
            expectations.add(expectation)
            return expectation
        }
    }

    def propertyMissing(name, args) {
        methodMissing(name, args)
    }


    private void _replay(){
        replay = true
    }

    private void _verify(){
        assert replay, "Mock should be replay before verify"
        if (expectations.find { !it.isVerified()} ){
            fail("Expectation not matched on verify:\n${callState()}")
        }
    }

    private void _reset(){
        this.expectations = []        
        replay = false

    }

    private callState(){
        def callState = new CallState()
        expectations.each {
            callState.append(it)
        }
        return callState
    }




}

