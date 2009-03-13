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
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import org.gmock.utils.JavaLoader
import static org.hamcrest.Matchers.*

class FunctionalTest extends GMockTestCase {

    void testBasic() {
        def mockLoader = mock()
        mockLoader.load('key').returns('value')
        play {
            assertEquals "value", mockLoader.load('key')
        }
    }

    void testCallMethodTooManyTime() {
        def mockLoader = mock()
        mockLoader.load('foo').returns('result1')
        mockLoader.load('bar').returns('result1')
        mockLoader.load('bar').returns('result1')
        def message
        play {
            try {
                mockLoader.load("foo")
                mockLoader.load("bar")
                mockLoader.load("bar")
                mockLoader.load("bar")
                fail("Should have throw an exception")
            } catch (AssertionFailedError e) {
                message = e.message
            }
        }
        def expected = "Unexpected method call 'load(\"bar\")'\n" +
                       "  'load(\"foo\")': expected 1, actual 1\n" +
                       "  'load(\"bar\")': expected 2, actual 2 (+1)"
        assertEquals expected, message
    }

    void testMultipleArguments() {
        def mockLoader1 = mock()
        def mockLoader2 = mock()
        mockLoader1.put("1A", "1B").returns("result1")
        mockLoader2.put("2A", "2B").returns("result2")

        play {
            assertEquals "result1", mockLoader1.put("1A", "1B")
            assertEquals "result2", mockLoader2.put("2A", "2B")
        }
    }

    void testMultipleArgumentsSameMock() {
        def mockLoader = mock()
        mockLoader.load("key1", "other1").returns("key1 other1")
        mockLoader.load("key2", "other2").returns("key2 other2")
        play {
            assertEquals "key1 other1", mockLoader.load("key1", "other1")
            assertEquals "key2 other2", mockLoader.load("key2", "other2")
        }
    }

    void testMethodNotCall() {
        def mockLoader = mock()
        mockLoader.load("load1").returns("result")
        mockLoader.load("load2").returns("result")
        mockLoader.load("load2").returns("result")
        def expected = "Expectation not matched on verify:\n" +
                       "  'load(\"load1\")': expected 1, actual 0\n" +
                       "  'load(\"load2\")': expected 2, actual 0"

        try {
            play {}
            fail("Should have throw an exception")
        } catch (AssertionFailedError e) {
            assertEquals expected, e.message
        }
    }

    void testMultipleExpectation() {
        def mockLoader = mock()
        mockLoader.load("key").returns("result")
        mockLoader.put("key")
        play {
            assertEquals "result", mockLoader.load("key")
            assertNull mockLoader.put("key")
        }
    }

    void testCallNotExpected() {
        def mockLoader = mock()
        play {
            try {
                mockLoader.load("key")
                fail("Should have throw an exception")
            } catch (AssertionFailedError e) {
                assertEquals "Unexpected method call 'load(\"key\")'", e.message
            }
        }
    }

    void testSameExpectation() {
        def mockLoader = mock()
        mockLoader.load('key').returns('first')
        mockLoader.load('key').returns('second')
        play {
            assertEquals "first", mockLoader.load("key")
            assertEquals "second", mockLoader.load("key")
        }
    }

    void testStubMethod() {
        def mockLoader = mock()
        mockLoader.load("key").returns("stub").stub()
        play {
            3.times {
                assertEquals 'stub', mockLoader.load('key')
            }
        }
    }

    void testStubedMethodNotCall() {
        def mockLoader = mock()
        mockLoader.load("key").returns("stub").stub()
        play {}
    }

    void testRaiseException() {
        def mockLoader = mock()
        mockLoader.load("key").raises(new IllegalArgumentException())
        play {
            shouldFail(IllegalArgumentException) {
                mockLoader.load("key")
            }
        }
    }

