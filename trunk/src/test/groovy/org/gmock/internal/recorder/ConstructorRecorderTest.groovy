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