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
import org.gmock.utils.Loader
import static org.hamcrest.Matchers.*

class FunctionnalPropertyTest extends GMockTestCase {

    void testBasic(){
        def mockLoader = mock()
        mockLoader.name.returns('a name')
        mockLoader.name.returns('a different name')
        mockLoader.name.set('another name')

        play {
            assertEquals "a name", mockLoader.name
            assertEquals "a different name", mockLoader.name
            mockLoader.name = 'another name'
            return null // prevent the getter of name from being invoked for evaluating the result of closure
        }
    }

    void testMultiplePropertyAndMethod(){
        def mockLoader = mock()
        mockLoader.name.returns('a name')
        mockLoader.name.set('another name')
        mockLoader.id.set('some id')
        mockLoader.load("fruit").returns("apple")

        play {
            mockLoader.id = "some id"
            assertEquals "a name", mockLoader.name
            mockLoader.name = 'another name'
            assertEquals "apple", mockLoader.load("fruit")
        }
    }

    void testUnexpectedPropertyCall(){
        def mockLoader = mock()

        mockLoader.name.returns('a name')
        mockLoader.id.set('some id')
        mockLoader.load("fruit").returns("apple")

        try {
            play {
                assertEquals "a name", mockLoader.name
                mockLoader.unexpected = 10
            }
            fail("Should throw an exception")
        } catch (AssertionFailedError e){
            def expected = "Unexpected property setter call 'unexpected = 10'\n"+
                           "  'name': expected 1, actual 1\n"+
                           "  'id = \"some id\"': expected 1, actual 0\n"+
                           "  'load(\"fruit\")': expected 1, actual 0"

            assertEquals expected, e.message
        }
    }

    void testExpectationNotVerified(){
        def mockLoader = mock()

        mockLoader.name.returns('a name')
        mockLoader.id.set('some id')

        try {
            play {
                assertEquals "a name", mockLoader.name
            }
        } catch (AssertionFailedError e){
            def expected = "Expectation not matched on verify:\n" +
                   "  'name': expected 1, actual 1\n" +
                   "  'id = \"some id\"': expected 1, actual 0"
            assertEquals expected, e.message
        }
    }

    void testStubGetter(){
        def mockLoader = mock()

        mockLoader.name.returns('a name').stub()

        play {
            assertEquals "a name", mockLoader.name
            assertEquals "a name", mockLoader.name
        }
    }

    void testStubGetterNotCalled(){
        def mockLoader = mock()
        mockLoader.name.returns('a name').stub()
        play {}
    }

    void testStubSetter(){
        def mockLoader = mock()

        mockLoader.name.set('a value').stub()

        play {
            mockLoader.name = "a value"
            mockLoader.name = "a value"
            return null // prevent the getter of name from being invoked for evaluating the result of closure
        }
    }

    void testStubSetterNotCalled(){
        def mockLoader = mock()
        mockLoader.name.set('a value').stub()
        play {}
    }


    void testGetPropertyRaisesException(){
        def mockLoader = mock()

        mockLoader.name.raises(new RuntimeException("An exception"))

        play {
            def message = shouldFail(RuntimeException) {
                mockLoader.name
            }
            assertEquals "An exception", message
        }
    }

    void testGetPropertyRaisesExceptionWithMessage() {
        def mockLoader = mock()

        mockLoader.name.raises(RuntimeException, "test")

        play {
            def message = shouldFail(RuntimeException) {
                mockLoader.name
            }
            assertEquals "test", message
        }
    }

    void testSetPropertyRaisesException(){
        def mockLoader = mock()

        mockLoader.name.set("a name")
        mockLoader.name.set("a invalid name").raises(new RuntimeException("An exception"))

        play {
            mockLoader.name = "a name"
            def message = shouldFail(RuntimeException) {
                mockLoader.name = "a invalid name"
            }
            assertEquals "An exception", message
        }
    }

    void testSetPropertyRaisesExceptionWithMessage() {
        def mockLoader = mock()

        mockLoader.name.set("invalid").raises(RuntimeException, "test")

        play {
            def message = shouldFail(RuntimeException) {
                mockLoader.name = "invalid"
            }
            assertEquals "test", message
        }
    }

    void testLastPropertySetDontGetEvaluated(){
        def mockLoader = mock()
        mockLoader.name.set('another name')
        play {
            setName(mockLoader)
        }
    }

    void setName(mockLoader){
        mockLoader.name = 'another name'
    }

