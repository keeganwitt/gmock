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

import org.gmock.internal.result.ReturnNull
import org.gmock.internal.result.ReturnValue
import org.gmock.internal.result.ThrowException
import org.gmock.internal.signature.StaticPropertyGetSignature
import org.gmock.internal.signature.StaticPropertySetSignature
import org.gmock.internal.signature.StaticPropertyUncompleteSignature

class StaticPropertyRecorder extends BaseRecorder {

    def clazz
    def property

    StaticPropertyRecorder(clazz, property, expectation) {
        super(expectation)
        this.clazz = clazz
        this.property = property
        this.expectation.signature = new StaticPropertyUncompleteSignature(clazz, property)
    }

    def set(value) {
        expectation.signature = new StaticPropertySetSignature(clazz, property, value)
        expectation.result = ReturnNull.INSTANCE
        return this
    }

    def returns(value) {
        expectation.signature = new StaticPropertyGetSignature(clazz, property)
        expectation.result = new ReturnValue(value)
        return this
    }

    private doRaises(Object[] params) {
        if (expectation.signature.class == StaticPropertyUncompleteSignature){
            expectation.signature = new StaticPropertyGetSignature(clazz, property)
        }
        expectation.result = ThrowException.metaClass.invokeConstructor(params)
        return this
    }

    def raises(Throwable exception) {
        return doRaises(exception)
    }

    def raises(Class exceptionClass, Object[] params) {
        return doRaises(exceptionClass, *params)
    }

}
