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

class AnyTimesTest extends GroovyTestCase {

    AnyTimes times = new AnyTimes()

    void testStillRemain() {
        assert times.stillRemain(0)
        assert times.stillRemain(1)
        assert times.stillRemain(5)
        assert times.stillRemain(10)
    }

    void testIsCase() {
        assert 0 in times
        assert 1 in times
        assert 10 in times
    }

    void testToString() {
        assertEquals "any times", times.toString()
    }

    void testMerge() {
        def st = new StrictTimes(5)
        def result = times.merge(st)
        assertEquals AtLeastTimes, result.class
        assertEquals 5, result.times
    }

}
