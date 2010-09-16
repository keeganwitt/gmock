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
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature
import org.gmock.internal.signature.PropertyUncompleteSignature

class PropertyRecorder extends BaseRecorder {

    def mock
    def propertyName
    Class setterClass
    Class getterClass

    PropertyRecorder(mock, propertyName, expectation, Class uncompleteClass = PropertyUncompleteSignature,
                     Class setterClass = PropertySetSignature, Class getterClass = PropertyGetSignature) {
        super(expectation)
        this.mock = mock
        this.propertyName = propertyName
        this.setterClass = setterClass
        this.getterClass = getterClass
        if (uncompleteClass) {
            expectation.signature = uncompleteClass.newInstance(mock, propertyName)
        }
    }

    def sets(value) {
        throw new DeprecationException("'sets' is deprecated. Use 'set' instead.");
    }

    def set(value) {
        expectation.signature = setterClass.newInstance(mock, propertyName, value)
        expectation.result = ReturnNull.INSTANCE
        return new PropertySetterRecorder(this)
    }

    def returns(value) {
        expectation.signature = getterClass.newInstance(mock, propertyName)
        expectation.result = new ReturnValue(value)
        return new PropertyGetterRecorder(this)
    }

    protected doRaises(Object[] params) {
        expectation.signature = getterClass.newInstance(mock, propertyName)
        expectation.result = ThrowException.newInstance(params)
        return new PropertyGetterRecorder(this)
    }

    def raises(Throwable exception) {
        return doRaises(exception)
    }

    def raises(Class exceptionClass, Object[] params) {
        return doRaises(exceptionClass, *params)
    }

    def times(times) {
        super.times(times)
        return new PropertyTimesRecorder(this)
    }

}

class PropertySetterRecorder extends PropertyRecorder {

    PropertySetterRecorder(recorder) {
        super(recorder.mock, recorder.propertyName, recorder.expectation, null, recorder.setterClass, recorder.getterClass)
    }

    def set(value) {
        expectation = expectation.duplicate()
        return super.set(value)
    }

    def returns(value) {
        expectation = expectation.duplicate()
        return super.returns(value)
    }

    protected doRaises(Object[] params) {
        expectation.result = ThrowException.newInstance(params)
        return new PropertyGetterRecorder(this)
    }

}

class PropertyGetterRecorder extends PropertySetterRecorder {

    PropertyGetterRecorder(recorder) {
        super(recorder)
    }

    protected doRaises(Object[] params) {
        expectation = expectation.duplicate()
        return super.doRaises(params)
    }

}

class PropertyTimesRecorder extends PropertyGetterRecorder {

    PropertyTimesRecorder(recorder) {
        super(recorder)
    }

    def times(times) {
        expectation = expectation.duplicate()
        return super.times(times)
    }

}
