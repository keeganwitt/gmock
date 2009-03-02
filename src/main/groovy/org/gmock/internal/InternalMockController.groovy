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

import java.lang.reflect.Modifier
import net.sf.cglib.proxy.Callback
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import org.gmock.GMockController
import org.gmock.internal.metaclass.DispatcherProxyMetaClass
import org.gmock.internal.metaclass.MockProxyMetaClass
import org.gmock.internal.recorder.ConstructorRecorder
import org.gmock.internal.recorder.InvokeConstructorRecorder
import org.gmock.internal.recorder.MockNameRecorder
import org.objenesis.ObjenesisHelper
import static org.gmock.internal.InternalModeHelper.doInternal
import junit.framework.Assert
import org.gmock.internal.callstate.CallState

class InternalMockController implements MockController {

    def mocks = []
    def classExpectations = new ClassExpectations(this)
    def orderedExpectations = new OrderedExpectations(this)
    def unorderedExpectations = new ExpectationCollection(this)
    def defaultNames = [:]

    boolean replay = false
    Order order = Order.NONE
    def mockDelegate = null

    // while running in internal mode, we should not mock any methods, instead, we should invoke the original implements
    // it is a little like the kernel mode in OS
    boolean internal = false

    boolean isOrdered() {
        order != Order.NONE
    }

    boolean isStrictOrdered() {
        order == Order.STRICT
    }

    boolean isLooseOrdered() {
        order == Order.LOOSE
    }

    @Deprecated
    def mock(Map constraints, Class clazz = Object, Closure expectationClosure = null) {
        return doMock(clazz, new ConstructorRecorder(constraints.constructor), null, null, expectationClosure)
    }

    def mock(Class clazz = Object, Object... args) {
        def constructorRecorder = null, invokeConstructorRecorder = null, mockNameRecorder = null, expectationClosure = null
        args.each { arg ->
            if (arg instanceof ConstructorRecorder) {
                constructorRecorder = checkUnset(constructorRecorder, arg, clazz, args)
            } else if (arg instanceof InvokeConstructorRecorder) {
                invokeConstructorRecorder = checkUnset(invokeConstructorRecorder, arg, clazz, args)
            } else if (arg instanceof MockNameRecorder) {
                mockNameRecorder = checkUnset(mockNameRecorder, arg, clazz, args)
            } else if (arg instanceof Closure) {
                expectationClosure = checkUnset(expectationClosure, arg, clazz, args)
            } else {
                invalidMockMethod(clazz, args)
            }
        }
        return doMock(clazz, constructorRecorder, invokeConstructorRecorder, mockNameRecorder, expectationClosure)
    }

    private checkUnset(value, arg, Class clazz, Object[] args) {
        if (value) {
            invalidMockMethod(clazz, args)
        } else {
            return arg
        }
    }

    private invalidMockMethod(Class clazz, Object[] args) {
        throw new MissingMethodException("mock", GMockController, [clazz, *args] as Object[])
    }

    private doMock(Class clazz, ConstructorRecorder constructorRecorder, InvokeConstructorRecorder invokeConstructorRecorder,
                   MockNameRecorder mockNameRecorder, Closure expectationClosure) {
        def mockInstance
        doInternal(this) {
            if (replay) {
                throw new IllegalStateException("Cannot create mocks in play closure.")
            }

            def mockName = getMockName(clazz, mockNameRecorder)
            def mpmc = new MockProxyMetaClass(clazz, classExpectations, this, mockName)

            if (!Modifier.isFinal(clazz.modifiers)) {
                mockInstance = mockNonFinalClass(clazz, mpmc, invokeConstructorRecorder, mockName)
            } else {
                mockInstance = mockFinalClass(clazz, mpmc, invokeConstructorRecorder)
            }

            if (constructorRecorder){
                def expectation = constructorRecorder.generateExpectation(clazz, mockInstance)
                classExpectations.addConstructorExpectation(clazz, expectation)
            }

            mpmc.mockInstance = mockInstance
            def mock = new MockInternal(this, mockInstance, mockName, mpmc)

            mocks << mock
        }
        if (expectationClosure){
            callClosureWithDelegate(expectationClosure, mockInstance)
        }

        return mockInstance
    }

    private getMockName(Class clazz, MockNameRecorder mockNameRecorder) {
        def mockName
        if (mockNameRecorder) {
            mockName = mockNameRecorder
        } else {
            mockName = new MockNameRecorder(clazz)
            if (!defaultNames[clazz]) {
                defaultNames[clazz] = mockName
            } else {
                if (defaultNames[clazz] instanceof MockNameRecorder) {
                    defaultNames[clazz].count = 1
                    mockName.count = defaultNames[clazz] = 2
                } else { // defaultNames[clazz] instanceof Integer
                    mockName.count = ++defaultNames[clazz]
                }
            }
        }
        return mockName
    }

