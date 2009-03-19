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
        times(AnyTimes.INSTANCE)
    }

    def times(IntRange range) {
        times(new RangeTimes(range))
    }

    def times(int times) {
        times(new StrictTimes(times))
    }

    def never() {
        times(NeverTimes.INSTANCE)
    }

    def once() {
        times(1)
    }

    def atLeastOnce() {
        atLeast(1)
    }

    def atLeast(int times) {
        times(new AtLeastTimes(times))
    }

    def atMostOnce() {
        atMost(1)
    }

    def atMost(int times) {
        times(new AtMostTimes(times))
    }

    protected times(times) {
        expectation.times = times
        return this
    }

}
