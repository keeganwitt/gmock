package org.gmock

class MethodExpectation {

    def signature
    def returnValue = new ReturnNull()
    def called = false
    def stubed = false

    MethodExpectation(methodName, arguments){
        this.signature = new MethodSignature(methodName, arguments)
    }
    
    void andReturn(returnValue){
        this.returnValue = new ReturnValue(returnValue)
    }

    void andRaise(exception){
        this.returnValue = new ReturnRaiseException(exception)
    }

    void andStubReturn(returnValue){
        this.returnValue = new ReturnValue(returnValue)
        stubed = true
    }

    boolean canCall(methodSignature){
        if (called) return false
        def result = this.signature == methodSignature
        return result
    }


    def doReturn(){
        if (!stubed) called = true
        return returnValue.doReturn()
    }

    def isVerified(){
        return stubed || called
    }

}