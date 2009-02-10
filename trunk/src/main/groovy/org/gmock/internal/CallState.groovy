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

class CallState {

    def methodStates = [:]

    def append(expectation){
        if (!expectation.hidden) {
            def signature = expectation.signature
            if (!methodStates.containsKey(signature)){
                methodStates[signature] = new MethodState(signature)
            }
            methodStates[signature].merge(expectation)
        }
    }

    def nowCalling(signature) {
        if (methodStates.containsKey(signature)){
            methodStates[signature].nowCalling = true
        }
    }

    String toString(){
        methodStates.values().join("\n")
    }

}

class MethodState {

    def methodSignature
    def expected = null
    def called = 0
    def nowCalling = false

    MethodState(methodSignature){
        this.methodSignature = methodSignature
    }

    def merge(expectation){
        expected = expected ? expectation.times.merge(expected) : expectation.times
        called += expectation.called
    }

    String toString(){
        "  '${methodSignature}': expected $expected, actual $called${nowCalling ? ' (+1)' : ''}"
    }

}
