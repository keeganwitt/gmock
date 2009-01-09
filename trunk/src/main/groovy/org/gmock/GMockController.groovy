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
package org.gmock

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import net.sf.cglib.proxy.Callback
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import org.gmock.internal.ClassExpectations
import org.gmock.internal.GroovyObjectMethodFilter
import static org.gmock.internal.InternalModeHelper.doInternal
import org.gmock.internal.MockHelper
import org.gmock.internal.metaclass.DispatcherProxyMetaClass
import org.gmock.internal.metaclass.MockProxyMetaClass
import org.gmock.internal.recorder.ConstructorRecorder
import org.gmock.internal.recorder.InvokeConstructorRecorder
import org.objenesis.ObjenesisHelper

class GMockController {

    def mocks = []
    def classExpectations = new ClassExpectations(this)
    def dispatchers = new HashSet()

    // while running in internal mode, we should not mock any methods, instead, we should invoke the original implements
    // it is a little like the kernel mode in OS
    def internal = false

    @Deprecated
    def mock(Map constraints, Class clazz = Object) {
        return doMock(clazz, new ConstructorRecorder(constraints.constructor), null)
    }

    def mock(Class clazz = Object) {
        return doMock(clazz, null, null)
    }

    def mock(Class clazz, InvokeConstructorRecorder invokeConstructorRecorder) {
        return doMock(clazz, null, invokeConstructorRecorder)
    }

    def mock(Class clazz, ConstructorRecorder constructorRecorder) {
        return doMock(clazz, constructorRecorder, null)
    }

    def mock(Class clazz, InvokeConstructorRecorder invokeConstructorRecorder, ConstructorRecorder constructorRecorder) {
        return doMock(clazz, constructorRecorder, invokeConstructorRecorder)
    }

    def mock(Class clazz, ConstructorRecorder constructorRecorder, InvokeConstructorRecorder invokeConstructorRecorder) {
        return doMock(clazz, constructorRecorder, invokeConstructorRecorder)
    }

    private doMock(Class clazz, ConstructorRecorder constructorRecorder, InvokeConstructorRecorder invokeConstructorRecorder) {
        doInternal(this) {
            def mpmc = new MockProxyMetaClass(clazz, classExpectations, this)
            def mockInstance

            if (!Modifier.isFinal(clazz.modifiers)) {
                mockInstance = mockNonFinalClass(clazz, mpmc, invokeConstructorRecorder)
            } else {
                mockInstance = mockFinalClass(clazz, mpmc, invokeConstructorRecorder)
            }

            if (constructorRecorder){
                def expectation = constructorRecorder.generateExpectation(clazz, mockInstance)
                classExpectations.addConstructorExpectation(clazz, expectation)
            }

            mocks << mpmc
            return mockInstance
        }
    }

    def play(Closure closure) {
        doInternal(this) {
            mocks*.validate()
            classExpectations.validate()
            mocks*.replay()
            classExpectations.startProxy()
        }
        try {
            try {
                closure.call()
            } finally {
                doInternal(this) {
                    classExpectations.stopProxy()
                }
            }
            doInternal(this) {
                mocks*.verify()
                classExpectations.verify()
            }
        } finally {
            doInternal(this) {
                mocks*.reset()
                classExpectations.reset()
            }
        }
    }

    private mockNonFinalClass(Class clazz, MockProxyMetaClass mpmc, InvokeConstructorRecorder invokeConstructorRecorder) {
        def groovyMethodInterceptor = { obj, Method method, Object[] args, MethodProxy proxy ->
            switch (method.name) {
                case "invokeMethod": return mpmc.invokeMethod(obj, args[0], args[1])
                case "getProperty": return mpmc.getProperty(obj, args[0])
                case "setProperty": return mpmc.setProperty(obj, args[0], args[1])
                case "getMetaClass": return mpmc
                // ignore "setMetaClass" method
            }
        } as MethodInterceptor
        def javaMethodInterceptor = { obj, Method method, Object[] args, MethodProxy proxy ->
            if (internal) {
                return proxy.invokeSuper(obj, args)
            } else {
                return mpmc.invokeMethod(obj, method.name, args)
            }
        } as MethodInterceptor

        def superClass = clazz.isInterface() ? Object : clazz
        def interfaces = clazz.isInterface() ? [clazz, GroovyObject] : [GroovyObject]

        def enhancer = new Enhancer(superclass: superClass, interfaces: interfaces,
                                    callbackFilter: GroovyObjectMethodFilter.instance,
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
            dispatchers << dpmc
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
