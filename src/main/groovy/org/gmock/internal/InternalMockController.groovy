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

class InternalMockController implements MockController {

    def mocks = []
    def classExpectations = new ClassExpectations(this)
    def orderedExpectations = new OrderedExpectations()

    boolean replay = false
    boolean ordered = false

    // while running in internal mode, we should not mock any methods, instead, we should invoke the original implements
    // it is a little like the kernel mode in OS
    boolean internal = false

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

            String mockName = mockNameRecorder ? mockNameRecorder.mockName : "Mock for $clazz.name"
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
            mocks << mpmc
        }
        if (expectationClosure){
            expectationClosure.resolveStrategy = Closure.DELEGATE_FIRST
            expectationClosure.setDelegate(mockInstance)
            expectationClosure(mockInstance)
        }

        return mockInstance
    }

    def play(Closure closure) {
        doInternal(this) {
            if (replay) {
                throw new IllegalStateException("Cannot nest play closures.")
            }

            mocks*.validate()
            classExpectations.validate()
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
            }
        }
    }

    def with(mock, Closure withClosure) {
        withClosure.resolveStrategy = Closure.DELEGATE_FIRST
        withClosure.setDelegate(mock)
        withClosure(mock)
    }

    def strict(Closure strictClosure) {
        orderedExpectations.newStrictGroup()
        try {
            ordered = true
            strictClosure()
        } finally {
            ordered = false
        }
    }

    private mockNonFinalClass(Class clazz, MockProxyMetaClass mpmc, InvokeConstructorRecorder invokeConstructorRecorder, String mockName) {
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

}
