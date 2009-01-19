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
import org.gmock.internal.Expectation
import org.gmock.internal.result.ReturnNull
import org.gmock.internal.result.ReturnValue
import org.gmock.internal.result.ThrowException
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature
import org.gmock.internal.signature.PropertyUncompleteSignature
import org.gmock.internal.times.AnyTimes

class PropertyRecorderTest extends GMockTestCase {

    void testRecordGetProperty(){
        def getSignature = mock(PropertyGetSignature, constructor("name"))

        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation).times(2)


        play {
            PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)
            propertyRecorder.returns("a name")
        }

        assertEquals getSignature, expectation.signature
        assertEquals ReturnValue, expectation.result.class
        assertEquals "a name", expectation.result.value
    }

    void testRecordSetProperty(){
        def setSignature = mock(PropertySetSignature, constructor("name", "a value"))

        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation).times(2)


        play {
            PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)
            propertyRecorder.set("a value")
        }

        assertEquals setSignature, expectation.signature
        assertEquals ReturnNull, expectation.result.class
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

        assertEquals ThrowException, expectation.result.class
        assertEquals exception, expectation.result.exception

    }

    void testRaisesExceptionClass() {
        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation)

        PropertyRecorder propertyRecorder = new PropertyRecorder("name", expectation)
        def cause = new RuntimeException()

        assertEquals propertyRecorder, propertyRecorder.raises(Exception, "test", cause)

        assertEquals ThrowException, expectation.result.class
        assertEquals Exception, expectation.result.exception.class
        assertEquals "test", expectation.result.exception.message
        assertEquals cause, expectation.result.exception.cause
    }

}
