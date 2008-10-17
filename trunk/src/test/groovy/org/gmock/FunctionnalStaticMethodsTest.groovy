package org.gmock

import junit.framework.AssertionFailedError


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

    void testNothingExpected() {
        def mockLoader = mock(Loader)
        mockLoader.static

        play {}
    }
}