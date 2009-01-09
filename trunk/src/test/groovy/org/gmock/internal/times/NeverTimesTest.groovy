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

class NeverTimesTest extends GroovyTestCase {

    NeverTimes times = new NeverTimes()

    void testStillRemain() {
        assertFalse times.stillRemain(0)
        assertFalse times.stillRemain(1)
        assertFalse times.stillRemain(5)
    }

    void testIsCase() {
        assert 0 in times
        assertFalse 1 in times
        assertFalse 2 in times
    }

    void testToString() {
        assertEquals "never", times.toString()
    }

    void testMerge() {
        def st = new StrictTimes(3)
        assertEquals st, times.merge(st)
    }

}
