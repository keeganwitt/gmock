package org.gmock

import junit.framework.AssertionFailedError
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

        def expected = "Unexpected method call 'is(.*)'\n" +
                       "  'is(.*)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                mock1.is(mock3)
            }
        }
        assert message ==~ expected
    }

    void testPassAMockObjectToAnotherButNotCalled() {
        def mock1 = mock()
        def mock2 = mock()
        mock1.is(mock2).returns(true)
        
        def expected = "Expectation not matched on verify:\n" +
                       "  'is(.*)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {}
        }
        assert message ==~ expected
    }

}
