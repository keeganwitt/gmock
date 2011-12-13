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

class PropertyRecorder extends BaseRecorder {

    PropertyRecorder(expectation) {
        super(expectation)
    }

    def set(value) {
        expectation.signature = expectation.signature.getSetter(value)
        expectation.result = ReturnNull.INSTANCE
        return new PropertySetterRecorder(expectation)
    }

    def returns(value) {
        expectation.signature = expectation.signature.getGetter()
        expectation.result = new ReturnValue(value)
        return new PropertyGetterRecorder(expectation)
    }

    def chains() {
        expectation.signature = expectation.signature.getGetter()
        return super.chains()
    }

    protected doRaises(Object[] params) {
        expectation.signature = expectation.signature.getGetter()
        expectation.result = ThrowException.newInstance(params)
        return new PropertyGetterRecorder(expectation)
    }

    def raises(Throwable exception) {
        return doRaises(exception)
    }

    def raises(Class exceptionClass, Object[] params) {
        return doRaises(exceptionClass, *params)
    }

    def times(times) {
        super.times(times)
        return new PropertyTimesRecorder(expectation)
    }

}

class PropertySetterRecorder extends PropertyRecorder {

    PropertySetterRecorder(expectation) {
        super(expectation)
    }

    def set(value) {
        expectation = expectation.duplicate()
        return super.set(value)
    }

    def returns(value) {
        expectation = expectation.duplicate()
        return super.returns(value)
    }

    def chains() {
        throw new MissingMethodException('chains', this.class, [])
    }

    protected doRaises(Object[] params) {
        expectation.result = ThrowException.newInstance(params)
        return new PropertyGetterRecorder(expectation)
    }

}

class PropertyGetterRecorder extends PropertySetterRecorder {

    PropertyGetterRecorder(expectation) {
        super(expectation)
    }

    protected doRaises(Object[] params) {
        expectation = expectation.duplicate()
        return super.doRaises(params)
    }

}

class PropertyTimesRecorder extends PropertyGetterRecorder {

    PropertyTimesRecorder(expectation) {
        super(expectation)
    }

    def times(times) {
        expectation = expectation.duplicate()
        return super.times(times)
    }

}
