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

import org.gmock.internal.result.ThrowException
import org.gmock.internal.signature.ConstructorSignature
import org.gmock.internal.expectation.Expectation
import org.gmock.internal.result.ReturnValue

class ConstructorRecorder {

    def args
    def result

    ConstructorRecorder(args){
        this.args = args
    }

    def raises(Throwable exception){
        this.result = new ThrowException(exception)
        return this
    }

    def raises(Class exceptionClass, Object[] params) {
        this.result = new ThrowException(exceptionClass, params)
        return this
    }

    def generateExpectation(clazz, mockInstance){
        def signature = new ConstructorSignature(clazz, args)
        def returnValue = result ?: new ReturnValue(mockInstance)
        def expectation = new Expectation(result: returnValue)
        expectation.signature = signature
        return expectation
    }

}
