package org.gmock

class ReturnMethodRecorder {

    def expectation

    ReturnMethodRecorder(expectation){
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