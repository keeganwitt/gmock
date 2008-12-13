package org.gmock.internal.recorder

import org.gmock.internal.signature.StaticSignature

class StaticMethodRecoder implements GroovyInterceptable {

    def expectation
    def aClass

    StaticMethodRecoder(aClass, expectation){
        this.expectation = expectation
        this.aClass = aClass
        this.expectation.signature = new StaticSignature(aClass)
    }

    Object invokeMethod(String name, Object args) {
        expectation.signature = new StaticSignature(aClass, name, args)
        return new ReturnMethodRecorder(expectation)
    }

}
