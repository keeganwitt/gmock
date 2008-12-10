package org.gmock

import junit.framework.AssertionFailedError
import static org.hamcrest.Matchers.*

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

    void testEmptyStaticExpectationsShouldNotAppearInErrorMessages() {
        def mockLoader = mock(Loader)
        mockLoader.static.one()
        mockLoader.static

        def expected = "Missing static expectation for Loader"
        def message = shouldFail(IllegalStateException) {
            play {}
        }
        assertEquals expected, message
    }

    void testMockStaticHashCode() {
        Loader mockLoader = mock(Loader)
        mockLoader.static.hashCode().returns(1)

        play {
            assertEquals 1, Loader.hashCode()
        }
    }

}
