package org.gmock.internal.recorder

import org.gmock.GMockTestCase
import org.gmock.internal.Expectation
import org.gmock.internal.ReturnNull
import org.gmock.internal.ReturnRaiseException
import org.gmock.internal.ReturnValue
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature
import org.gmock.internal.times.AnyTimes
import org.gmock.internal.signature.PropertyUncompleteSignature

class PropertyRecorderTest extends GMockTestCase {

    void testRecordGetProperty(){
        def getSignature = mock(PropertyGetSignature, constructor: ["name"])

        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation).times(2)


        play {
            PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)
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
        mockExpectations.checkTimes(expectation).times(2)


        play {
            PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)
            propertyRecorder.set("a value")
        }

        assertEquals setSignature, expectation.signature
        assertEquals ReturnNull, expectation.returnValue.class
    }

    void testStubRecord(){
        def expectation = mock()

        expectation.signature.set(match {it instanceof PropertyUncompleteSignature})
        expectation.times.set(match { it instanceof AnyTimes })

        play {
            PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)
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
