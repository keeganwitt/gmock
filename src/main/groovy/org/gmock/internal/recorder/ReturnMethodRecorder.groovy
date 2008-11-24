package org.gmock.internal.recorder

import org.gmock.internal.ReturnRaiseException
import org.gmock.internal.ReturnValue

class ReturnMethodRecorder extends BaseRecorder {

    ReturnMethodRecorder(expectation){
        super(expectation)
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

}
