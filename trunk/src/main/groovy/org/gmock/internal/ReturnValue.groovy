package org.gmock.internal

class ReturnValue {

    def returnValue

    ReturnValue(returnValue){
        this.returnValue = returnValue;
    }

    def doReturn(){
        return returnValue
    }

}
