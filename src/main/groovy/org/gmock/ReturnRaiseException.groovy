package org.gmock

class ReturnRaiseException {

    def exception;

    ReturnRaiseException(exception){
        this.exception = exception;
    }

    def doReturn() {
        throw exception
    }


}