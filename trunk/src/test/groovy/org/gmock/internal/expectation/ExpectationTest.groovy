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
package org.gmock.internal.expectation

import org.gmock.GMockTestCase
import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.times.AnyTimes

class ExpectationTest extends GMockTestCase {

    void testIsVerifiedHasntBeenCalled() {
        def mockExpectations = mock()
        def expectation = new Expectation(signatureObserver: mockExpectations)
        mockExpectations.signatureChanged(expectation)

        play {
            expectation.signature = new MethodSignature(new Object(), "a method", [])
            assertFalse expectation.isVerified()
        }
    }

    void testIsVerifiedHasBeenCalled() {
        def mockExpectations = mock()
        def expectation = new Expectation(signatureObserver: mockExpectations)
        mockExpectations.signatureChanged(expectation)

        play {
            expectation.signature = new MethodSignature(new Object(), "a method", [])
            expectation.times = new AnyTimes()
            assertTrue expectation.isVerified()
        }
    }

    void testIsVerifiedIsStub() {
        def expectation = new Expectation()
        expectation.times = new AnyTimes()
        assertTrue expectation.isVerified()
    }

    void testSetSignature() {
        def mockExpectations = mock()
        def expectation = new Expectation(signatureObserver: mockExpectations)
        mockExpectations.signatureChanged(expectation)

        play {
            expectation.signature = new MethodSignature(new Object(), "method", [1, 2])
        }
    }

    void testCanCall() {
        def mockTimes = mock()
        mockTimes.stillRemain(3).returns(true)
        def signature = new MethodSignature(new Object(), "method", [1, 2])
        def expectation = new Expectation(times: mockTimes, called: 3)
        expectation.@signature = signature
        play {
            assert expectation.canCall(signature)
        }

        mockTimes.stillRemain(3).returns(false)
        play {
            assertFalse expectation.canCall(signature)
        }

        mockTimes.stillRemain(3).returns(true)
        play {
            assertFalse expectation.canCall(new MethodSignature(new Object(), "other", []))
        }
    }

    void testDoReturn() {
        def mockReturn = mock()
        def mockObject = new Object()
        mockReturn.answer(mockObject, 'test', []).returns(99)
        def expectation = new Expectation(result: mockReturn, called: 3)

        play {
            assertEquals 99, expectation.answer(mockObject, 'test', [])
            assertEquals 4, expectation.called
        }
    }

    void testValidateCallsSignatureValidation(){
        def mockSignature = mock()
        mockSignature.validate().once()

        def expectation = new Expectation()
        expectation.@signature = mockSignature
        play {
            expectation.validate()
        }
    }

}
