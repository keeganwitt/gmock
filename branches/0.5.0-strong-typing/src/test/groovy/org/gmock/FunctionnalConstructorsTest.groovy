package org.gmock

import junit.framework.AssertionFailedError
import static org.hamcrest.Matchers.*
import org.gmock.utils.Loader

class FunctionnalConstructorsTest extends GMockTestCase {

    void testConstructorNoArgument(){
        def mockLoader = mock(Loader, constructor: [])
        mockLoader.load("abc").returns("123")

        play {
            def expectedLoader = new Loader()
            assertEquals "123", expectedLoader.load("abc")
            assertSame expectedLoader, mockLoader
        }

    }

    void testConstructorMultipleArguments(){
        Date now = new Date()
        def mockLoader1 = mock(Loader, constructor: ["1", "2"])
        def mockLoader2 = mock(Loader, constructor: [now])

        play {
            def epectedLoader2 = new Loader(now)
            def epectedLoader1 = new Loader("1", "2")
            assertSame epectedLoader1, mockLoader1
            assertSame epectedLoader2, mockLoader2
        }
    }

    void testConstructorNoMatchFound(){
        def mockLoader1 = mock(Loader, constructor: ["1", "2"])
        def mockLoader2 = mock(Loader, constructor: ["1", "2"])
        def mockLoader3 = mock(Loader, constructor: ["foo"])

        try {
            play {
                try {
                    def foundLoader = new Loader("1", "2")
                    def loaderNotFound = new Loader("a", "b")
                    fail("Should have throw an exception")
                } catch (AssertionFailedError e){
                    def expected = "Unexpected constructor call 'new Loader(a,b)'\n"+
                                   "  'new Loader(1,2)': expected 2, actual 1\n"+
                                   "  'new Loader(foo)': expected 1, actual 0"

                    assertEquals expected, e.message
                }
            }
        } catch (AssertionFailedError ignore){}
    }

    void testMockingConstructorAllowStaticMethodCall(){
        def mockLoader = mock(Loader, constructor: [])
        mockLoader.load("abc").returns("123")

        play {
            def expectedLoader = new Loader()
            assertEquals "123", expectedLoader.load("abc")
            assertEquals 1, Loader.one()
        }
    }

    void testConstructorNotCalled(){
        def mockLoader1 = mock(Loader, constructor: ["1", "2"])
        def mockLoader2 = mock(Loader, constructor: [3, 4])

        try {
            play {
                def loader = new Loader("1", "2")
            }
            fail("Should have throw an exception")
        } catch (AssertionFailedError e){
            def expected = "Expectation not matched on verify:\n" +
                           "  'new Loader(\"1\", \"2\")': expected 1, actual 1\n" +
                           "  'new Loader(3, 4)': expected 1, actual 0"
            assertEquals expected, e.message
        }
    }

    void testConstructorDifferentClassExpected(){
        def mockLoader1 = mock(Loader, constructor: ["1"])

        def now = new Date()
        try {
            play {
                def loader = new Loader(now)
            }
            fail("Should have throw an exception")
        } catch (AssertionFailedError e){
            def expected = "Unexpected constructor call 'new Loader(${now.inspect()})'\n"+
                           "  'new Loader(\"1\")': expected 1, actual 0"
            assertEquals expected, e.message
        }

    }

    void testConstructorHamcrestMatcher() {
        def mockLoader = mock(Loader, constructor: [greaterThanOrEqualTo(5), "1"])

        play {
            assertSame mockLoader, new Loader(5, "1")
        }
    }

    void testConstructorHamcrestMatcherNotMatched() {
        mock(Loader, constructor: [greaterThan(5), "2"])
        def expected = "Unexpected constructor call 'new Loader(5, \"2\")'\n" +
                       "  'new Loader(a value greater than <5>, \"2\")': expected 1, actual 0"

        def message = shouldFail(AssertionFailedError) {
            play {
                new Loader(5, "2")
            }
        }
        assertEquals expected, message
    }

    void testConstructorClosureMatcher() {
        def mockLoader = mock(Loader, constructor: [match { it >= 5 }, "1"])

        play {
            assertSame mockLoader, new Loader(5, "1")
        }
    }

    void testConstructorClosureMatcherNotMatched() {
        mock(Loader, constructor: [match { it > 5 }, "2"])
        def expected = "Unexpected constructor call 'new Loader(5, \"2\")'\n" +
                       "  'new Loader(a value matching the closure matcher, \"2\")': expected 1, actual 0"

        def message = shouldFail(AssertionFailedError) {
            play {
                new Loader(5, "2")
            }
        }
        assertEquals expected, message
    }

}
