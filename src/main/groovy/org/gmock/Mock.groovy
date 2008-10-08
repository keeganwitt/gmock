package org.gmock

import static junit.framework.Assert.*

class Mock {

    def expectations = new ExpectationCollection()
    def replay = false

    def methodMissing(String name, args) {
        def signature = new MethodSignature(name, args)
        if (replay){
            def expectation = expectations.findMatching(signature)
            if (expectation){
                return expectation.doReturn()
            } else {
                def callState = expectations.callState().toString()
                if (callState){ callState = "\n$callState" }
                fail("Unexpected method call '${signature}'$callState")
            }
            return result
        } else {
            def expectation = new Expectation(signature)
            expectations.add( expectation )
            return new MethodRecorder(expectation)
        }
    }


    def propertyMissing(name, args) {
        methodMissing(name, args)
    }

    private void _verify(){
        expectations.verify()
    }

    private void _reset(){
        this.expectations = new ExpectationCollection()
        replay = false
    }

    private void _replay(){
        replay = true
    }
    



}

