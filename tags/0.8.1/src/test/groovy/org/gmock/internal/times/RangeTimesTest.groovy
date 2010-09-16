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

class RangeTimesTest extends GroovyTestCase {

    void testStillRemain() {
        def times = new RangeTimes(1..2)
        assert times.stillRemain(0)
        assert times.stillRemain(1)
        assertFalse times.stillRemain(2)

        times = new RangeTimes(5..3)
        assert times.stillRemain(3)
        assertFalse times.stillRemain(5)
    }

    void testIsCase() {
        def times = new RangeTimes(2..4)
        assertFalse 0 in times
        assertFalse 1 in times
        assert 2 in times
        assert 3 in times
        assert 4 in times
        assertFalse 5 in times

        times = new RangeTimes(3..1)
        assertFalse 0 in times
        assert 1 in times
        assert 3 in times
        assertFalse 4 in times
    }

    void testToString() {
        def times = new RangeTimes(2..3)
        assertEquals "2..3", times.toString()

        times = new RangeTimes(5..2)
        assertEquals "5..2", times.toString()
    }

    void testMerge() {
        def times = new RangeTimes(2..5)
        def st = new StrictTimes(4)
        def result = times.merge(st)
        assertEquals RangeTimes, result.class
        assertEquals 6..9, result.range

        times = new RangeTimes(5..2)
        result = times.merge(st)
        assertEquals RangeTimes, result.class
        assertEquals 6..9, result.range
    }

}
