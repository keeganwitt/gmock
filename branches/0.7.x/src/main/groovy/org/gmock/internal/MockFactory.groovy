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
        def mockName = getMockName(mockArgs.clazz, mockArgs.mockNameRecorder)
        def mpmc = new MockProxyMetaClass(mockArgs.clazz, controller.classExpectations, controller, mockName)
        return createMockWithMetaClass(mockArgs, mpmc, mockName)
    }

    def createConcreteMock(mockArgs) {
        def concreteInstance = mockArgs.concreteInstance
        if (concreteMocks.containsKey(concreteInstance)) {
            return concreteMocks.get(concreteInstance)
        } else {
            def mockName = getMockName(mockArgs.clazz, mockArgs.mockNameRecorder)
            def cmpmc = new ConcreteMockProxyMetaClass(mockArgs.clazz, controller, mockArgs.concreteInstance, mockName)
            def mock = createMockWithMetaClass(mockArgs, cmpmc, mockName)
            controller.concreteMocks << cmpmc
            concreteMocks.put(concreteInstance, mock)
            return mock
        }
    }

    private createMockWithMetaClass(mockArgs, metaClass, mockName) {
        def mockInstance
        if (isFinalClass(mockArgs.clazz)) {
            mockInstance = mockFinalClass(mockArgs.clazz, metaClass, mockArgs.invokeConstructorRecorder)
        } else {
            mockInstance = mockNonFinalClass(mockArgs.clazz, metaClass, mockArgs.invokeConstructorRecorder, mockName)
        }
        def mock = new MockInternal(controller, mockInstance, mockName, metaClass)
        mockCollection << mock
        return mock
    }

    private boolean isFinalClass(Class clazz) {
        return Modifier.isFinal(clazz.modifiers) ||
                (clazz.declaredConstructors.size() > 0 && clazz.declaredConstructors.every { Modifier.isPrivate(it.modifiers) })
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
