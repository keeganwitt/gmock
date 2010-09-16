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
import static org.hamcrest.Matchers.*
import org.gmock.utils.Loader

class FunctionnalStaticMethodsTest extends GMockTestCase {

    void testSimpleMethod(){
        def mockLoader = mock(Loader)
        mockLoader.static.one().returns("two")

        play {
            assertEquals "two", Loader.one()
        }
    }

    void testNoReturn(){
        def mockLoader = mock(Loader)
        mockLoader.static.one()

        play {
            assertNull(Loader.one())
        }
    }

    void testMultipleArguments(){
        def mockMath = mock(Math)
        mockMath.static.max(2,5).returns(0)
        mockMath.static.max(3,6).returns(1)
        mockMath.static.random().returns(0.5)

        play {
            assertEquals 1, Math.max(3,6)
            assertEquals 0, Math.max(2,5)
            assertEquals 0.5, Math.random()
        }
    }

    void testConstructorCanStillBeCalled(){
        def mockLoader = mock(Loader)
        mockLoader.static.one().returns("two")

        play {
            assertEquals "two", Loader.one()
            assertTrue new Loader() instanceof Loader
        }
    }

    void testMethodNotCalled(){
        def mockLoader = mock(Loader)
        mockLoader.static.one().returns("one")
        mockLoader.static.one().returns("other")
        mockLoader.static.two().returns("two")

        try {
            play {
                assertEquals "one", Loader.one()
                assertEquals "two", Loader.two()
            }
        } catch (AssertionFailedError e){
            def expected = "Expectation not matched on verify:\n" +
                        "  'Loader.one()': expected 2, actual 1\n" +
                        "  'Loader.two()': expected 1, actual 1"
            assertEquals expected, e.message
        }
    }

    void testMethodNotExpexted(){
        def mockLoader = mock(Loader)
        mockLoader.static.one().returns("one")

        try {
            play {
                assertEquals "two", Loader.two()
            }
        } catch (AssertionFailedError e){
            def expected = "Unexpected static method call 'Loader.two()'\n" +
                           "  'Loader.one()': expected 1, actual 0"
            assertEquals expected, e.message
        }
    }

    void testStaticMethodHamcrestMatcher() {
        def mockLoader = mock(Loader)
        mockLoader.static.check(isIn(1..5), is("test")).returns("correct")

        play {
            assertEquals "correct", Loader.check(4, "test")
        }
    }

