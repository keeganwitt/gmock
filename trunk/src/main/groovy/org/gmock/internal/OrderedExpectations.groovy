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

import org.gmock.internal.times.StrictTimes
import static org.junit.Assert.fail

class OrderedExpectations {

    def groups = []

    def newStrictGroup() {
        groups << new StrictGroup()
    }

    def add(mock, expectation) {
        groups.last().add(mock, expectation)
    }

    def findMatching(mock, signature) {
        for (group in groups) {
            def expectation = group.findMatching(mock, signature)
            if (expectation) return expectation
        }
        return null
    }

    def findSignature(mock, signature) {
        groups.find { it.findSignature(mock, signature) }
    }

    def validate() {
        groups*.validate()
    }

    def verify() {
        groups*.verify()
    }

    def reset() {
        groups = []
    }

}

class StrictGroup {

    def expectations = []
    def current = 0

    def add(mock, expectation) {
        expectation.signatureObserver = this
        expectations << expectation
    }

    def findMatching(mock, signature) {
        def backup = current
        for (; current < expectations.size(); ++current) {
            def expectation = expectations[current]
            if (expectation.mock.is(mock) && expectation.canCall(signature)) {
                return expectation
            } else if (!expectation.satisfied()) {
                break
            }
        }
        current = backup
        return null
    }

    def validate() {
        expectations*.validate()
    }

    def verify() {
        if (!expectations.every { it.isVerified()}) {
            fail() // TODO: the message should be given
        }
    }

    def findSignature(mock, signature) {
        expectations.find { mock.is(it.mock) && signature.match(it.signature) }
    }

    void checkTimes(expectation) {
        if (!expectations.empty) {
            def last = expectations.last()
            def exps = last.is(expectation) ? expectations.subList(0, expectations.size() - 1) : expectations
            if (!exps.empty) {
                last = exps.last()
                if (last.signature == expectation.signature && last.mock.is(expectation.mock) && !(last.times instanceof StrictTimes)) {
                    expectations = exps
                    throw new IllegalStateException("Last method called on mock already has a non-fixed count set.")
                }
            }
        }
    }

    void signatureChanged(expectation) {
        checkTimes(expectation)
    }

}
