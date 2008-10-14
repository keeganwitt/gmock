package org.gmock.internal

class ReturnRaiseException {

    def exception;

    ReturnRaiseException(exception){
        this.exception = exception;
    }

    def doReturn() {
        throw exception
    }


}