    void testRaiseExceptionNotCalled() {
        def mockLoader = mock()
        mockLoader.load("key").raises(new IllegalArgumentException())
        def expected = "Expectation not matched on verify:\n" +
                       "  'load(\"key\")': expected 1, actual 0"

        try {
            play {}
        } catch (AssertionFailedError e) {
            assertEquals expected, e.message
        }
    }

    void testRaiseExceptionClass() {
        def mockLoader = mock()
        mockLoader.load("key").raises(IllegalArgumentException)

        play {
            shouldFail(IllegalArgumentException) {
                mockLoader.load("key")
            }
        }
    }

    void testRaiseExceptionClassWithMessage() {
        def mockLoader = mock()
        mockLoader.load("key").raises(IllegalArgumentException, "error message")

        play {
            def expected = "error message"
            def message = shouldFail(IllegalArgumentException) {
                mockLoader.load("key")
            }
            assertEquals expected, message
        }
    }

    void testRaiseExceptionClassWithMessageAndCause() {
        def mockLoader = mock()
        def message = "error message"
        def cause = new Exception()
        mockLoader.load("key").raises(IllegalArgumentException, message, cause)

        play {
            try {
                mockLoader.load("key")
                fail("Should have throw an exception")
            } catch (IllegalArgumentException e) {
                assertEquals message, e.message
                assertEquals cause, e.cause
            }
        }
    }

    void testRaiseExceptionClassNotCalled() {
        def mockLoader = mock()
        mockLoader.load("key").raises(IllegalArgumentException)
        def expected = "Expectation not matched on verify:\n" +
                       "  'load(\"key\")': expected 1, actual 0"

        try {
            play {}
        } catch (AssertionFailedError e) {
            assertEquals expected, e.message
        }
    }

    void testMultiplePlay() {
        def mockLoader = mock()
        mockLoader.load("key1").returns("value1")
        play {
            assertEquals "value1", mockLoader.load("key1")
        }

        mockLoader.load("key2").returns("value2")
        mockLoader.put("value3")
        play {
            assertEquals "value2", mockLoader.load("key2")
            assertNull mockLoader.put("value3")
        }
    }

    void testHamcrestMatcher() {
        def mockLoader = mock()
        mockLoader.put("test", is(not(lessThan(5)))).returns("correct")

        play {
            assertEquals "correct", mockLoader.put("test", 10)
        }
    }

    void testHamcrestMatcherNotMatched() {
        def mockLoader = mock()
        mockLoader.load(is(not("test"))).returns("correct")
        def expected = "Unexpected method call 'load(\"test\")'\n" +
                       "  'load(is not \"test\")': expected 1, actual 0"

        def message = shouldFail(AssertionFailedError) {
            play {
                mockLoader.load("test")
            }
        }
        assertEquals expected, message
    }

    void testClosureMatcher() {
        def mockLoader = mock()
        mockLoader.put("test", match { it > 5 }).returns("correct")

        play {
            assertEquals "correct", mockLoader.put("test", 10)
        }
    }

    void testClosureMatcherNotMatched() {
        def mockLoader = mock()
        mockLoader.load(match { it != "test" }).returns("correct")
        def expected = "Unexpected method call 'load(\"test\")'\n" +
                       "  'load(a value matching the closure matcher)': expected 1, actual 0"

        def message = shouldFail(AssertionFailedError) {
            play {
                mockLoader.load("test")
            }
        }
        assertEquals expected, message
    }

