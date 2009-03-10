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
package org.gmock.internal.callstate

import org.gmock.internal.expectation.Expectation

class CallState {

    def states = []
    def multipleMocks
    def showTitle
    def title

    CallState(multipleMocks, showTitle, title = null) {
        this.multipleMocks = multipleMocks
        this.showTitle = showTitle
        this.title = title
    }

    def append(Expectation expectation) {
        if (!expectation.hidden) {
            def signature = expectation.signature
            def state = findStateWithSignature(signature)
            if (!state) {
                state = new MethodState(signature, multipleMocks)
                states << state
            }
            state.merge(expectation)
        }
    }

    protected findStateWithSignature(signature) {}

    def append(CallState callState) {
        states << callState
    }

    def nowCalling(signature) {
        states*.nowCalling(signature)
    }

    String toString(level = 1) {
        def result = ""
        if (showTitle && title) {
            result = "${'  ' * level}$title:\n"
            ++level
        }
        result += states*.toString(level).join("\n")
        return result
    }

}

class MethodState {

    def signature
    def multipleMocks
    def expected = null
    def called = 0
    def nowCalling = false

    MethodState(signature, multipleMocks) {
        this.signature = signature
        this.multipleMocks = multipleMocks
    }

    def merge(expectation){
        expected = expected ? expectation.times.merge(expected) : expectation.times
        called += expectation.called
    }

    def nowCalling(signature) {
        if (this.signature == signature) {
            nowCalling = true
        }
    }

    String toString(level) {
        "${'  ' * level}${signature.toString(multipleMocks)}: expected $expected, actual $called${nowCalling ? ' (+1)' : ''}"
    }

}
