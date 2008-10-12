package org.gmock

class ReturnValue {

    def returnValue;

    ReturnValue(returnValue){
        this.returnValue = returnValue;
    }

    def doReturn(){
        return returnValue
    }

}