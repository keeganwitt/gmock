package org.gmock.internal

import org.gmock.GMockTestCase
import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.times.AnyTimes

class ExpectationTest extends GMockTestCase {

    void testIsVerifiedHasntBeenCalled() {
        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation)

        play {
            expectation.signature = new MethodSignature("a method", [])
            assertFalse expectation.isVerified()
        }
    }

    void testIsVerifiedHasBeenCalled() {
        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation)

        play {
            expectation.signature = new MethodSignature("a method", [])
            expectation.times = new AnyTimes()
            assertTrue expectation.isVerified()
        }
    }

    void testIsVerifiedIsStub() {
        def expectation = new Expectation()
        expectation.times = new AnyTimes()
        assertTrue expectation.isVerified()
    }

    void testIsVerifiedDoesntContainSignature() {
        def expectation = new Expectation()
        assertTrue expectation.isVerified()
    }

    void testSetSignature() {
        def mockExpectations = mock()
        def expectation = new Expectation(expectations: mockExpectations)
        mockExpectations.checkTimes(expectation)

        play {
            expectation.signature = new MethodSignature("method", [1, 2])
        }
    }

    void testCanCall() {
        def mockTimes = mock()
        mockTimes.stillRemain(3).returns(true)
        def signature = new MethodSignature("method", [1, 2])
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
            assertFalse expectation.canCall(new MethodSignature("other", []))
        }
    }

    void testDoReturn() {
        def mockReturn = mock()
        mockReturn.answer().returns(99)
        def expectation = new Expectation(result: mockReturn, called: 3)

        play {
            assertEquals 99, expectation.answer()
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

    void testValidateWithoutSignature(){
        def expectation = new Expectation()
        play {
            expectation.validate()
        }
    }


}
