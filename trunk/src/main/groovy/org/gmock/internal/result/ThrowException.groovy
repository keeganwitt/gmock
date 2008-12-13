package org.gmock.internal.result

class ThrowException {

    def exception

    ThrowException(exception){
        this.exception = exception
    }

    ThrowException(Class exceptionClass, Object[] params){
        this.exception = exceptionClass.metaClass.invokeConstructor(params)
    }

    def answer() {
        throw exception
    }

}
