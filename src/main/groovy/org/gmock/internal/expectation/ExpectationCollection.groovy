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
package org.gmock.internal.expectation

import org.gmock.internal.times.StrictTimes

class ExpectationCollection {

    def expectations = []
    def controller

    ExpectationCollection(controller) {
        this.controller = controller
    }

    void add(expectation) {
        expectation.signatureObserver = this
        expectations << expectation
    }

    def findMatching(signature) {
        expectations.find { it.canCall(signature)}
    }

    void verify(){
        if (!isVerified()) {
            controller.fail("Expectation not matched on verify:")
        }
    }

    def isVerified() {
        expectations.every { it.isVerified() }
    }

    def satisfied() {
        isVerified()
    }

    def validate(){
        expectations*.validate()
    }

    void checkTimes(expectation) {
        def last = expectations.reverse().find {
            !it.is(expectation) && it.signature == expectation.signature
        }
        if (last != null && !(last.times instanceof StrictTimes)) {
            expectations.remove(expectation)
            throw new IllegalStateException("Last method called on mock already has a non-fixed count set.")
        }
    }

    void signatureChanged(expectation) {
        checkTimes(expectation)
    }

    def findSignature(signature) {
        expectations.find { signature.match(it.signature) }
    }

    def reset() {
        expectations = []
    }

    boolean isEmpty() {
        expectations.empty
    }

}
