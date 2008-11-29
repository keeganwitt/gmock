package org.gmock

import net.sf.cglib.proxy.*
import java.lang.reflect.Method
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

    def groovyInvocationHandler = { proxy, Method method, Object[] args ->
        println "GroovyInvocationHandler.invoke($method.name, $args)"
        switch (method.name) {
            case "invokeMethod": return mpmc.invokeMethod(proxy, method.name, args)
            case "getProperty": return mpmc.getProperty(proxy, args[0])
            case "setProperty": return mpmc.setProperty(proxy, args[0], args[1])
            case "getMetaClass": return mpmc
            // ignore "setMetaClass" method
        }
    } as InvocationHandler
    def javaInvocationHandler = { proxy, Method method, Object[] args ->
        println "JavaInvocationHandler.invoke($method.name, $args)"
        mpmc.invokeMethod(proxy, method.name, args)
    } as InvocationHandler

    def superClass = clazz.isInterface() ? Object : clazz
    def interfaces = clazz.isInterface() ? [clazz, GroovyObject] : [GroovyObject]

    // Enhancer.createClass() does not support setting callbacks to the class, but to the instances
    // so we just set callback types here, and set callbacks after the instantiation of the instance
    def enhancer = new Enhancer(superclass: superClass, interfaces: interfaces, callbackFilter: filter,
            callbackTypes: [InvocationHandler, InvocationHandler] as Class[])
    def mockClass = enhancer.createClass()

    // use objenesis to instantiate the instance
    def mockInstance = ObjenesisHelper.newInstance(mockClass) // mockClass.newInstance()

    // cannot use "mockInstance.setCallbacks()" here, because in Groovy it will get the meta class first,
    // which has not been set until the callbacks are set, so we have to do it in Java
    MockHelper.setCallbacksTo(mockInstance, [groovyInvocationHandler, javaInvocationHandler] as Callback[])

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
