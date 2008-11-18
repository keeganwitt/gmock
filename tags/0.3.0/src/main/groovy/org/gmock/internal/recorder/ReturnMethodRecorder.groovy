package org.gmock.internal.recorder

import org.gmock.internal.ReturnRaiseException
import org.gmock.internal.ReturnValue

class ReturnMethodRecorder {

    def expectation

    ReturnMethodRecorder(expectation){
        this.expectation = expectation
    }

    def returns(returnValue){
        expectation.returnValue = new ReturnValue(returnValue)
        return this
    }

    def raises(Throwable exception){
        expectation.returnValue = new ReturnRaiseException(exception)
        return this
    }

    def raises(Class exceptionClass, Object[] params) {
        expectation.returnValue = new ReturnRaiseException(exceptionClass, params)
        return this
    }

    def stub(){
        expectation.stubed = true
    }

}