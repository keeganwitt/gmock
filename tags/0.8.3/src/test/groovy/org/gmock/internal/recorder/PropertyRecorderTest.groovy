/*
 * Copyright 2008-2009 Julien Gagnet, Johnny Jian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmock.internal.recorder

import org.gmock.GMockTestCase
import org.gmock.internal.expectation.Expectation
import org.gmock.internal.result.ReturnNull
import org.gmock.internal.result.ReturnValue
import org.gmock.internal.result.ThrowException
import org.gmock.internal.times.AnyTimes

class PropertyRecorderTest extends GMockTestCase {

    void testRecordGetProperty(){
        def getSignature = mock()
        def signature = mock()
        signature.getGetter().returns(getSignature)

        def mockExpectations = mock()
        def expectation = new Expectation(signatureObserver: mockExpectations)
        mockExpectations.signatureChanged(expectation).times(2)

        play {
            expectation.signature = signature
            PropertyRecorder propertyRecorder = new PropertyRecorder(expectation)
            propertyRecorder.returns("a name")
        }

        assertEquals getSignature, expectation.signature
        assertEquals ReturnValue, expectation.result.class
        assertEquals "a name", expectation.result.value
    }

    void testRecordSetProperty(){
        def setSignature = mock()
        def signature = mock()
        signature.getSetter('a value').returns(setSignature)

        def mockExpectations = mock()
        def expectation = new Expectation(signatureObserver: mockExpectations)
        mockExpectations.signatureChanged(expectation).times(2)

        play {
            expectation.signature = signature
            PropertyRecorder propertyRecorder = new PropertyRecorder(expectation)
            propertyRecorder.set("a value")
        }

        assertEquals setSignature, expectation.signature
        assertEquals ReturnNull, expectation.result.class
    }

    void testStubRecord(){
        def expectation = mock()
        expectation.times.set(match { it instanceof AnyTimes })

        play {
            PropertyRecorder propertyRecorder = new PropertyRecorder(expectation)
            propertyRecorder.stub()
        }
    }

    void testRaisesException(){
        def getSignature = mock()
        def signature = mock()
        signature.getGetter().returns(getSignature)

        def mockExpectations = mock()
        def expectation = new Expectation(signatureObserver: mockExpectations)
        mockExpectations.signatureChanged(expectation).times(2)

        def exception = new RuntimeException()

        play {
            expectation.signature = signature
            PropertyRecorder propertyRecorder = new PropertyRecorder(expectation)
            propertyRecorder.raises(exception)
        }

        assertEquals getSignature, expectation.signature
        assertEquals ThrowException, expectation.result.class
        assertEquals exception, expectation.result.exception
    }

    void testRaisesExceptionClass() {
        def getSignature = mock()
        def signature = mock()
        signature.getGetter().returns(getSignature)

        def mockExpectations = mock()
        def expectation = new Expectation(signatureObserver: mockExpectations)
        mockExpectations.signatureChanged(expectation).times(2)

        def cause = new RuntimeException()

        play {
            expectation.signature = signature
            PropertyRecorder propertyRecorder = new PropertyRecorder(expectation)
            propertyRecorder.raises(Exception, "test", cause)
        }

        assertEquals getSignature, expectation.signature
        assertEquals ThrowException, expectation.result.class
        assertEquals Exception, expectation.result.exception.class
        assertEquals "test", expectation.result.exception.message
        assertEquals cause, expectation.result.exception.cause
    }

}
