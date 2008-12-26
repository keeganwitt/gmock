package org.gmock

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import net.sf.cglib.proxy.Callback
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import org.gmock.internal.ClassExpectations
import org.gmock.internal.Expectation
import org.gmock.internal.GroovyObjectMethodFilter
import static org.gmock.internal.InternalModeHelper.doInternal
import org.gmock.internal.MockHelper
import org.gmock.internal.metaclass.DispatcherProxyMetaClass
import org.gmock.internal.metaclass.MockProxyMetaClass
import org.gmock.internal.result.ReturnValue
import org.gmock.internal.signature.ConstructorSignature
import org.objenesis.ObjenesisHelper
import org.gmock.internal.recorder.ConstructorRecorder

class GMockController {

    def mocks = []
    def classExpectations = new ClassExpectations(this)
    def dispatchers = new HashSet()

    // while running in internal mode, we should not mock any methods, instead, we should invoke the original implements
    // it is a little like the kernel mode in OS
    def internal = false

    @Deprecated()
    def mock(Map constraints, Class clazz = Object) {
        return mock(clazz, new ConstructorRecorder(constraints.constructor))
    }
    
    def mock(Class clazz = Object, ConstructorRecorder constructorRecorder = null) {
        doInternal(this) {
            def mpmc = new MockProxyMetaClass(clazz, classExpectations, this)
            def mockInstance

            if (!Modifier.isFinal(clazz.modifiers)) {
                mockInstance = mockNonFinalClass(clazz, mpmc)
            } else {
                mockInstance = mockFinalClass(clazz, mpmc)
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

    def constructor(Object[] args){
        return new ConstructorRecorder(args)
    }

    def stop() {
        doInternal(this) {
            dispatchers*.stopProxy()
        }
    }

    private mockNonFinalClass(Class clazz, MockProxyMetaClass mpmc) {
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

        def mockInstance = newInstance(mockClass)
        MockHelper.setCallbacksTo(mockInstance, [groovyMethodInterceptor, javaMethodInterceptor] as Callback[])

        return mockInstance
    }

    private mockFinalClass(Class clazz, MockProxyMetaClass mpmc) {
        def mockInstance = newInstance(clazz)
        if (GroovyObject.isAssignableFrom(clazz)) {
            mockInstance.metaClass = mpmc
        } else {
            def fmc = DispatcherProxyMetaClass.getInstance(clazz)
            fmc.controller = this
            fmc.setMetaClassForInstance(mockInstance, mpmc)
            dispatchers << fmc
        }
        return mockInstance
    }

    private newInstance(Class clazz) {
        ObjenesisHelper.newInstance(clazz)
    }

}
