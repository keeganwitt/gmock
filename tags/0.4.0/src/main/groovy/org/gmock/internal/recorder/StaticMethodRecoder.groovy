package org.gmock.internal.recorder

import org.gmock.internal.signature.StaticSignature

class StaticMethodRecoder {

    def expectation
    def aClass

    StaticMethodRecoder(aClass, expectation){
        this.expectation = expectation
        this.aClass = aClass
        this.expectation.signature = new StaticSignature(aClass)
    }

    def methodMissing(String name, args) {
        expectation.signature = new StaticSignature(aClass, name, args)
        return new ReturnMethodRecorder(expectation)
    }

}