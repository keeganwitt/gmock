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
package org.gmock.internal.times

class AtMostTimesTest extends GroovyTestCase {

    void testStillRemain() {
        def times = new AtMostTimes(0)
        assertFalse times.stillRemain(0)
        assertFalse times.stillRemain(1)

        times = new AtMostTimes(1)
        assert times.stillRemain(0)
        assertFalse times.stillRemain(1)

        times = new AtMostTimes(3)
        assert times.stillRemain(2)
        assertFalse times.stillRemain(3)
    }

    void testIsCase() {
        def times = new AtMostTimes(0)
        assert 0 in times
        assertFalse 1 in times

        times = new AtMostTimes(1)
        assert 0 in times
        assert 1 in times
        assertFalse 2 in times

        times = new AtMostTimes(5)
        assert 5 in times
        assertFalse 6 in times
    }

    void testToString() {
        def times = new AtMostTimes(1)
        assertEquals "at most 1", times.toString()

        times = new AtMostTimes(5)
        assertEquals "at most 5", times.toString()
    }

    void testMerge() {
        def times = new AtMostTimes(5)
        def st = new StrictTimes(2)
        def result = times.merge(st)
        assertEquals AtMostTimes, result.class
        assertEquals 7, result.times
    }

}
