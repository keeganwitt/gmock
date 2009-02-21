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

class OrderedExpectations {

    def expectations = []
    def current = 0

    def add(expectation) {
        expectations << expectation
    }

    def findMatching(signature) {
        while (current < expectations.size()) {
            def expectation = expectations[current]
            if (expectation.canCall(signature)) {
                return expectation
            } else if (expectation.satisfied()) {
                ++current
            } else {
                break
            }
        }
        return null
    }

    def verify() {
        if (!expectations.every { it.isVerified()}) {
            fail() // TODO: the message should be given
        }
    }

    def reset() {
        expectations = []
        current = 0
    }

}
