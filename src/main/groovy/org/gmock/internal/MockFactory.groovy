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

import org.gmock.internal.recorder.ConstructorRecorder
import org.gmock.internal.recorder.InvokeConstructorRecorder
import org.gmock.internal.recorder.MockNameRecorder
import org.gmock.GMockController
import org.gmock.internal.metaclass.MockProxyMetaClass
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.Callback
import net.sf.cglib.proxy.MethodInterceptor
import static org.gmock.internal.metaclass.MetaClassHelper.setMetaClassTo
import org.gmock.internal.metaclass.ConcreteMockProxyMetaClass
import java.lang.reflect.Modifier
import org.objenesis.ObjenesisHelper
import org.gmock.internal.util.WeakIdentityHashMap


class MockFactory {


    def controller
    def defaultNames = [:]
    Map concreteMocks = new WeakIdentityHashMap()
    def mockCollection

    MockFactory(controller, mockCollection) {
        this.controller = controller
        this.mockCollection = mockCollection
    }



    private static final ARG_CLASSES = [constructorRecorder: ConstructorRecorder,
            invokeConstructorRecorder: InvokeConstructorRecorder,
            mockNameRecorder: MockNameRecorder,
            expectationClosure: Closure]

    private static final CONCRETE_ARG_CLASSES = [mockNameRecorder: MockNameRecorder,
            expectationClosure: Closure]

    def createMock(mockArgs) {
        def mockInstance
        def mockName = getMockName(mockArgs.clazz, mockArgs.mockNameRecorder)
        def mpmc = new MockProxyMetaClass(mockArgs.clazz, controller.classExpectations, controller, mockName)
        if (!Modifier.isFinal(mockArgs.clazz.modifiers)) {
            mockInstance = mockNonFinalClass(mockArgs.clazz, mpmc, mockArgs.invokeConstructorRecorder, mockName)
        } else {
            mockInstance = mockFinalClass(mockArgs.clazz, mpmc, mockArgs.invokeConstructorRecorder)
        }
        def mock = new MockInternal(controller, mockInstance, mockName, mpmc)
        mockCollection << mock
        return mock
    }

    def createConcreteMock(mockArgs) {
        def mockInstance
        def concreteInstance = mockArgs.concreteInstance
        if (concreteMocks.containsKey(concreteInstance)) {
            return concreteMocks.get(concreteInstance)
        } else {
            def mockName = getMockName(mockArgs.clazz, mockArgs.mockNameRecorder)
            def mpmc = new ConcreteMockProxyMetaClass(mockArgs.clazz, controller, mockArgs.concreteInstance, mockName)
            controller.concreteMocks << mpmc
            if (!Modifier.isFinal(mockArgs.clazz.modifiers)) {
                mockInstance = mockNonFinalClass(mockArgs.clazz, mpmc, mockArgs.invokeConstructorRecorder, mockName)
            } else {
                mockInstance = mockFinalClass(mockArgs.clazz, mpmc, mockArgs.invokeConstructorRecorder)
            }
            def mock = new MockInternal(controller, mockInstance, mockName, mpmc)
            mockCollection << mock
            concreteMocks.put(concreteInstance, mock)
            return mock
        }
    }


    def parseMockArgument(clazz, args) {
        def mockArgs = [:]
        if (isValidMockArgs(args)){
            mockArgs.clazz = clazz
            args.each {arg ->
                def name = findArgName(arg)
                checkAndSet(mockArgs, name, arg, clazz, args)
            }
        } else if (clazz == Object && isValidConcreteArgs(args)){
            mockArgs.concreteInstance = args[0]
            mockArgs.clazz = args[0].class
            args.each {arg ->
                def name = findConcreteArgName(arg)
                checkAndSet(mockArgs, name, arg, clazz, args)
            }
        } else {
            invalidMockMethod(clazz, args)
        }
        return mockArgs
    }

    private isValidMockArgs(args){
        for (a in args){
            if (!instanceOfKnowClass(a)){
                return false
            }
        }
        return true
    }

    private isValidConcreteArgs(args){
        if (args.size() > 1){
            for (a in args[1..-1]){
                if (!instanceOfKnowClass(a)){
                    return false
                }
            }
        }
        return true
    }

    private findArgName(arg) {
        ARG_CLASSES.find {k, Class c -> c.isInstance(arg) }?.key
    }

    private findConcreteArgName(arg) {
        CONCRETE_ARG_CLASSES.find {k, Class c -> c.isInstance(arg) }?.key
    }

    private boolean instanceOfKnowClass(object) {
        findArgName(object)
    }

    private boolean instanceOfKnowConcreteClass(object) {
        findConcreteArgName(object)
    }

    private boolean allInstanceOfKnowClass(array) {
    }

    private checkAndSet(Map mockArgs, String name, Object arg, Class clazz, Object[] args) {
        if (mockArgs[name]) {
            invalidMockMethod(clazz, args)
        } else {
            mockArgs[name] = arg
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
        def groovyMethodInterceptor = new GroovyMethodInterceptor(mpmc)
        def javaMethodInterceptor = new JavaMethodInterceptor(controller, mpmc, mockName)

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

    private mockFinalClass(Class clazz, ProxyMetaClass mpmc, InvokeConstructorRecorder invokeConstructorRecorder) {
        def mockInstance = newInstance(clazz, invokeConstructorRecorder)
        setMetaClassTo(mockInstance, clazz, mpmc, controller)
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
