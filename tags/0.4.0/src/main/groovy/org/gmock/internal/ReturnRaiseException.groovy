package org.gmock.internal

class ReturnRaiseException {

    def exception;

    ReturnRaiseException(exception){
        this.exception = exception;
    }

    ReturnRaiseException(Class exceptionClass, Object[] params){
        this.exception = exceptionClass.metaClass.invokeConstructor(params)

    }

    def doReturn() {
        throw exception
    }


}