package org.gmock

class MethodRecorder {

    def expectation

    MethodRecorder(expectation){
        this.expectation = expectation
    }

    def returns(returnValue){
        expectation.returnValue = new ReturnValue(returnValue)
        return this
    }

    def raises(exception){
        expectation.returnValue = new ReturnRaiseException(exception)
        return this
    }

    def stub(){
        expectation.stubed = true
    }

}