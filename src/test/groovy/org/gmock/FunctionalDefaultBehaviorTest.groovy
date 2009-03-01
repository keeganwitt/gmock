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
package org.gmock

import junit.framework.AssertionFailedError
import org.gmock.utils.JavaTestHelper

class FunctionalDefaultBehaviorTest extends GMockTestCase {

    void testEquals() {
        def m1 = mock()
        def m2 = mock()
        play {
            assertFalse m1 == m2
            assertTrue m2 != m1
            assertTrue m1 == m1
            assertTrue m2 == m2
        }
    }

    void testEqualsInJava() {
        def m1 = mock()
        def m2 = mock()
        play {
            assertFalse JavaTestHelper.equalsToEachOther(m1, m2)
            assertFalse JavaTestHelper.equalsToEachOther(m2, m1)
            assertTrue JavaTestHelper.equalsToEachOther(m1, m1)
            assertTrue JavaTestHelper.equalsToEachOther(m2, m2)
        }
    }

    void testHashCode() {
        def m = mock()
        play {
            assertEquals System.identityHashCode(m), m.hashCode()
        }
    }

    void testHashCodeInJava() {
        def m = mock()
        play {
            assertEquals System.identityHashCode(m), JavaTestHelper.hashCodeOf(m)
        }
    }

    void testPutIntoAHashMap() {
        def map = [:]
        def key1 = mock()
        def key2 = mock()
        play {
            map.put(key1, "value 1")
            map.put(key2, "value 2")
            assertEquals "value 1", map.get(key1)
            assertEquals "value 2", map.get(key2)
        }
    }

    void testToString() {
        def m1 = mock()
        def m2 = mock(Date)
        play {
            assertEquals "Mock for Object", m1.toString()
            assertEquals "Mock for Date", m2.toString()
        }
    }

    void testToStringInJava() {
        def m1 = mock()
        def m2 = mock(Date)
        play {
            assertEquals "Mock for Object", JavaTestHelper.toStringOn(m1)
            assertEquals "Mock for Date", JavaTestHelper.toStringOn(m2)
        }
    }

    void testMockEqualsStopDefaultBehavior() {
        def m1 = mock()
        def m2 = mock()
        m1.equals(m2).returns(true)
        play {
            assertTrue m1 == m2
            assertEquals System.identityHashCode(m1), m1.hashCode()
            assertEquals "Mock for Object (1)", m1.toString()

            def expected = "Unexpected method call 'equals(Mock for Object (2))' on 'Mock for Object (1)'\n" +
                           "  'equals(Mock for Object (2))' on 'Mock for Object (1)': expected 1, actual 1 (+1)"
            def message = shouldFail(AssertionFailedError) {
                m1.equals(m2)
            }
            assertEquals expected, message
        }
    }

    void testMockHashCodeStopDefaultBehavior() {
        def m1 = mock(), m2 = mock()
        m1.hashCode().returns(1)
        play {
            assertEquals 1, m1.hashCode()
            assertFalse m1 == m2
            assertEquals "Mock for Object (1)", m1.toString()

            def expected = "Unexpected method call 'hashCode()' on 'Mock for Object (1)'\n" +
                           "  'hashCode()' on 'Mock for Object (1)': expected 1, actual 1 (+1)"
            def message = shouldFail(AssertionFailedError) {
                m1.hashCode()
            }
            assertEquals expected, message
        }
    }

    void testMockToStringStopDefaultBehavior() {
        def m1 = mock(), m2 = mock()
        m1.toString().returns("test")
        play {
            assertEquals "test", m1.toString()
            assertFalse m1 == m2
            assertEquals System.identityHashCode(m1), m1.hashCode()

            def expected = "Unexpected method call 'toString()' on 'Mock for Object (1)'\n" +
                           "  'toString()' on 'Mock for Object (1)': expected 1, actual 1 (+1)"
            def message = shouldFail(AssertionFailedError) {
                m1.toString()
            }
            assertEquals expected, message
        }
    }

    void testDefaultMockNames() {
        def m1 = mock()
        def m2 = mock(Date)

        play {
            assertEquals 'Mock for Date', m2.toString()
            assertEquals 'Mock for Date', JavaTestHelper.toStringOn(m2)
        }

        def m3 = mock(Date, name('m3'))
        def m4 = mock(Date)
        def m5 = mock(Date)

        play {
            assertEquals 'Mock for Object', m1.toString()
            assertEquals 'Mock for Date (1)', m2.toString()
            assertEquals 'm3', m3.toString()
            assertEquals 'Mock for Date (2)', m4.toString()
            assertEquals 'Mock for Date (3)', m5.toString()

            assertEquals 'Mock for Object', JavaTestHelper.toStringOn(m1)
            assertEquals 'Mock for Date (1)', JavaTestHelper.toStringOn(m2)
            assertEquals 'm3', JavaTestHelper.toStringOn(m3)
            assertEquals 'Mock for Date (2)', JavaTestHelper.toStringOn(m4)
            assertEquals 'Mock for Date (3)', JavaTestHelper.toStringOn(m5)
        }
    }

}
