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

import org.gmock.internal.callstate.UnorderedCallState

class UnorderedExpectations {

    Map expectations = new LinkedHashMap()
    def controller

    UnorderedExpectations(controller) {
        this.controller = controller
    }

    def reset() {
        expectations = new LinkedHashMap()
    }

    def appendToCallState(callState) {
        if (expectations.size() > 0) {
            def unorderedCallState = new UnorderedCallState(callState)
            expectations.keySet().each { unorderedCallState.append(it) }
        }
    }

    def add(expectation, underlyingExpectations) {
        underlyingExpectations.add(expectation)
        expectations.put(expectation, underlyingExpectations)
        expectation.expectations = this
    }

    def duplicate(expectation, newExpectation) {
        add(newExpectation, expectations.get(expectation))
    }

}