    void testSetPropertyHamcrestMatcher() {
        def mockLoader = mock()
        mockLoader.test.set(is(not("wrong")))

        play {
            mockLoader.test = "correct"
            return null // prevent the getter of test from being invoked for evaluating the result of closure
        }
    }

    void testSetPropertyHamcrestMatcherNotMatched() {
        def mockLoader = mock()
        mockLoader.test.set(isOneOf(1, 2, 3))
        def expected = "Unexpected property setter call 'test = 0'\n" +
                       "  'test = one of {<1>, <2>, <3>}': expected 1, actual 0"

        def message = shouldFail(AssertionFailedError) {
            play {
                mockLoader.test = 0
                return null // prevent the getter of test from being invoked for evaluating the result of closure
            }
        }
        assertEquals expected, message
    }

    void testSetPropertyClosureMatcher() {
        def mockLoader = mock()
        mockLoader.test.set(match { it != "wrong" })

        play {
            mockLoader.test = "correct"
            return null // prevent the getter of test from being invoked for evaluating the result of closure
        }
    }

    void testSetPropertyClosureMatcherNotMatched() {
        def mockLoader = mock()
        mockLoader.test.set(match { it in [1, 2, 3] })
        def expected = "Unexpected property setter call 'test = 0'\n" +
                       "  'test = a value matching the closure matcher': expected 1, actual 0"

        def message = shouldFail(AssertionFailedError) {
            play {
                mockLoader.test = 0
                return null // prevent the getter of test from being invoked for evaluating the result of closure
            }
        }
        assertEquals expected, message
    }

    void testSetPropertyToNull() {
        def mockLoader = mock()
        mockLoader.test.set(null)

        play {
            mockLoader.test = null
            return null // prevent the getter of test from being invoked for evaluating the result of closure
        }
    }

    void testRangeTimes() {
        def mockLoader = mock()
        mockLoader.id.returns(1).times(1..2)
        play {
            assertEquals 1, mockLoader.id
        }

        mockLoader.id.returns(2).times(1..2)
        play {
            2.times {
                assertEquals 2, mockLoader.id
            }
        }
    }

