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

import org.gmock.internal.result.ReturnValue
import org.gmock.internal.result.ThrowException

class MethodRecorder extends BaseRecorder {

    MethodRecorder(expectation) {
        super(expectation)
    }

    def returns(value) {
        expectation.result = new ReturnValue(value)
        return new MethodDuplicateRecorder(expectation)
    }

    def raises(Throwable exception) {
        expectation.result = new ThrowException(exception)
        return new MethodDuplicateRecorder(expectation)
    }

    def raises(Class exceptionClass, Object[] params) {
        expectation.result = new ThrowException(exceptionClass, params)
        return new MethodDuplicateRecorder(expectation)
    }

    protected times(times) {
        super.times(times)
        return new MethodTimesRecorder(expectation)
    }

}

class MethodDuplicateRecorder extends MethodRecorder {

    MethodDuplicateRecorder(expectation) {
        super(expectation)
    }

    def returns(value) {
        expectation = expectation.duplicate()
        return super.returns(value)
    }

    def chains() {
        throw new MissingMethodException('chains', this.class, [])
    }

    def raises(Throwable exception) {
        expectation = expectation.duplicate()
        return super.raises(exception)
    }

    def raises(Class exceptionClass, Object[] params) {
        expectation = expectation.duplicate()
        return super.raises(exceptionClass, params)
    }

}

class MethodTimesRecorder extends MethodDuplicateRecorder {

    MethodTimesRecorder(expectation) {
        super(expectation)
    }

    protected times(times) {
        expectation = expectation.duplicate()
        return super.times(times)
    }

}
