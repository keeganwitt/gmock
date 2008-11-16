package org.gmock

import junit.framework.AssertionFailedError


class FunctionnalPropertyTest extends GMockTestCase {

    void testBasic(){
        def mockLoader = mock()
        mockLoader.name.returns('a name')
        mockLoader.name.returns('a different name')
        mockLoader.name.sets('another name')

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
        mockLoader.name.sets('another name')
        mockLoader.id.sets('some id')
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
        mockLoader.id.sets('some id')
        mockLoader.load("fruit").returns("apple")

        try {
            play {
                assertEquals "a name", mockLoader.name
                mockLoader.unexpected = 10
            }
            fail("Should throw an exception")
        } catch (AssertionFailedError e){
            def expected = "Unexpected property call 'unexpected = 10'\n"+
                           "  'name': expected 1, actual 1\n"+
                           "  'id = \"some id\"': expected 1, actual 0\n"+
                           "  'load(\"fruit\")': expected 1, actual 0"

            assertEquals expected, e.message
        }
    }

    void testExpectationNotVerified(){
        def mockLoader = mock()

        mockLoader.name.returns('a name')
        mockLoader.id.sets('some id')

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

        mockLoader.name.sets('a value').stub()

        play {
            mockLoader.name = "a value"
            mockLoader.name = "a value"
            return null // prevent the getter of name from being invoked for evaluating the result of closure
        }
    }

    void testStubSetterNotCalled(){
        def mockLoader = mock()
        mockLoader.name.sets('a value').stub()
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

        mockLoader.name.sets("a name")
        mockLoader.name.sets("a invalid name").raises(new RuntimeException("An exception"))

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

        mockLoader.name.sets("invalid").raises(RuntimeException, "test")

        play {
            def message = shouldFail(RuntimeException) {
                mockLoader.name = "invalid"
            }
            assertEquals "test", message
        }
    }

}
