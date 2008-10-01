package org.gmock

class ConstructorExpectation {

    def signature
    def called
    def stubed
    def mock

    ConstructorExpectation(aClass, arguments, mock){
        this.signature = new ConstructorSignature(aClass, arguments)
        this.mock = mock
    }

    boolean canCall(arguments){
        if (called) return false
        def result = this.signature.arguments == arguments
        return result
    }

    def doReturn(){
        called = true
        return mock
    }



    

}