    void testRangeTimesTooMany() {
        def mockLoader = mock()
        mockLoader.id.returns(3).times(1..2)
        def expected = "Unexpected property getter call 'id'\n" +
                       "  'id': expected 1..2, actual 2 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                3.times {
                    assertEquals 3, mockLoader.id
                }
            }
            assertEquals expected, message
        }
    }

    void testStrictTimes() {
        def mockLoader = mock()
        mockLoader.id.set(4).times(2)
        mockLoader.id.returns(5).once()

        play {
            mockLoader.id = 4
            mockLoader.id = 4
            assertEquals 5, mockLoader.id
        }
    }

    void testStrictTimesTooMany() {
        def mockLoader = mock()
        mockLoader.id.set(6).times(2)
        def expected = "Unexpected property setter call 'id = 6'\n" +
                       "  'id = 6': expected 2, actual 2 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                3.times {
                    mockLoader.id = 6
                    return null
                }
            }
            assertEquals expected, message
        }
    }

    void testNever() {
        def mockLoader = mock()
        mockLoader.id.set(7).never()
        play {}
    }

    void testNeverTooMany() {
        def mockLoader = mock()
        mockLoader.id.set(8).never()
        def expected = "Unexpected property setter call 'id = 8'\n" +
                       "  'id = 8': expected never, actual 0 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                mockLoader.id = 8
            }
            assertEquals expected, message
        }
    }

    void testAtLeast() {
        def mockLoader = mock()
        mockLoader.id.set(9).atLeast(2)
        mockLoader.id.returns(10).atLeastOnce()

        play {
            mockLoader.id = 9
            mockLoader.id = 9
            assertEquals 10, mockLoader.id
        }
    }

    void testAtLeastTooFew() {
        def mockLoader = mock()
        mockLoader.id.set(11).atLeastOnce()
        def expected = "Expectation not matched on verify:\n" +
                       "  'id = 11': expected at least 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {}
        }
        assertEquals expected, message
    }

    void testAtMost() {
        def mockLoader = mock()
        mockLoader.id.set(12).atMost(2)
        mockLoader.id.returns(13).atMostOnce()

        play {
            mockLoader.id = 12
            mockLoader.id = 12
            assertEquals 13, mockLoader.id
        }
    }

    void testAtMostTooMany() {
        def mockLoader = mock()
        mockLoader.id.returns(14).atMost(2)
        def expected = "Unexpected property getter call 'id'\n" +
                       "  'id': expected at most 2, actual 2 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                3.times {
                    assertEquals 14, mockLoader.id
                }
            }
            assertEquals expected, message
        }
    }

    void testNonfixedTimesAfterFixedTimes() {
        def mockLoader = mock()
        mockLoader.id.set(15).once()
        mockLoader.id.set(15).times(2)
        mockLoader.id.returns(16).times(3)
        mockLoader.id.set(15).atMost(3)
        mockLoader.id.returns(16).never()

        play {
            4.times {
                mockLoader.id = 15
                return null
            }
            3.times {
                assertEquals 16, mockLoader.id
            }
        }
    }

    void testNonfixedTimesAfterFixedTimesFailed() {
        def mockLoader = mock()
        mockLoader.id.set(15).once()
        mockLoader.id.set(15).times(2)
        mockLoader.id.returns(16).times(3)
        mockLoader.id.set(15).atMost(3)
        mockLoader.id.returns(16).never()

        def expected = "Unexpected property getter call 'id'\n" +
                       "  'id = 15': expected at most 6, actual 4\n" +
                       "  'id': expected 3, actual 3 (+1)"
        play {
            4.times {
                mockLoader.id = 15
                return null
            }
            3.times {
                assertEquals 16, mockLoader.id
            }
            def message = shouldFail(AssertionFailedError) {
                assertEquals 16, mockLoader.id
            }
            assertEquals expected, message
        }
    }

    void testAnythingAfterNonfixedTimes() {
        def mockLoader = mock()
        mockLoader.id.set(15).once()
        mockLoader.id.set(15).times(2)
        mockLoader.id.returns(16).times(3)
        mockLoader.id.set(15).atLeastOnce()
        mockLoader.id.returns(16).times(1..2)

        shouldFail(IllegalStateException) {
            mockLoader.id.set(15)
        }
        shouldFail(IllegalStateException) {
            mockLoader.id.returns(16).never()
        }
    }

    void testAssignPropertyInRecordState() {
        def mockLoader = mock()
        def expected = "Cannot use property setter in record mode. Are you trying to mock a setter? Use 'id.set(\"1\")' instead."
        def message = shouldFail(MissingPropertyException) {
            mockLoader.id = "1"
        }
        assertEquals expected, message
    }

    void testNonCompletePropertyExpectationsAreInvalid() {
        def mockLoader = mock()
        mockLoader.nonCompleteProperty

        def expected = "Missing property expectation for 'nonCompleteProperty'"
        def message = shouldFail(IllegalStateException) {
            play {}
        }
        assertEquals expected, message
    }

    void testStaticProperty() {
        def mockLoader = mock(Loader)
        mockLoader.static.name.set("name")
        mockLoader.static.name.returns("another name")
        mockLoader.static.number.set(match { it > 1 })
        mockLoader.static.something.returns(1).times(3)
        mockLoader.static.error.raises(RuntimeException)
        mockLoader.static.error.set(0).raises(IllegalStateException)

        play {
            Loader.name = "name"
            assertEquals "another name", Loader.name
            Loader.number = 10
            3.times {
                assertEquals 1, Loader.something
            }
            shouldFail(RuntimeException) {
                Loader.error
            }
            shouldFail(IllegalStateException) {
                Loader.error = 0
            }
        }
    }

    void testStaticPropertyFailed() {
        def mockLoader = mock(Loader)
        mockLoader.static.a.returns(1)
        mockLoader.static.b.set(2)

        def expected = "Expectation not matched on verify:\n" +
                       "  'Loader.a': expected 1, actual 0\n" +
                       "  'Loader.b = 2': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {}
        }
        assertEquals expected, message
    }

    void testNonCompleteStaticPropertyExpectationsAreInvalid() {
        def mockLoader = mock(Loader)
        mockLoader.static.something

        def expected = "Missing property expectation for 'Loader.something'"
        def message = shouldFail(IllegalStateException) {
            play {}
        }
        assertEquals expected, message
    }

    void testAssignStaticPropertyInRecordState() {
        def mockLoader = mock(Loader)
        def expected = "Cannot use property setter in record mode. Are you trying to mock a setter? Use 'static.something.set([:])' instead."
        def message = shouldFail(MissingPropertyException) {
            mockLoader.static.something = [:]
        }
        assertEquals expected, message
    }

}
