package org.gmock

import junit.framework.AssertionFailedError
import static org.hamcrest.Matchers.*

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

    void testLastPropertySetDontGetEvaluated(){
        def mockLoader = mock()
        mockLoader.name.sets('another name')
        play {
            setName(mockLoader)
        }
    }

    void setName(mockLoader){
        mockLoader.name = 'another name'
    }

    void testSetPropertyHamcrestMatcher() {
        def mockLoader = mock()
        mockLoader.test.sets(is(not("wrong")))

        play {
            mockLoader.test = "correct"
            return null // prevent the getter of name from being invoked for evaluating the result of closure
        }
    }

    void testSetPropertyHamcrestMatcherNotMatched() {
        def mockLoader = mock()
        mockLoader.test.sets(isOneOf(1, 2, 3))
        def expected = "Unexpected property call 'test'\n" +
                       "  'test = one of {<1>, <2>, <3>}': expected 1, actual 0"

        def message = shouldFail(AssertionFailedError) {
            play {
                mockLoader.test = 0
                return null // prevent the getter of name from being invoked for evaluating the result of closure
            }
        }
        assertEquals expected, message
    }

    void testSetPropertyClosureMatcher() {
        def mockLoader = mock()
        mockLoader.test.sets(match { it != "wrong" })

        play {
            mockLoader.test = "correct"
            return null // prevent the getter of name from being invoked for evaluating the result of closure
        }
    }

    void testSetPropertyClosureMatcherNotMatched() {
        def mockLoader = mock()
        mockLoader.test.sets(match { it in [1, 2, 3] })
        def expected = "Unexpected property call 'test'\n" +
                       "  'test = a value matching the closure matcher': expected 1, actual 0"

        def message = shouldFail(AssertionFailedError) {
            play {
                mockLoader.test = 0
                return null // prevent the getter of name from being invoked for evaluating the result of closure
            }
        }
        assertEquals expected, message
    }

}
