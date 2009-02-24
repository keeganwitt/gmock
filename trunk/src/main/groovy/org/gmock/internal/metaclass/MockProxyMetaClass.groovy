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
package org.gmock.internal.metaclass

import org.gmock.internal.Expectation
import org.gmock.internal.ExpectationCollection
import static org.gmock.internal.InternalModeHelper.doExternal
import static org.gmock.internal.InternalModeHelper.doInternal
import org.gmock.internal.matcher.AlwaysMatchMatcher
import static org.gmock.internal.metaclass.MetaClassHelper.*
import org.gmock.internal.recorder.PropertyRecorder
import org.gmock.internal.recorder.ReturnMethodRecorder
import org.gmock.internal.recorder.StaticMethodRecoder
import org.gmock.internal.result.EqualsDefaultBehavior
import org.gmock.internal.result.HashCodeDefaultBehavior
import org.gmock.internal.result.ToStringDefaultBehavior
import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature
import org.gmock.internal.times.AnyTimes

class MockProxyMetaClass extends ProxyMetaClass {

    def expectations = new ExpectationCollection()
    def classExpectations
    def controller
    def mockInstance
    String mockName

    MockProxyMetaClass(Class clazz, classExpectations, controller, String mockName) {
        super(GroovySystem.metaClassRegistry, clazz, GroovySystem.metaClassRegistry.getMetaClass(clazz))
        this.classExpectations = classExpectations
        this.controller = controller
        this.mockName = mockName
    }

    Object invokeMethod(Object object, String methodName, Object[] arguments) {
        invokeMethod(theClass, object, methodName, arguments, false, false)
    }

    Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        doInternal(controller) {
            adaptee.invokeMethod(receiver, methodName, arguments)
        } {
            def signature = newSignatureForMethod(methodName, arguments)
            if (controller.replay){
                return findExpectation(expectations, signature, "Unexpected method call", arguments, controller, this)
            } else {
                if (methodName == "static" && arguments.length == 1 && arguments[0] instanceof Closure) {
                    invokeStaticExpectationClosure(arguments[0])
                    return null
                } else {
                    def expectation = new Expectation(signature: signature)
                    addToExpectations(expectation, expectations, controller, this)
                    return new ReturnMethodRecorder(expectation)
                }
            }
        }
    }

    private invokeStaticExpectationClosure(Closure staticExpectationClosure) {
        def recorder = new StaticMethodRecoder(theClass, classExpectations, controller)
        staticExpectationClosure.resolveStrategy = Closure.DELEGATE_FIRST
        staticExpectationClosure.delegate = recorder
        try {
            controller.mockDelegate = recorder
            doExternal(controller) {
                staticExpectationClosure(recorder)
            }
        } finally {
            controller.mockDelegate = null
        }
    }

    Object getProperty(Object object, String property) {
        getProperty(theClass, object, property, false, false)
    }

    Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
        doInternal(controller) {
            adaptee.getProperty(receiver, property)
        } {
            if (controller.replay){
                def signature = new PropertyGetSignature(property)
                return findExpectation(expectations, signature, "Unexpected property getter call", [], controller, this)
            } else {
                if (property == "static"){
                    return new StaticMethodRecoder(theClass, classExpectations, controller)
                } else {
                    def expectation = new Expectation()
                    addToExpectations(expectation, expectations, controller, this)
                    return new PropertyRecorder(property, expectation)
                }
            }
        }
    }

    void setProperty(Object object, String property, Object newValue) {
        setProperty(theClass, object, property, newValue, false, false)
    }

    void setProperty(Class sender, Object receiver, String property, Object value, boolean isCallToSuper, boolean fromInsideClass) {
        doInternal(controller) {
            adaptee.setProperty(receiver, property, value)
        } {
            if (controller.replay){
                def signature = new PropertySetSignature(property, value)
                findExpectation(expectations, signature, "Unexpected property setter call", [value], controller, this)
            } else {
                throw new MissingPropertyException("Cannot use property setter in record mode. " +
                        "Are you trying to mock a setter? Use '${property}.set(${value.inspect()})' instead.")
            }
        }
    }

    MetaMethod pickMethod(String methodName, Class[] arguments) {
        doInternal(controller) {
            adaptee.pickMethod(methodName, arguments)
        } {
            if (!controller.replay) {
                def method = getGMockMethod(methodName, arguments, this, controller)
                if (method) return method
            }
            return new ProxyMetaMethod(this, methodName, arguments)
        }
    }

    void verify(){
        expectations.verify()
    }

    void validate(){
        expectations.validate()
    }

    void reset(){
        this.expectations = new ExpectationCollection()
    }

    void replay() {
        addMethodDefaultBehavior("equals", [AlwaysMatchMatcher.INSTANCE], new EqualsDefaultBehavior(mockInstance))
        addMethodDefaultBehavior("hashCode", [], new HashCodeDefaultBehavior(mockInstance))
        addMethodDefaultBehavior("toString", [], new ToStringDefaultBehavior(mockName))
    }

    private addMethodDefaultBehavior(methodName, arguments, result) {
        def signature = new MethodSignature(methodName, arguments)
        if (!expectations.findSignature(signature)) {
            def expectation = new Expectation(signature: signature, result: result, times: AnyTimes.INSTANCE, hidden: true)
            expectations.add(expectation)
        }
    }

}
