/*
 * Copyright 2008-2009 Julien Gagnet, Johnny Jian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmock.internal

import org.gmock.internal.result.ReturnNull
import org.gmock.internal.times.StrictTimes

class Expectation {

    def expectations
    def signature
    def result = ReturnNull.INSTANCE
    def times = new StrictTimes(1)
    def called = 0
    def hidden = false

    void setSignature(signature) {
        this.signature = signature
        expectations.checkTimes(this)
    }

    boolean canCall(methodSignature) {
        return times.stillRemain(called) && signature.match(methodSignature)
    }

    def answer(arguments) {
        ++called
        return result.answer(arguments as Object[])
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