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
package org.gmock.internal

import org.gmock.internal.MockDelegate
import org.gmock.internal.expectation.Expectation
import org.gmock.internal.expectation.ExpectationCollection
import org.gmock.internal.factory.DefaultSignatureFactory
import org.gmock.internal.matcher.AlwaysMatchMatcher
import org.gmock.internal.recorder.MethodRecorder
import org.gmock.internal.recorder.PropertyRecorder
import org.gmock.internal.recorder.StaticRecoder
import org.gmock.internal.result.EqualsDefaultBehavior
import org.gmock.internal.result.HashCodeDefaultBehavior
import org.gmock.internal.result.ToStringDefaultBehavior
import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.signature.PropertySignature
import org.gmock.internal.times.AnyTimes
import static org.gmock.internal.metaclass.MetaClassHelper.*
import org.gmock.internal.result.AsBooleanDefaultBehavior

class MockInternal {

    def expectations
    def controller
    def mockName
    def clazz
    def classExpectations
    def signatureFactory

    MockInternal(controller, mockName, clazz, classExpectations, signatureFactory = new DefaultSignatureFactory()) {
        this.expectations = new ExpectationCollection(controller)
        this.controller = controller
        this.mockName = mockName
        this.clazz = clazz
        this.classExpectations = classExpectations
        this.signatureFactory = signatureFactory
    }

    void verify(){
        expectations.verify()
    }

    void validate(){
        expectations.validate()
    }

    void reset(){
        this.expectations = new ExpectationCollection(controller)
    }

    void replay() {
        addMethodDefaultBehavior("equals", [AlwaysMatchMatcher.INSTANCE], new EqualsDefaultBehavior())
        addMethodDefaultBehavior("hashCode", [], new HashCodeDefaultBehavior())
        addMethodDefaultBehavior("toString", [], new ToStringDefaultBehavior(mockName))
        addMethodDefaultBehavior("asBoolean", [], new AsBooleanDefaultBehavior())
    }

    private addMethodDefaultBehavior(methodName, arguments, result) {
        def signature = new MethodSignature(this, methodName, arguments)
        if (!findSignature(signature)) {
            def expectation = new Expectation(signature: signature, result: result, times: AnyTimes.INSTANCE, hidden: true)
            expectations.add(expectation)
        }
    }

    Object invokeMockMethod(Object mockObject, String methodName, Object[] arguments) {
        arguments = MockHelper.evaluateGStrings(arguments, controller)
        if (controller.replay){
            def signature = signatureFactory.createMethodSignature(this, methodName, arguments)
            def result = findExpectation(expectations, signature, "Unexpected method call", mockObject, methodName, arguments, controller)
            return result
        } else {
            def signature = new MethodSignature(this, methodName, arguments)
            if (methodName == "static" && arguments.length == 1 && arguments[0] instanceof Closure) {
                invokeStaticExpectationClosure(arguments[0])
                return null
            } else {
                def expectation = new Expectation(signature: signature)
                controller.addToExpectations(expectation, expectations)
                return new MethodRecorder(expectation)
            }
        }
    }

    private invokeStaticExpectationClosure(Closure staticExpectationClosure) {
        def recorder = new StaticRecoder(clazz, classExpectations)
        staticExpectationClosure.resolveStrategy = Closure.DELEGATE_FIRST
        def backup = controller.mockDelegate
        try {
            controller.mockDelegate = staticExpectationClosure.delegate = new MockDelegate(recorder, controller)
            controller.doExternal {
                staticExpectationClosure(recorder)
            }
        } finally {
            controller.mockDelegate = backup
        }
    }

    Object getMockProperty(Object mockObject, String property) {
        if (controller.replay){
            def signature = signatureFactory.createPropertyGetSignature(this, property)
            return findExpectation(expectations, signature, "Unexpected property getter call", mockObject, getGetterMethodName(property), [], controller)
        } else {
            if (property == "static"){
                return new StaticRecoder(clazz, classExpectations)
            } else {
                def expectation = new Expectation(signature: new PropertySignature(this, property))
                controller.addToExpectations(expectation, expectations)
                return new PropertyRecorder(expectation)
            }
        }
    }

    Object setMockProperty(Object mockObject, String property, Object value) {
        if (controller.replay){
            def signature = signatureFactory.createPropertySetSignature(this, property, value)
            findExpectation(expectations, signature, "Unexpected property setter call", mockObject, getSetterMethodName(property), [value], controller)
        } else {
            throw new MissingPropertyException("Cannot use property setter in record mode. " +
                    "Are you trying to mock a setter? Use '${property}.set(${value.inspect()})' instead.")
        }
    }

    def findSignature(signature) {
        return controller.orderingController.orderedExpectations.findSignature(signature) ?: expectations.findSignature(signature)
    }

}
