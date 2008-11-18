package org.gmock.internal

import org.gmock.GMockTestCase
import org.gmock.internal.signature.MethodSignature

class ExpectationTest extends GMockTestCase {

    void testIsVerifiedHasntBeenCalled(){
        def expectation = new Expectation(new MethodSignature("a method", []))
        assertFalse expectation.isVerified()

    }

    void testIsVerifiedHasBeenCalled(){
        def expectation = new Expectation(new MethodSignature("a method", []))
        expectation.called = true
        assertTrue expectation.isVerified()
    }

    void testIsVerifiedIsStub(){
        def expectation = new Expectation()
        expectation.stubed = true
        assertTrue expectation.isVerified()
    }


    void testIsVerifiedDoesntContainSignature(){
        def expectation = new Expectation()
        assertTrue expectation.isVerified()
    }

}