    private callClosureWithDelegate(Closure closure, delegate) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = delegate
        def backup = mockDelegate
        try {
            mockDelegate = delegate
            closure(delegate)
        } finally {
            mockDelegate = backup
        }
    }

    def play(Closure closure) {
        doInternal(this) {
            if (replay) {
                throw new IllegalStateException("Cannot nest play closures.")
            }
            if (ordered) {
                throw new IllegalStateException("Play closures cannot be inside strict closure.")
            }

            mocks*.validate()
            classExpectations.validate()
            orderedExpectations.validate()
            mocks*.replay()
            classExpectations.startProxy()
        }
        try {
            try {
                replay = true
                closure.call()
            } finally {
                replay = false
                doInternal(this) {
                    classExpectations.stopProxy()
                }
            }
            doInternal(this) {
                mocks*.verify()
                classExpectations.verify()
                orderedExpectations.verify()
            }
        } finally {
            doInternal(this) {
                mocks*.reset()
                classExpectations.reset()
                orderedExpectations.reset()
                unorderedExpectations.reset()
            }
        }
    }

    def with(mock, Closure withClosure) {
        callClosureWithDelegate(withClosure, mock)
    }

    def strict(Closure strictClosure) {
        if (ordered) {
            throw new IllegalStateException("Cannot nest strict closures.")
        }
        if (replay) {
            throw new IllegalStateException("Strict closures cannot be inside play closure.")
        }

        orderedExpectations.newStrictGroup()
        callClosureWithMockDelegate(strictClosure, Order.STRICT)
    }

    def loose(Closure looseClosure) {
        if (looseOrdered) {
            throw new IllegalStateException("Cannot nest loose closures.")
        }
        if (!strictOrdered) {
            throw new IllegalStateException("Loose closures can only be inside strict closure.")
        }

        orderedExpectations.newLooseGroup()
        callClosureWithMockDelegate(looseClosure, Order.LOOSE)
    }

    private def callClosureWithMockDelegate(Closure closure, Order order) {
        Order backup = this.order
        try {
            this.order = order
            if (mockDelegate) {
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.delegate = mockDelegate
            }
            closure(mockDelegate)
        } finally {
            this.order = backup
        }
    }

    private mockNonFinalClass(Class clazz, MockProxyMetaClass mpmc, InvokeConstructorRecorder invokeConstructorRecorder, mockName) {
        def groovyMethodInterceptor = new GroovyMethodInterceptor(mpmc)
        def javaMethodInterceptor = new JavaMethodInterceptor(this, mpmc, mockName)

        def superClass = clazz.isInterface() ? Object : clazz
        def interfaces = clazz.isInterface() ? [clazz, GroovyObject] : [GroovyObject]

        def enhancer = new Enhancer(superclass: superClass, interfaces: interfaces,
                                    callbackFilter: GroovyObjectMethodFilter.INSTANCE,
                                    callbackTypes: [MethodInterceptor, MethodInterceptor])
        def mockClass = enhancer.createClass()

        def mockInstance = newInstance(mockClass, invokeConstructorRecorder)
        MockHelper.setCallbacksTo(mockInstance, [groovyMethodInterceptor, javaMethodInterceptor] as Callback[])

        return mockInstance
    }

    private mockFinalClass(Class clazz, MockProxyMetaClass mpmc, InvokeConstructorRecorder invokeConstructorRecorder) {
        def mockInstance = newInstance(clazz, invokeConstructorRecorder)
        if (GroovyObject.isAssignableFrom(clazz)) {
            mockInstance.metaClass = mpmc
        } else {
            def dpmc = DispatcherProxyMetaClass.getInstance(clazz)
            dpmc.controller = this
            dpmc.setMetaClassForInstance(mockInstance, mpmc)
        }
        return mockInstance
    }

    private newInstance(Class clazz, InvokeConstructorRecorder invokeConstructorRecorder) {
        if (invokeConstructorRecorder) {
            return clazz.newInstance(invokeConstructorRecorder.args)
        } else {
            return ObjenesisHelper.newInstance(clazz)
        }
    }

    def fail(message, signature = null) {
        def callState = callState(signature).toString()
        if (callState) { callState = "\n$callState" }
        signature = signature ? ' ' + signature.toString(mocks.size() > 1) : ''
        Assert.fail("$message$signature$callState")
    }

    private callState(signature) {
        def callState = new CallState(mocks.size() > 1, !orderedExpectations.empty)
        orderedExpectations.appendToCallState(callState)
        unorderedExpectations.appendToCallState(callState)
        if (signature) {
            callState.nowCalling(signature)
        }
        return callState
    }

}

enum Order { NONE, STRICT, LOOSE }
