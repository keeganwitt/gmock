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
package org.gmock.internal.recorder

import org.gmock.internal.times.*

class BaseRecorder {

    def expectation

    BaseRecorder(expectation) {
        this.expectation = expectation
    }

    def stub() {
        expectation.times = AnyTimes.INSTANCE
        return null
    }

    def times(IntRange range) {
        expectation.times = new RangeTimes(range)
        return null
    }

    def times(int times) {
        expectation.times = new StrictTimes(times)
        return null
    }

    def never() {
        expectation.times = NeverTimes.INSTANCE
        return null
    }

    def once() {
        return times(1)
    }

    def atLeastOnce() {
        return atLeast(1)
    }

    def atLeast(int times) {
        expectation.times = new AtLeastTimes(times)
        return null
    }

    def atMostOnce() {
        return atMost(1)
    }

    def atMost(int times) {
        expectation.times = new AtMostTimes(times)
        return null
    }

}
