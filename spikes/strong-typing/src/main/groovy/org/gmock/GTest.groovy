package org.gmock

import java.lang.reflect.Method
import net.sf.cglib.proxy.*
import org.objenesis.ObjenesisHelper

class MockProxyMetaClass extends MetaClassImpl {

    MockProxyMetaClass(Class clazz) {
        super(clazz)
    }

    Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        println "MockProxyMetaClass.invokeMethod($methodName, $arguments)"
        return null
    }

    Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
        println "MockProxyMetaClass.getProperty($property)"
        return null
    }

    void setProperty(Class sender, Object receiver, String property, Object value, boolean isCallToSuper, boolean fromInsideClass) {
        println "MockProxyMetaClass.setProperty($property, $value)"
    }

}

def filter = [
    accept: { Method method ->
        GroovyObject.metaClass.pickMethod(method.name, method.parameterTypes) ? 0 : 1
    },
    equals: { o ->
        this.is o
    }
] as CallbackFilter

def mock = { Class clazz ->
    def mpmc = new MockProxyMetaClass(clazz)

    def groovyMethodInterceptor = { obj, Method method, Object[] args, MethodProxy proxy ->
        println "GroovyMethodInterceptor.invoke($method.name, $args)"
        switch (method.name) {
            case "invokeMethod": return mpmc.invokeMethod(obj, method.name, args)
            case "getProperty": return mpmc.getProperty(obj, args[0])
            case "setProperty": return mpmc.setProperty(obj, args[0], args[1])
            case "getMetaClass": return mpmc
            // ignore "setMetaClass" method
        }
    } as MethodInterceptor
    def javaMethodInterceptor = { obj, Method method, Object[] args, MethodProxy proxy ->
        println "JavaMethodInterceptor.invoke($method.name, $args)"
        mpmc.invokeMethod(obj, method.name, args)
    } as MethodInterceptor

    def superClass = clazz.isInterface() ? Object : clazz
    def interfaces = clazz.isInterface() ? [clazz, GroovyObject] : [GroovyObject]

    def enhancer = new Enhancer(superclass: superClass, interfaces: interfaces, useFactory: false, callbackFilter: filter,
            callbackTypes: [groovyMethodInterceptor.class, javaMethodInterceptor.class] as Class[])
    def mockClass = enhancer.createClass()
    Enhancer.registerCallbacks(mockClass, [groovyMethodInterceptor, javaMethodInterceptor] as Callback[])

    // use objenesis to instantiate the instance
    def mockInstance = ObjenesisHelper.newInstance(mockClass) // mockClass.newInstance()
    return mockInstance
}

[GLogger, JLogger, Logger].each { clazz ->
    println clazz.name.center(80, "-")
    Logger logger = mock(clazz)

    logger.info("call on groovy side")
    println()
    new JTest().testLogger(logger)
    println()

    logger.something = "set on groovy side"
    println()
    logger.something
    println()

    logger.dynamicMethod("call on groovy side")
    println()
}