    void testRangeTimes() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).times(1..2)
        play {
            assertEquals 2, mockLoader.load(1)
        }

        mockLoader.load(1).returns(2).times(1..2)
        play {
            2.times {
                assertEquals 2, mockLoader.load(1)
            }
        }
    }

    void testRangeTimesTooFew() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).times(1..2)
        def expected = "Expectation not matched on verify:\n" +
                       "  'load(1)': expected 1..2, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {}
        }
        assertEquals expected, message
    }

    void testRangeTimesTooMany() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).times(1..2)
        def expected = "Unexpected method call 'load(1)'\n" +
                       "  'load(1)': expected 1..2, actual 2 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                3.times { mockLoader.load(1) }
            }
            assertEquals expected, message
        }
    }

    void testStrictTimes() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).times(2)

        play {
            2.times {
                assertEquals 2, mockLoader.load(1)
            }
        }
    }

    void testStrictTimesTooFew() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).times(2)
        def expected = "Expectation not matched on verify:\n" +
                       "  'load(1)': expected 2, actual 1"
        def message = shouldFail(AssertionFailedError) {
            play {
                assertEquals 2, mockLoader.load(1)
            }
        }
        assertEquals expected, message
    }

    void testStrictTimesTooMany() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).times(2)
        def expected = "Unexpected method call 'load(1)'\n" +
                       "  'load(1)': expected 2, actual 2 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                3.times { mockLoader.load(1) }
            }
            assertEquals expected, message
        }
    }

    void testNever() {
        def mockLoader = mock()
        mockLoader.load(1).never()
        play {}
    }

    void testNeverTooMany() {
        def mockLoader = mock()
        mockLoader.load(1).never()
        def expected = "Unexpected method call 'load(1)'\n" +
                       "  'load(1)': expected never, actual 0 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                mockLoader.load(1)
            }
            assertEquals expected, message
        }
    }

    void testOnce() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).once()
        play {
            assertEquals 2, mockLoader.load(1)
        }
    }

    void testAtLeast() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).atLeast(2)
        play {
            2.times {
                assertEquals 2, mockLoader.load(1)
            }
        }

        mockLoader.load(1).returns(3).atLeast(2)
        play {
            3.times {
                assertEquals 3, mockLoader.load(1)
            }
        }
    }

    void testAtLeastTooFew() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).atLeast(2)
        def expected = "Expectation not matched on verify:\n" +
                       "  'load(1)': expected at least 2, actual 1"
        def message = shouldFail(AssertionFailedError) {
            play {
                assertEquals 2, mockLoader.load(1)
            }
        }
        assertEquals expected, message
    }

    void testAtLeastOnce() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).atLeastOnce()
        play {
            assertEquals 2, mockLoader.load(1)
        }

        mockLoader.load(2).returns(3).atLeastOnce()
        play {
            2.times {
                assertEquals 3, mockLoader.load(2)
            }
        }
    }

    void testAtMost() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).atMost(3)
        play {
            2.times {
                assertEquals 2, mockLoader.load(1)
            }
        }

        mockLoader.load(2).returns(3).atMost(3)
        play {
            3.times {
                assertEquals 3, mockLoader.load(2)
            }
        }
    }

    void testAtMostTooMany() {
        def mockLoader = mock()
        mockLoader.load(3).returns(4).atMost(2)
        def expected = "Unexpected method call 'load(3)'\n" +
                       "  'load(3)': expected at most 2, actual 2 (+1)"
        def message = shouldFail(AssertionFailedError) {
            play {
                3.times { mockLoader.load(3) }
            }
        }
        assertEquals expected, message
    }

    void testAtMostOnce() {
        def mockLoader = mock()
        mockLoader.load(5).returns(5).atMostOnce()
        play {}

        mockLoader.load(5).returns(5).atMostOnce()
        play {
            assertEquals 5, mockLoader.load(5)
        }
    }

    void testNonfixedTimesAfterFixedTimes() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).times(3)
        mockLoader.put(9, 9).times(1)
        mockLoader.load(1).returns(3).times(2)
        mockLoader.load(1).returns(4).atMost(2)
        mockLoader.put(9, 9).times(1..2)

        play {
            3.times {
                assertEquals 2, mockLoader.load(1)
            }
            2.times {
                assertEquals 3, mockLoader.load(1)
            }
            assertEquals 4, mockLoader.load(1)

            2.times {
                mockLoader.put(9, 9)
            }
        }
    }

    void testNonfixedTimesAfterFixedTimesFailed() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).times(3)
        mockLoader.put(9, 9).times(1)
        mockLoader.load(1).returns(3).times(2)
        mockLoader.load(1).returns(4).atLeast(2)
        mockLoader.put(9, 9).times(1..2)

        def expected = "Expectation not matched on verify:\n" +
                       "  'load(1)': expected at least 7, actual 6\n" +
                       "  'put(9, 9)': expected 2..3, actual 2"
        def message = shouldFail(AssertionFailedError) {
            play {
                3.times {
                    assertEquals 2, mockLoader.load(1)
                }
                2.times {
                    assertEquals 3, mockLoader.load(1)
                }
                assertEquals 4, mockLoader.load(1)

                2.times {
                    mockLoader.put(9, 9)
                }
            }
        }
        assertEquals expected, message
    }

    void testAnythingAfterNonfixedTimes() {
        def mockLoader = mock()
        mockLoader.load(1).returns(2).times(3)
        mockLoader.put(9, 9).times(1)
        mockLoader.load(1).returns(3).times(2)
        mockLoader.load(1).returns(4).stub()
        mockLoader.put(9, 9).never()

        shouldFail(IllegalStateException) {
            mockLoader.load(1).returns(5).times(2)
        }
        shouldFail(IllegalStateException) {
            mockLoader.put(9, 9).atLeastOnce()
        }
    }

    void testPassAMockObjectToAnother() {
        def mock1 = mock()
        def mock2 = mock()
        mock1.is(mock2).returns(true)

        play {
            assert mock1.is(mock2)
        }
    }

    void testPassAMockObjectToAnotherButUnexpectedCalled() {
        def mock1 = mock()
        def mock2 = mock()
        def mock3 = mock()
        mock1.is(mock2).returns(true)

        def expected = "Unexpected method call 'is(Mock for Object (3))' on 'Mock for Object (1)'\n" +
                       "  'is(Mock for Object (2))' on 'Mock for Object (1)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                mock1.is(mock3)
            }
        }
        assertEquals expected, message
    }

    void testPassAMockObjectToAnotherButNotCalled() {
        def mock1 = mock()
        def mock2 = mock()
        mock1.is(mock2).returns(true)

        def expected = "Expectation not matched on verify:\n" +
                       "  'is(Mock for Object (2))' on 'Mock for Object (1)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {}
        }
        assertEquals expected, message
    }

    void testMockObjectAsClosureDelegateWithOwnerFirstStrategy() {
        def mock = mock()
        mock.mockMethod().returns("correct")
        mock.mockProperty.set(1)
        mock.mockProperty.returns(2)

        play {
            def closure = {
                assertEquals "correct", mockMethod()
                mockProperty = 1
                assertEquals 2, mockProperty
            }
            closure.delegate = mock
            closure.resolveStrategy = Closure.OWNER_FIRST
            closure()
        }
    }

    void testMockObjectAsClosureDelegateWithDelegateFirstStrategy() {
        def mock = mock()
        mock.mockMethod().returns("correct")
        mock.mockProperty.set(3)
        mock.mockProperty.returns(4)

        play {
            def closure = {
                this.assertEquals "correct", mockMethod()
                mockProperty = 3
                this.assertEquals 4, mockProperty
            }
            closure.delegate = mock
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            closure()
        }
    }

    void testMockObjectAsClosureDelegateWithDelegateOnlyStrategy() {
        def mock = mock()
        mock.mockMethod().returns("correct")
        mock.mockProperty.set(5)
        mock.mockProperty.returns(6)

        play {
            def closure = {
                this.assertEquals "correct", mockMethod()
                mockProperty = 5
                this.assertEquals 6, mockProperty
            }
            closure.delegate = mock
            closure.resolveStrategy = Closure.DELEGATE_ONLY
            closure()
        }
    }

    void testShouldResetAfterThrowingExceptionForUnexpectedCall() {
        def mock = mock()
        mock.hashCode().returns(1)

        shouldFail(AssertionFailedError) {
            play {
                mock.toString()
            }
        }

        play {}
    }

    void testShouldResetAfterThrowingExceptionForVerifyFail() {
        def mock = mock()
        mock.hashCode().returns(1)

        shouldFail(AssertionFailedError) {
            play {}
        }

        play {}
    }

    void testShouldNotAllowCreatingMocksInPlayClosure() {
        play {
            shouldFail(IllegalStateException) {
                mock()
            }
        }
    }

    void testShouldNotAllowNestingPlayClosures() {
        play {
            shouldFail(IllegalStateException) {
                play {}
            }
        }
    }

    void testMockingClosureCalls() {
        def closure = mock(Closure)
        closure.call(1, 2).returns(3)
        play {
            assertEquals 3, closure(1, 2)
        }

        closure.call().returns("correct")
        play {
            assertEquals "correct", closure()
        }
    }

    void testAssigningANonTypeVariableToATypeVariableGiveBadErrorMessage() {
        def expected = /Cannot cast object 'Mock for Object' with class 'groovy\.lang\.GroovyObject.*' / +
                       /to class 'java\.util\.Date'/
        def message = shouldFail(GroovyCastException) {
            Date date = mock()
        }
        assert message ==~ expected
    }

    void testAssigningANonTypeVariableToATypeVariableGiveBadErrorMessageWithMockName() {
        def expected = /Cannot cast object 'now' with class 'groovy\.lang\.GroovyObject.*' / +
                       /to class 'java\.util\.Date'/
        def message = shouldFail(GroovyCastException) {
            Date date = mock(name("now"))
        }
        assert message ==~ expected
    }

    void testWithDelegateToMock(){
        def mockLoader = mock()

        with(mockLoader){
            load("key").returns("number")
            count.returns(1)
        }
        play{
            assertEquals("number", mockLoader.load("key"))
            assertEquals(1, mockLoader.count)
        }
    }

    void testMockName() {
        def mock = mock(Date, name("date mock"))
        play {
          assertEquals "date mock", mock.toString()
        }
    }

    void testMockNameShouldBeInErrorMessage() {
        def m1 = mock()
        def m2 = mock(name("test mock"))
        m1.is(m2).returns(false)

        def expected = "Expectation not matched on verify:\n" +
                       "  'is(test mock)' on 'Mock for Object': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {}
        }
        assertEquals expected, message
    }

    void testMockNameWithOtherArguments() {
        def m1 = mock(JavaLoader, name("m1"), invokeConstructor("test"))
        mock(JavaLoader, constructor("test"), name("m2")) {
            something().returns(true)
        }
        play {
            assertEquals "m1", m1.toString()
            assertEquals "test", m1.@name
            def m2 = new JavaLoader("test")
            assertTrue m2.something()
            assertEquals "m2", m2.toString()
        }
    }

    void testInvalidMockMethodsShouldFail() {
        shouldFail(MissingMethodException) {
            mock(JavaLoader, name("1"), name("2"))
        }
        shouldFail(MissingMethodException) {
            mock(JavaLoader, constructor(), constructor())
        }
        shouldFail(MissingMethodException) {
            mock(JavaLoader, invokeConstructor(), invokeConstructor())
        }
        shouldFail(MissingMethodException) {
            mock(JavaLoader) {} {}
        }
        shouldFail(MissingMethodException) {
            mock(JavaLoader, "3")
        }
    }

    void testUnexpectedMethodCallErrorMessageWithMultipleMocks() {
        def m1 = mock(Date), m2 = mock(), m3 = mock(Date, name('now')), m4 = mock(Date)
        m4.a()
        m3.b.returns(1)
        m2.c.set(2)
        m1.setD(true)
        m1.isD().returns(true)
        m1.getD().returns(true)
        mock(Date, constructor('now')).static {
            e()
            f.returns(3)
            g.set(4)
        }

        def expected = "Unexpected method call 'z()' on 'Mock for Date (1)'\n" +
                       "  'a()' on 'Mock for Date (2)': expected 1, actual 0\n" +
                       "  'b' on 'now': expected 1, actual 0\n" +
                       "  'c = 2' on 'Mock for Object': expected 1, actual 0\n" +
                       "  'setD(true)' on 'Mock for Date (1)': expected 1, actual 0\n" +
                       "  'isD()' on 'Mock for Date (1)': expected 1, actual 0\n" +
                       "  'getD()' on 'Mock for Date (1)': expected 1, actual 0\n" +
                       "  'new Date(\"now\")': expected 1, actual 0\n" +
                       "  'Date.e()': expected 1, actual 0\n" +
                       "  'Date.f': expected 1, actual 0\n" +
                       "  'Date.g = 4': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                m1.z()
            }
        }
        assertEquals expected, message
    }

    void testRegexMethodName() {
        def mock = mock()
        mock./[a-z]/().returns(0)
        mock./get.*/().returns(1)
        play {
            assertEquals 0, mock.k()
            assertEquals 1, mock.getSomething()
        }
    }

    void testNoCrossUsageToPropertyGetterWithRegexMethodName() {
        def mock = mock()
        mock./getA.*/().returns(0)
        def expected = "Unexpected property getter call 'abc'\n" +
                       "  '\"getA.*\"()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                mock.abc
            }
        }
        assertEquals expected, message
    }

    void testNoCrossUsageToPropertySetterWithRegexMethodName() {
        def mock = mock()
        mock./setX.*/(1)
        def expected = "Unexpected property setter call 'xyz = 1'\n" +
                       "  '\"setX.*\"(1)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                mock.xyz = 1
            }
        }
        assertEquals expected, message
    }

    void testRegexPropertyName() {
        def mock = mock()
        mock./\w{4}\d/.returns(1).times(2)
        mock./(?i)gmock\djava/.set('cool').times(2)
        play {
            assertEquals 1, mock.user1
            assertEquals 1, mock.getUser1()
            mock.gmock4Java = 'cool'
            mock.setGMock4Java('cool')
        }
    }

    void testRegexPropertyNameAndFailed() {
        def mock = mock()
        mock./a.*/.returns(1)
        def expected = "Unexpected property getter call 'bbb'\n" +
                       "  '\"a.*\"': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                mock.bbb
            }
        }
        assertEquals expected, message
    }

    void testMultipleReturnsOnOneExpectation() {
        def mock = mock()
        mock.fun().returns(1).returns(2)
        play {
            assertEquals 1, mock.fun()
            assertEquals 2, mock.fun()
        }
    }

    void testMultipleTimesOnOneExpectation() {
        def mock = mock()
        mock.fun().times(2).times(3)
        mock.foo().returns(1).times(2).atLeastOnce()
        play {
            5.times { mock.fun() }
            4.times {
                assertEquals 1, mock.foo()
            }
        }
    }

    void testMultipleReturnsAndTimesOnOneExpectation() {
        def mock = mock()
        mock.fun().returns(1).times(2).returns(2).times(3).returns(3).times(4)
        play {
            (1..3).each { n ->
                (n + 1).times {
                    assertEquals n, mock.fun()
                }
            }
        }
    }

    void testMultipleReturnsAndRaisesOnOneExpectation() {
        def mock = mock()
        mock.fun().returns(1).raises(RuntimeException).times(2)
        play {
            assertEquals 1, mock.fun()
            2.times {
                shouldFail(RuntimeException) { mock.fun() }
            }
        }
    }

    void testMultipleReturnsOnOneExpectationShouldCheckTimes() {
        def mock = mock()
        def recorder = mock.fun().times(1).times(1..2)
        shouldFail(IllegalStateException) {
            recorder.once()
        }
    }

    void testDelegateOfWithClosureShouldBehaveTheSame() {
        def s = mock()
        with(s) {
            it.match(match{it}, delegate.match{it}).returns('yes')
        }
        play {
            assertEquals 'yes', s.match {true}{true}
        }
    }

}
