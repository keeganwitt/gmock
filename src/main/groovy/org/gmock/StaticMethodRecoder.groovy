package org.gmock

import org.gmock.signature.StaticSignature


class StaticMethodRecoder {

    def expectation
    def aClass

    StaticMethodRecoder(aClass, expectation){
        this.expectation = expectation
        this.aClass = aClass
    }

    def methodMissing(String name, args) {
        expectation.signature = new StaticSignature(aClass, name, args)
        return new ReturnMethodRecorder(expectation)
    }

}