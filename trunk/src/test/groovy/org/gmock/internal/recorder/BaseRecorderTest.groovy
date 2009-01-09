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

import org.gmock.GMockTestCase
import org.gmock.internal.times.*

class BaseRecorderTest extends GMockTestCase {

    def mockExpectation = mock()
    BaseRecorder recorder = new BaseRecorder(mockExpectation)

    void testStub() {
        mockExpectation.times.set(match { it.class == AnyTimes })
        play {
            recorder.stub()
        }
    }

    void testTimesRange() {
        mockExpectation.times.set(match { it instanceof RangeTimes && it.range == 1..2 })
        play {
            recorder.times(1..2)
        }
    }

    void testTimesNumber() {
        mockExpectation.times.set(match { it instanceof StrictTimes && it.times == 5 })
        play {
            recorder.times(5)
        }
    }

    void testNever() {
        mockExpectation.times.set(match { it.class == NeverTimes })
        play {
            recorder.never()
        }
    }

    void testOnce() {
        mockExpectation.times.set(match { it instanceof StrictTimes && it.times == 1 })
        play {
            recorder.once()
        }
    }

    void testAtLeastOnce() {
        mockExpectation.times.set(match { it instanceof AtLeastTimes && it.times == 1 })
        play {
            recorder.atLeastOnce()
        }
    }

    void testAtLeast() {
        mockExpectation.times.set(match { it instanceof AtLeastTimes && it.times == 5 })
        play {
            recorder.atLeast(5)
        }
    }

    void testAtMostOnce() {
        mockExpectation.times.set(match { it instanceof AtMostTimes && it.times == 1 })
        play {
            recorder.atMostOnce()
        }
    }

    void testAtMost() {
        mockExpectation.times.set(match { it instanceof AtMostTimes && it.times == 3 })
        play {
            recorder.atMost(3)
        }
    }

}
