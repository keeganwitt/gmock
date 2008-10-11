package org.gmock

import junit.framework.AssertionFailedError

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

}