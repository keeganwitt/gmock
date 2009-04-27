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

import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.result.ToStringDefaultBehavior
import org.gmock.internal.result.HashCodeDefaultBehavior
import org.gmock.internal.result.EqualsDefaultBehavior
import org.gmock.internal.matcher.AlwaysMatchMatcher
import org.gmock.internal.times.AnyTimes
import org.gmock.internal.recorder.MethodRecorder
import static org.gmock.internal.metaclass.MetaClassHelper.*
import org.gmock.internal.recorder.StaticMethodRecoder
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.recorder.PropertyRecorder
import org.gmock.internal.signature.PropertySetSignature
import org.gmock.internal.expectation.Expectation
import org.gmock.internal.expectation.ExpectationCollection

class MockInternal {

    def expectations
    def mockInstance
    def controller
    def mockProxyMetaClass
    def mockName

    MockInternal(controller, mockInstance, mockName, mockProxyMetaClass){
        this.expectations = new ExpectationCollection(controller)
        this.controller = controller
        this.mockName = mockName
        this.mockProxyMetaClass = mockProxyMetaClass
        this.mockInstance = mockInstance
        mockProxyMetaClass.mock = this
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
        addMethodDefaultBehavior("equals", [AlwaysMatchMatcher.INSTANCE], new EqualsDefaultBehavior(mockInstance))
        addMethodDefaultBehavior("hashCode", [], new HashCodeDefaultBehavior(mockInstance))
        addMethodDefaultBehavior("toString", [], new ToStringDefaultBehavior(mockName))
    }

    private addMethodDefaultBehavior(methodName, arguments, result) {
        def signature = new MethodSignature(mockProxyMetaClass, methodName, arguments)
        if (!findSignature(signature)) {
            def expectation = new Expectation(signature: signature, result: result, times: AnyTimes.INSTANCE, hidden: true)
            expectations.add(expectation)
        }
    }

    Object invokeMockMethod(String methodName, Object[] arguments) {
        def signature = new MethodSignature(mockProxyMetaClass, methodName, arguments)
        if (controller.replay){
            def result =  findExpectation(expectations, signature, "Unexpected method call", arguments, controller)
            return result
        } else {
            if (methodName == "static" && arguments.length == 1 && arguments[0] instanceof Closure) {
                invokeStaticExpectationClosure(arguments[0])
                return null
            } else {
                def expectation = new Expectation(signature: signature)
                addToExpectations(expectation, expectations, controller)
                return new MethodRecorder(expectation)
            }
        }
    }

    private invokeStaticExpectationClosure(Closure staticExpectationClosure) {
        def recorder = new StaticMethodRecoder(mockProxyMetaClass.theClass, mockProxyMetaClass.classExpectations)
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

    Object getMockProperty(String property) {
        if (controller.replay){
            def signature = new PropertyGetSignature(mockProxyMetaClass, property)
            return findExpectation(expectations, signature, "Unexpected property getter call", [], controller)
        } else {
            if (property == "static"){
                return new StaticMethodRecoder(mockProxyMetaClass.theClass, mockProxyMetaClass.classExpectations)
            } else {
                def expectation = new Expectation()
                addToExpectations(expectation, expectations, controller)
                return new PropertyRecorder(mockProxyMetaClass, property, expectation)
            }
        }
    }

    Object setMockProperty(String property, Object value) {
        if (controller.replay){
            def signature = new PropertySetSignature(mockProxyMetaClass, property, value)
            findExpectation(expectations, signature, "Unexpected property setter call", [value], controller)
        } else {
            throw new MissingPropertyException("Cannot use property setter in record mode. " +
                    "Are you trying to mock a setter? Use '${property}.set(${value.inspect()})' instead.")
        }
    }

    def findSignature(signature) {
        return controller.orderedExpectations.findSignature(signature) ?: expectations.findSignature(signature)
    }

}
