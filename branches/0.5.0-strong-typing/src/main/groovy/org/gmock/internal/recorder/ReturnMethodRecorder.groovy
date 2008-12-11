package org.gmock.internal.recorder

import org.gmock.internal.result.ReturnValue
import org.gmock.internal.result.ThrowException

class ReturnMethodRecorder extends BaseRecorder {

    ReturnMethodRecorder(expectation){
        super(expectation)
    }

    def returns(value){
        expectation.result = new ReturnValue(value)
        return this
    }

    def raises(Throwable exception){
        expectation.result = new ThrowException(exception)
        return this
    }

    def raises(Class exceptionClass, Object[] params) {
        expectation.result = new ThrowException(exceptionClass, params)
        return this
    }

}
