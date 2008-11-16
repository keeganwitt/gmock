package org.gmock.internal.recorder

import org.gmock.GMockTestCase
import org.gmock.internal.Expectation
import org.gmock.internal.ReturnNull
import org.gmock.internal.ReturnRaiseException
import org.gmock.internal.ReturnValue
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature

class PropertyRecorderTest extends GMockTestCase {

    void testRecordGetProperty(){
        def getSignature = mock(PropertyGetSignature, constructor: ["name"])

        def expectation = new Expectation()
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

        def expectation = new Expectation()
        PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)

        play {
            propertyRecorder.sets("a value")
        }

        assertEquals setSignature, expectation.signature
        assertEquals ReturnNull, expectation.returnValue.class
    }

    void testStubRecord(){
        def expectation = mock()
        PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)

        expectation.stubed.sets(true)

        play {
            propertyRecorder.stub()
        }
    }

    void testRaisesException(){
        def expectation = new Expectation()
        PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)
        def exception = new RuntimeException()

        assertEquals propertyRecorder, propertyRecorder.raises(exception)

        assertEquals ReturnRaiseException, expectation.returnValue.class
        assertEquals exception, expectation.returnValue.exception

    }

    void testRaisesExceptionClass() {
        def expectation = new Expectation()
        PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)
        def cause = new RuntimeException()

        assertEquals propertyRecorder, propertyRecorder.raises(Exception, "test", cause)

        assertEquals ReturnRaiseException, expectation.returnValue.class
        assertEquals Exception, expectation.returnValue.exception.class
        assertEquals "test", expectation.returnValue.exception.message
        assertEquals cause, expectation.returnValue.exception.cause
    }

}
