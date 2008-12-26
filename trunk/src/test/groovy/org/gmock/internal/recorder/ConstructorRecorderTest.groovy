package org.gmock.internal.recorder

import org.gmock.GMockTestCase
import org.gmock.internal.signature.ConstructorSignature
import org.gmock.internal.signature.ParameterSignature

class ConstructorRecorderTest extends GMockTestCase {

    void testGenerateExpectationNoExceptionSetup(){
        def constructorRecorder = new ConstructorRecorder([1, "2"])
        def mockInstance = "a mockInstance"

        play {
            def expectation = constructorRecorder.generateExpectation(String, mockInstance)
            assertEquals mockInstance, expectation.result.value
            assertTrue expectation.signature instanceof ConstructorSignature
            assertEquals String, expectation.signature.aClass
            assertEquals (new ParameterSignature([1, "2"]), expectation.signature.arguments)
        }
    }

    void testGenerateExpectationWithExceptionSetup(){
        def constructorRecorder = new ConstructorRecorder([1, "2"])
        def exception = new RuntimeException()
        constructorRecorder.raises(exception)

        play {
            def expectation = constructorRecorder.generateExpectation(String, "a mockInstance")
            assertEquals exception, expectation.result.exception
            assertTrue expectation.signature instanceof ConstructorSignature
            assertEquals String, expectation.signature.aClass
            assertEquals (new ParameterSignature([1, "2"]), expectation.signature.arguments)
        }
    }

}