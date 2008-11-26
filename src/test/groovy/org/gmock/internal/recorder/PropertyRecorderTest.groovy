package org.gmock.internal.recorder

import org.gmock.GMockTestCase
import org.gmock.internal.Expectation
import org.gmock.internal.ReturnNull
import org.gmock.internal.ReturnRaiseException
import org.gmock.internal.ReturnValue
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature
import org.gmock.internal.times.AnyTimes

class PropertyRecorderTest extends GMockTestCase {

    void testRecordGetProperty(){
        def getSignature = mock(PropertyGetSignature, constructor: ["name"])

        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation)

        PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)

        play {
            propertyRecorder.returns("a name")
        }

        assertEquals getSignature, expectation.signature
        assertEquals ReturnValue, expectation.returnValue.class
        assertEquals "a name", expectation.returnValue.returnValue
    }

    void testRecordSetProperty(){
        def setSignature = mock(PropertySetSignature, constructor: ["name", "a value"])

        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation)

        PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)

        play {
            propertyRecorder.set("a value")
        }

        assertEquals setSignature, expectation.signature
        assertEquals ReturnNull, expectation.returnValue.class
    }

    void testStubRecord(){
        def expectation = mock()
        PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)

        expectation.times.set(match { it instanceof AnyTimes })

        play {
            propertyRecorder.stub()
        }
    }

    void testRaisesException(){
        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation)

        PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)
        def exception = new RuntimeException()

        assertEquals propertyRecorder, propertyRecorder.raises(exception)

        assertEquals ReturnRaiseException, expectation.returnValue.class
        assertEquals exception, expectation.returnValue.exception

    }

    void testRaisesExceptionClass() {
        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation)

        PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)
        def cause = new RuntimeException()

        assertEquals propertyRecorder, propertyRecorder.raises(Exception, "test", cause)

        assertEquals ReturnRaiseException, expectation.returnValue.class
        assertEquals Exception, expectation.returnValue.exception.class
        assertEquals "test", expectation.returnValue.exception.message
        assertEquals cause, expectation.returnValue.exception.cause
    }

}
