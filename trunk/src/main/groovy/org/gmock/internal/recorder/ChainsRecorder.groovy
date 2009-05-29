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

import org.gmock.internal.expectation.Expectation
import org.gmock.internal.signature.ChainsSignature
import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.signature.PropertySignature
import static org.gmock.internal.metaclass.MetaClassHelper.addToExpectations

class ChainsRecorder implements GroovyInterceptable {

    def controller
    def mock
    def expectations
    def preExpectation

    def ChainsRecorder(controller, mock, expectations, preExpectation) {
        this.controller = controller
        this.mock = mock
        this.expectations = expectations
        this.preExpectation = preExpectation
    }

    Object invokeMethod(String name, Object args) {
        def signature = new ChainsSignature(preExpectation.signature, new MethodSignature(mock, name, args))
        def expectation = new Expectation(signature: signature)
        addToExpectations(expectation, expectations, controller)
        return new MethodRecorder(expectation)
    }

    Object getProperty(String property) {
        def signature = new ChainsSignature(preExpectation.signature, new PropertySignature(mock, property))
        def expectation = new Expectation(signature: signature)
        addToExpectations(expectation, expectations, controller)
        return new PropertyRecorder(expectation)
    }

    void setProperty(String property, Object value) {
        throw new MissingPropertyException("Cannot use property setter in record mode. " +
                "Are you trying to mock a setter? Use '${property}.set(${value.inspect()})' instead.")
    }

    // TODO: check if expectations are missing

}
