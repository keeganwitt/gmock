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

    def raises(Throwable exception){
        expectation.returnValue = new ReturnRaiseException(exception)
        return this
    }

    def raises(Class exceptionClass, Object[] params) {
        def exception = exceptionClass.metaClass.invokeConstructor(params)
        return raises(exception)
    }

    def stub(){
        expectation.stubed = true
    }

}