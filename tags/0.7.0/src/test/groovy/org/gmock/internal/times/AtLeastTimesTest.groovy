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

class AtLeastTimesTest extends GroovyTestCase {

    void testStillRemain() {
        def times = new AtLeastTimes(0)
        assert times.stillRemain(0)
        assert times.stillRemain(5)

        times = new AtLeastTimes(1)
        assert times.stillRemain(0)
        assert times.stillRemain(5)
    }

    void testIsCase() {
        def times = new AtLeastTimes(0)
        assert 0 in times
        assert 1 in times
        assert 10 in times

        times = new AtLeastTimes(1)
        assertFalse 0 in times
        assert 1 in times
        assert 10 in times

        times = new AtLeastTimes(5)
        assertFalse 0 in times
        assertFalse 1 in times
        assertFalse 4 in times
        assert 5 in times
        assert 10 in times
    }

    void testToString() {
        def times = new AtLeastTimes(0)
        assertEquals "at least 0", times.toString()

        times = new AtLeastTimes(1)
        assertEquals "at least 1", times.toString()
    }

    void testMerge() {
        def times = new AtLeastTimes(3)
        def st = new StrictTimes(5)
        def result = times.merge(st)
        assertEquals AtLeastTimes, result.class
        assertEquals 8, result.times
    }

}
