package org.gmock.internal.recorder

import org.gmock.internal.result.ThrowException
import org.gmock.internal.signature.ConstructorSignature
import org.gmock.internal.Expectation
import org.gmock.internal.result.ReturnValue

class ConstructorRecorder {

    def args
    def result

    ConstructorRecorder(args){
        this.args = args
    }

    def raises(Throwable exception){
        this.result = new ThrowException(exception)
        return this
    }

    def raises(Class exceptionClass, Object[] params) {
        this.result = new ThrowException(exceptionClass, params)
        return this
    }

    def generateExpectation(clazz, mockInstance){
        def signature = new ConstructorSignature(clazz, args)
        def returnValue = result ? result : new ReturnValue(mockInstance)
        def expectation = new Expectation(result: returnValue)
        expectation.@signature = signature
        return expectation
    }

}

