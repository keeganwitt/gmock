package org.gmock.internal

import org.gmock.internal.times.StrictTimes

class Expectation {

    def expectations
    def signature
    def returnValue = new ReturnNull()
    def times = new StrictTimes(1)
    def called = 0

    void setSignature(signature) {
        this.signature = signature
        expectations.checkTimes(this)
    }

    boolean canCall(methodSignature) {
        return times.stillRemain(called) && signature.match(methodSignature)
    }

    def doReturn() {
        ++called
        return returnValue.doReturn()
    }

    def isVerified() {
        return !signature || called in times
    }

    def validate(){
        signature?.validate()
    }

    String toString() {
        return "Expectation [signature: $signature, return: $returnValue, times: $times]"
    }

}