    void testStaticMethodHamcrestMatcherNotMatched() {
        def mockLoader = mock(Loader)
        mockLoader.static.check(isIn(1..5)).returns("correct")
        def expected = "Unexpected static method call 'Loader.check(0)'\n" +
                       "  'Loader.check(one of {<1>, <2>, <3>, <4>, <5>})': expected 1, actual 0"

        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.check(0)
            }
        }
        assertEquals expected, message
    }

    void testStaticMethodClosureMatcher() {
        def mockLoader = mock(Loader)
        mockLoader.static.check(match { it in 1..5 }, match { it == "test" }).returns("correct")

        play {
            assertEquals "correct", Loader.check(4, "test")
        }
    }

    void testStaticMethodClosureMatcherNotMatched() {
        def mockLoader = mock(Loader)
        mockLoader.static.check(match { it in 1..5 }).returns("correct")
        def expected = "Unexpected static method call 'Loader.check(0)'\n" +
                       "  'Loader.check(a value matching the closure matcher)': expected 1, actual 0"

        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.check(0)
            }
        }
        assertEquals expected, message
    }

    void testStub() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().stub()
        play {}

        mockLoader.static.one().returns(4).stub()
        play {
            5.times {
                assertEquals 4, Loader.one()
            }
        }
    }

    void testRangeTimes() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().times(1..2)
        play {
            Loader.one()
        }

        mockLoader.static.one().times(1..2)
        play {
            2.times { Loader.one() }
        }
    }

    void testRangeTimesTooMany() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().times(1..2)
        def expected = "Unexpected static method call 'Loader.one()'\n" +
                       "  'Loader.one()': expected 1..2, actual 2 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                3.times { Loader.one() }
            }
            assertEquals expected, message
        }
    }

    void testStrictTimes() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().times(2)
        play {
            2.times { Loader.one() }
        }
    }

    void testStrictTimesTooMany() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().times(2)
        def expected = "Unexpected static method call 'Loader.one()'\n" +
                       "  'Loader.one()': expected 2, actual 2 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                3.times { Loader.one() }
            }
            assertEquals expected, message
        }
    }

    void testNever() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().never()
        play {}
    }

    void testNeverTooMany() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().never()
        def expected = "Unexpected static method call 'Loader.one()'\n" +
                       "  'Loader.one()': expected never, actual 0 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                Loader.one()
            }
            assertEquals expected, message
        }
    }

    void testOnce() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().once()
        play {
            Loader.one()
        }
    }

    void testAtLeast() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().atLeast(2)
        play {
            2.times { Loader.one() }
        }

        mockLoader.static.one().atLeast(2)
        play {
            3.times { Loader.one() }
        }
    }

    void testAtLeastTooFew() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().atLeast(2)
        def expected = "Expectation not matched on verify:\n" +
                       "  'Loader.one()': expected at least 2, actual 1"
        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.one()
            }
        }
        assertEquals expected, message
    }

    void testAtLeastOnce() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().atLeastOnce()
        play {
            Loader.one()
        }
    }

    void testAtMost() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().atMost(2)
        play {
            Loader.one()
        }

        mockLoader.static.one().atMost(2)
        play {
            2.times { Loader.one() }
        }
    }

    void testAtMostTooMany() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().atMost(2)
        def expected = "Unexpected static method call 'Loader.one()'\n" +
                       "  'Loader.one()': expected at most 2, actual 2 (+1)"
        play {
            def message = shouldFail(AssertionFailedError) {
                3.times { Loader.one() }
            }
            assertEquals expected, message
        }
    }

    void testAtMostOnce() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().atMostOnce()
        play {
            Loader.one()
        }
    }

    void testNonfixedTimesAfterFixedTimes() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().times(2)
        mockLoader.static.one().once()
        mockLoader.static.one().times(1..2)
        play {
            4.times { Loader.one() }
        }
    }

    void testNonfixedTimesAfterFixedTimesFailed() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().times(2)
        mockLoader.static.one().once()
        mockLoader.static.one().times(1..2)
        def expected = "Expectation not matched on verify:\n" +
                       "  'Loader.one()': expected 4..5, actual 3"
        def message = shouldFail(AssertionFailedError) {
            play {
                3.times { Loader.one() }
            }
        }
        assertEquals expected, message
    }

    void testAnythingAfterNonfixedTimes() {
        def mockLoader = mock(Loader)
        mockLoader.static.one().times(2)
        mockLoader.static.one().atLeast(3)

        shouldFail(IllegalStateException) {
            mockLoader.static.one().once()
        }
        shouldFail(IllegalStateException) {
            mockLoader.static.one().atMostOnce()
        }
    }

    void testMultipleStaticMocking() {
        def mockLoader1 = mock(Loader)
        def mockLoader2 = mock(Loader)
        mockLoader1.static.first()
        mockLoader2.static.second()

        play {
            Loader.first()
            Loader.second()
        }
    }

    void testMockStaticHashCode() {
        Loader mockLoader = mock(Loader)
        mockLoader.static.hashCode().returns(1)
        mockLoader.static.hashCode().returns(2)

        play {
            assertEquals 1, Loader.hashCode()
            assertEquals 2, Loader.hashCode()
        }
    }

    void testMockStaticHashCodeButUnexpectedCalled() {
        Loader mockLoader = mock(Loader)
        mockLoader.static.hashCode().returns(1)
        mockLoader.static.hashCode().returns(2)

        def expected = "Unexpected static method call 'Loader.toString()'\n" +
                       "  'Loader.hashCode()': expected 2, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.toString()
            }
        }
        assertEquals expected, message
    }

    void testStaticClosure() {
        mock(Loader).static {
            one().returns("one")
            it.hashCode().returns(3)
            two(match { it in ["two", 2] }).returns("two")
            setUp().returns("set up")
            name.set("test")
            name.returns("loader")
        }
        play {
            assertEquals "one", Loader.one()
            assertEquals 3, Loader.hashCode()
            assertEquals "two", Loader.two(2)
            assertEquals "set up", Loader.setUp()
            Loader.name = "test"
            assertEquals "loader", Loader.name
        }
    }

    void testMockMatchMethodInStaticClosure() {
        def closure = { -> }
        mock(Loader) {
            'static' {
                it.match(closure).returns("yes")
            }
            it.static {
                it.match(closure).returns("no")
            }
        }
        play {
            assertEquals "yes", Loader.match(closure)
            assertEquals "no", Loader.match(closure)
        }
    }

    void testMockMethodsAndPropertiesNamedStaticInStaticClosure() {
        def closure = { -> }
        mock(Loader).static {
            it.static().returns(true)
            it.static(closure).returns(true)
            it.static.returns(true)
        }
        play {
            assert Loader.static()
            assert Loader.static(closure)
            assert Loader.static
        }
    }

    void testMockMethodsNamedMockInStaticClosure() {
        def closure = { -> }
        mock(Loader).static {
            mock().returns("yes")
            mock(closure).returns("no")
        }
        play {
            assertEquals "yes", Loader.mock()
            assertEquals "no", Loader.mock(closure)
        }
    }

    void testStaticClosureInWithClosure(){
        Loader mockLoader = mock(Loader)

        with(mockLoader){
            load("key").returns("number")
            'static' {
                count().returns(1)
            }
        }
        play{
            assertEquals("number", mockLoader.load("key"))
            assertEquals 1, Loader.count()
        }
    }

    void testNonCompleteStaticExpectationsAreInvalid() {
        def mockLoader = mock(Loader)
        mockLoader.static.one()
        mockLoader.static

        def expected = "Missing static expectation for Loader"
        def message = shouldFail(IllegalStateException) {
            play {}
        }
        assertEquals expected, message
    }

    void testRegexStaticMethodName() {
        mock(Loader).static {
            /\w{4}/().returns(true)
            /isE\w*/().returns(true)
        }
        play {
            assertTrue Loader.init()
            assertTrue Loader.isEmpty()
        }
    }

    void testNoCrossUsageToStaticPropertyGetterWithRegexStaticMethodName() {
        mock(Loader).static./isE\w*/().returns(true)
        def expected = "Unexpected static property getter call 'Loader.empty'\n" +
                       "  'Loader.\"isE\\w*\"()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.empty
            }
        }
        assertEquals expected, message
    }

    void testNoCrossUsageToStaticPropertySetterWithRegexStaticMethodName() {
        mock(Loader).static./setF\w*/(true)
        def expected = "Unexpected static property setter call 'Loader.full = true'\n" +
                       "  'Loader.\"setF\\w*\"(true)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.full = true
            }
        }
        assertEquals expected, message
    }

    void testRegexStaticPropertyName() {
        mock(Loader).static {
            it./(?i)Empty/.returns(true).times(2)
            it./(?i)Full/.set(false).times(2)
        }
        play {
            assertTrue Loader.empty
            assertTrue Loader.isEmpty()
            Loader.full = false
            Loader.setFull(false)
        }
    }

    void testRegexStaticPropertyNameAndFailed() {
        mock(Loader).static./.*/.returns(true)
        def expected = "Unexpected static property setter call 'Loader.something = true'\n" +
                       "  'Loader.\".*\"': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.something = true
            }
        }
        assertEquals expected, message
    }

    void testMultipleReturnsAndRaisesOnOneExpectation() {
        mock(Loader).static.fun().returns(1).raises(RuntimeException).times(2).returns(2).stub()
        play {
            assertEquals 1, Loader.fun()
            2.times {
                shouldFail(RuntimeException) { Loader.fun() }
            }
            2.times {
                assertEquals 2, Loader.fun()
            }
        }
    }

    void testDelegateOfStaticClosureShouldBehaveTheSame() {
        mock(Loader).static {
            it.match(match{it}, delegate.match{it}).returns('yes')
        }
        play {
            assertEquals 'yes', Loader.match {true}{true}
        }
    }

}
