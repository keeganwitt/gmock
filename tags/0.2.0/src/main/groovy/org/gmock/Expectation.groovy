package org.gmock

class Expectation {

    def signature
    def returnValue = new ReturnNull()
    def called = false
    def stubed = false

    Expectation(signature, returnValue = new ReturnNull()){
        this.signature = signature
        this.returnValue = returnValue
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

    String toString(){
        return "Expectation [signature: $signature, return: $returnValue]"
    }

}