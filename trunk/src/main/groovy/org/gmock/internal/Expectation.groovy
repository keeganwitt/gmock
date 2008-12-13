package org.gmock.internal

import org.gmock.internal.result.ReturnNull
import org.gmock.internal.times.StrictTimes

class Expectation {

    def expectations
    def signature
    def result = new ReturnNull()
    def times = new StrictTimes(1)
    def called = 0

    void setSignature(signature) {
        this.signature = signature
        expectations.checkTimes(this)
    }

    boolean canCall(methodSignature) {
        return times.stillRemain(called) && signature.match(methodSignature)
    }

    def answer() {
        ++called
        return result.answer()
    }

    def isVerified() {
        // TODO: should we check if signature is null here any more?
        return !signature || called in times
    }

    def validate(){
        // TODO: should we check if signature is null here any more?
        signature?.validate()
    }

    String toString() {
        return "Expectation [signature: $signature, result: $result, times: $times]"
    }

}
