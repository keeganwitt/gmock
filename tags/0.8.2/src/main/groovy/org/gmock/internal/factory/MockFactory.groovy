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
package org.gmock.internal.factory

import java.lang.reflect.Modifier
import net.sf.cglib.proxy.Callback
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import org.gmock.GMockController
import org.gmock.internal.factory.ChainsSignatureFactory
import org.gmock.internal.metaclass.ConcreteMockProxyMetaClass
import org.gmock.internal.metaclass.GeneratedMockProxyMetaClass
import org.gmock.internal.metaclass.MockProxyMetaClass
import org.gmock.internal.recorder.ConstructorRecorder
import org.gmock.internal.recorder.InvokeConstructorRecorder
import org.gmock.internal.recorder.MockNameRecorder
import org.gmock.internal.util.WeakIdentityHashMap
import org.objenesis.ObjenesisHelper
import org.gmock.internal.*
import org.gmock.internal.metaclass.GeneratedClassProxyMetaClass

class MockFactory {

    def controller
    def defaultNames = [:]
    Map concreteMocks = new WeakIdentityHashMap()
    def mockCollection

    MockFactory(controller, mockCollection) {
        this.controller = controller
        this.mockCollection = mockCollection
    }

    private static final ARG_CLASSES = [
            constructorRecorder: ConstructorRecorder,
            invokeConstructorRecorder: InvokeConstructorRecorder,
            mockNameRecorder: MockNameRecorder,
            expectationClosure: Closure
        ]

    private static final CONCRETE_ARG_CLASSES = [
            mockNameRecorder: MockNameRecorder,
            expectationClosure: Closure
        ]

    def createMock(mockArgs) {
        def mockName = getMockName(mockArgs.clazz, mockArgs.mockNameRecorder)
        def mock = createMockInternal(mockArgs.clazz, mockName)
        return createMockWithMockInternal(mock, mockArgs.invokeConstructorRecorder)
    }

    def createConcreteMock(mockArgs) {
        def concreteInstance = mockArgs.concreteInstance
        if (concreteMocks.containsKey(concreteInstance)) {
            return concreteMocks.get(concreteInstance)
        } else {
            def mockName = getMockName(mockArgs.clazz, mockArgs.mockNameRecorder)
            def mock = createMockInternal(mockArgs.clazz, mockName)
            def cmpmc = new ConcreteMockProxyMetaClass(mockArgs.clazz, controller, mockArgs.concreteInstance, mock)
            controller.concreteMocks << cmpmc

            def mockInstance = createMockWithMetaClass(mockArgs.clazz, cmpmc, null, mockName)
            concreteMocks.put(concreteInstance, mockInstance)
            return mockInstance
        }
    }

    def createMockOfClass(Class clazz, MockInternal mock) {
        mock.clazz = clazz
        return createMockWithMockInternal(mock, null)
    }

    private createMockWithMockInternal(mock, constructor) {
        def mpmc = new MockProxyMetaClass(mock.clazz, controller, mock)
        return createMockWithMetaClass(mock.clazz, mpmc, constructor, mock.mockName)
    }

    private createMockWithMetaClass(Class clazz, metaClass, constructor, mockName) {
        if (isFinalClass(clazz)) {
            return mockFinalClass(clazz, metaClass, constructor)
        } else {
            return mockNonFinalClass(clazz, metaClass, constructor, mockName)
        }
    }

    private boolean isFinalClass(Class clazz) {
        return Modifier.isFinal(clazz.modifiers) ||
                (clazz.declaredConstructors.size() > 0 && clazz.declaredConstructors.every { Modifier.isPrivate(it.modifiers) })
    }

    private createMockInternal(clazz, mockName) {
        def mock = new MockInternal(controller, mockName, clazz, controller.classExpectations)
        mockCollection << mock
        return mock
    }

    def createChainsMockInternal(previousSignature) {
        def signatureFactory = new ChainsSignatureFactory(previousSignature)
        def mock = new ChainsMockInternal(controller, new MockNameRecorder(''), Object, controller.classExpectations, signatureFactory)
        mockCollection << mock
        return mock
    }

    def parseMockArgument(clazz, args) {
        def mockArgs = [:]
        if (isValidMockArgs(args)){
            mockArgs.clazz = clazz
            setMockArgs(mockArgs, ARG_CLASSES, clazz, args)
        } else if (clazz == Object && isValidConcreteArgs(args)){
            mockArgs.concreteInstance = args[0]
            mockArgs.clazz = args[0].class
            setMockArgs(mockArgs, CONCRETE_ARG_CLASSES, clazz, args)
        } else {
            invalidMockMethod(clazz, args)
        }
        return mockArgs
    }

    private setMockArgs(mockArgs, argClasses, clazz, args) {
        args.each { arg ->
            def name = findArgName(arg, argClasses)
            checkAndSet(mockArgs, name, arg, clazz, args)
        }
    }

    private findArgName(arg, argClasses) {
        argClasses.find { k, Class c -> c.isInstance(arg) }?.key
    }

    private isValidMockArgs(args){
        args.every { instanceOfKnowClass(it) }
    }

    private isValidConcreteArgs(args){
        args.size() >= 1 && args.toList().tail().every { instanceOfKnowConcreteClass(it) }
    }

    private boolean instanceOfKnowClass(object) {
        ARG_CLASSES.any { k, Class c -> c.isInstance(object) }
    }

    private boolean instanceOfKnowConcreteClass(object) {
        CONCRETE_ARG_CLASSES.any { k, Class c -> c.isInstance(object) }
    }

    private checkAndSet(Map mockArgs, String name, Object arg, Class clazz, Object[] args) {
        if (name){
            if (mockArgs[name]) {
                invalidMockMethod(clazz, args)
            } else {
                mockArgs[name] = arg
            }
        }
    }

    private invalidMockMethod(Class clazz, Object[] args) {
        throw new MissingMethodException("mock", GMockController, [clazz, * args] as Object[])
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

    private mockNonFinalClass(Class clazz, ProxyMetaClass mpmc, InvokeConstructorRecorder invokeConstructorRecorder, mockName) {
        def groovyClass = GroovyObject.isAssignableFrom(clazz)

        def superClass = clazz.isInterface() ? Object : clazz
        def interfaces = clazz.isInterface() ? [clazz] : null
        def callbackFilter = groovyClass ? GroovyObjectMethodFilter.INSTANCE : JavaObjectMethodFilter.INSTANCE
        def callbackTypes = groovyClass ? [MethodInterceptor] * 2 : [MethodInterceptor]
        def callbacks = [new JavaMethodInterceptor(controller, mpmc, mockName)]
        if (groovyClass) {
            callbacks << new GroovyMethodInterceptor(mpmc)
        }

        def enhancer = new Enhancer(superclass: superClass, interfaces: interfaces, callbackFilter: callbackFilter,
                callbackTypes: callbackTypes)
        def mockClass = enhancer.createClass()

        def mockInstance = newInstance(mockClass, invokeConstructorRecorder)
        MockHelper.setCallbacksTo(mockInstance, callbacks as Callback[])

        if (!groovyClass) {
            GeneratedMockProxyMetaClass.startProxy(mockInstance, mpmc)
            GeneratedClassProxyMetaClass.startProxy(mockClass, clazz)
        }

        return mockInstance
    }

    private mockFinalClass(Class clazz, ProxyMetaClass mpmc, InvokeConstructorRecorder invokeConstructorRecorder) {
        def mockInstance = newInstance(clazz, invokeConstructorRecorder)
        mockInstance.metaClass = mpmc
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
