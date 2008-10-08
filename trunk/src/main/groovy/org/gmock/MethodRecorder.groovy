package org.gmock

class MethodRecorder {

    def expectation

    MethodRecorder(expectation){
        this.expectation = expectation
    }

    void andReturn(returnValue){
        expectation.returnValue = new ReturnValue(returnValue)
    }

    void andRaise(exception){
        expectation.returnValue = new ReturnRaiseException(exception)
    }

    void andStubReturn(returnValue){
        expectation.returnValue = new ReturnValue(returnValue)
        expectation.stubed = true
    }


}