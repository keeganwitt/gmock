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
import org.gmock.internal.callstate.OrderedCallState
import org.gmock.internal.callstate.UnorderedCallState

class OrderedExpectations {

    def groups = []
    def controller

    OrderedExpectations(controller) {
        this.controller = controller
    }

    def newStrictGroup() {
        groups << new StrictGroup(controller)
    }

    def newLooseGroup() {
        groups.last().newLooseGroup()
    }

    def add(expectation) {
        groups.last().add(expectation)
    }

    def findMatching(signature) {
        findWith("findMatching", signature)
    }

    def findSignature(signature) {
        findWith("findSignature", signature)
    }

    private findWith(method, signature) {
        for (group in groups) {
            def expectation = group."$method"(signature)
            if (expectation) return expectation
        }
        return null
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

    boolean isEmpty() {
        groups.every { it.empty }
    }

    def appendToCallState(callState) {
        groups*.appendToCallState(callState)
    }

}

class StrictGroup {

    def expectations = []
    def current = 0
    def controller

    StrictGroup(controller) {
        this.controller = controller
    }

    def add(expectation) {
        if (controller.orderingController.strictOrdered) {
            expectation.signatureObserver = this
            expectation.expectations = this
            expectations << expectation
        } else { // looseOrdered
            expectations.last().add(expectation)
        }
    }

    def newLooseGroup() {
        def looseGroup = new LooseGroup(controller)
        expectations << looseGroup
    }

    def findMatching(signature) {
        def backup = current
        for (; current < expectations.size(); ++current) {
            def expectation = expectations[current]
            def found = expectation.findMatching(signature)
            if (found) return found
            if (!expectation.satisfied()) {
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
        if (!expectations.every { it.isVerified() }) {
            controller.fail("Expectation not matched on verify:")
        }
    }

    def findSignature(signature) {
        for (expectation in expectations) {
            def found = expectation.findSignature(signature)
            if (found) return found
        }
        return null
    }

    void checkTimes(expectation) {
        if (!expectations.empty) {
            def last = expectations.last()
            def exps = last.is(expectation) ? expectations.subList(0, expectations.size() - 1) : expectations
            if (!exps.empty) {
                last = exps.last()
                if (last instanceof Expectation && last.signature == expectation.signature && !(last.times instanceof StrictTimes)) {
                    expectations = exps
                    throw new IllegalStateException("Last method called on mock already has a non-fixed count set.")
                }
            }
        }
    }

    void signatureChanged(expectation) {
        checkTimes(expectation)
    }

    boolean isEmpty() {
        expectations.empty || expectations.every { it instanceof LooseGroup && it.empty }
    }

    def appendToCallState(callState) {
        if (!empty) {
            def orderedCallState = new OrderedCallState(callState)
            expectations.each {
                if (it instanceof Expectation) {
                    orderedCallState.append(it)
                } else {
                    it.appendToCallState(orderedCallState)
                }
            }
        }
    }

    def duplicate(expectation, newExpectation) {
        newExpectation.signatureObserver = this
        newExpectation.expectations = this
        expectations << newExpectation
    }

}

class LooseGroup extends ExpectationCollection {

    LooseGroup(controller) {
        super(controller)
    }

    void add(expectation) {
        expectation.expectations = this
        super.add(expectation)
    }

    def appendToCallState(callState) {
        if (!empty) {
            def unorderedCallState = new UnorderedCallState(callState)
            expectations.each { unorderedCallState.append(it) }
        }
    }

    def duplicate(expectation, newExpectation) {
        add(newExpectation)
    }

}
