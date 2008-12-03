package org.gmock

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import net.sf.cglib.proxy.*
import org.codehaus.groovy.reflection.CachedClass
import org.codehaus.groovy.reflection.ReflectionCache
import org.codehaus.groovy.runtime.DefaultGroovyMethods
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

class ProxyMetaMethod extends MetaMethod {

    private MetaClass theMetaClass

    private String name

    private CachedClass declaringClass

    ProxyMetaMethod(MetaClass metaClass, String name, Class[] parameterTypes) {
        super(parameterTypes)
        this.theMetaClass = metaClass
        this.name = name
        this.declaringClass = ReflectionCache.getCachedClass(metaClass.theClass)
        getParameterTypes()
    }

    int getModifiers() {
        return Modifier.PUBLIC
    }

    String getName() {
        return name
    }

    Class getReturnType() {
        return Object
    }

    CachedClass getDeclaringClass() {
        return declaringClass
    }

    Object invoke(Object object, Object[] arguments) {
        return theMetaClass.invokeMethod(object, name, arguments)
    }

}

class DispatcherMetaClass extends MetaClassImpl {

    private MetaClass originalMetaClass

    private List metaClasses

    private DispatcherMetaClass(Class clazz, MetaClass originalMetaClass) {
        super(clazz)
        this.originalMetaClass = originalMetaClass
        metaClasses = new LinkedList()
    }


    static DispatcherMetaClass getInstance(Class clazz) {
        MetaClassRegistry registry = GroovySystem.metaClassRegistry
        MetaClass metaClass = registry.getMetaClass(clazz)
        if (metaClass instanceof DispatcherMetaClass) {
            return metaClass as DispatcherMetaClass
        } else {
            DispatcherMetaClass filterMetaClass = new DispatcherMetaClass(clazz, metaClass)
            registry.setMetaClass(clazz, filterMetaClass)
            return filterMetaClass
        }
    }

    Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        println "DispatcherMetaClass.invokeMethod($methodName, $arguments)"
        return getMetaClassForInstance(receiver).invokeMethod(sender, receiver, methodName, arguments, isCallToSuper, fromInsideClass)
    }

    Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
        println "DispatcherMetaClass.getProperty($property)"
        return getMetaClassForInstance(receiver).getProperty(sender, receiver, property, isCallToSuper, fromInsideClass)
    }

    void setProperty(Class sender, Object receiver, String property, Object value, boolean isCallToSuper, boolean fromInsideClass) {
        println "DispatcherMetaClass.setProperty($property, $value)"
        getMetaClassForInstance(receiver).setProperty(sender, receiver, property, value, isCallToSuper, fromInsideClass)
    }

    MetaMethod pickMethod(String methodName, Class[] arguments) {
        originalMetaClass.pickMethod(methodName, arguments) ? new ProxyMetaMethod(this, methodName, arguments) : null
    }

    void setMetaClassForInstance(Object instance, MetaClass metaClass) {
        metaClasses << [instance, metaClass]
    }

    MetaClass getMetaClassForInstance(Object instance) {
        MetaClass metaClass = metaClasses.find { obj, mc -> DefaultGroovyMethods.is(instance, obj) }?.get(1)
        return metaClass ?: originalMetaClass
    }

    /**
     * This method should be called to restore the original meta class, but I don't know where to call it during unit
     * tests, although it is OK to leave the dispatcher meta class in the meta class registry
     */
    void stopProxy() {
        GroovySystem.metaClassRegistry.setMetaClass(theClass, originalMetaClass)
    }

}

class GroovyObjectMethodFilter implements CallbackFilter {

    static GroovyObjectMethodFilter instance = new GroovyObjectMethodFilter()

    private GroovyObjectMethodFilter() {}

    int accept(Method method) {
        GroovyObject.metaClass.pickMethod(method.name, method.parameterTypes) ? 0 : 1
    }

}

def mock(Class clazz = Object) {
    def mpmc = new MockProxyMetaClass(clazz)

    if (!Modifier.isFinal(clazz.modifiers)) {
        def groovyMethodInterceptor = { obj, Method method, Object[] args, MethodProxy proxy ->
            println "GroovyMethodInterceptor.invoke($method.name, $args)"
            switch (method.name) {
                case "invokeMethod": return mpmc.invokeMethod(obj, args[0], args[1])
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

        def enhancer = new Enhancer(superclass: superClass, interfaces: interfaces, useFactory: false,
                callbackFilter: GroovyObjectMethodFilter.instance,
                callbackTypes: [groovyMethodInterceptor.class, javaMethodInterceptor.class])
        def mockClass = enhancer.createClass()
        Enhancer.registerCallbacks(mockClass, [groovyMethodInterceptor, javaMethodInterceptor] as Callback[])

        // use objenesis to instantiate the instance
        def mockInstance = ObjenesisHelper.newInstance(mockClass) // mockClass.newInstance()
        return mockInstance
    } else {
        def mockInstance = ObjenesisHelper.newInstance(clazz)
        if (GroovyObject.isAssignableFrom(clazz)) {
            mockInstance.metaClass = mpmc
        } else {
            def fmc = DispatcherMetaClass.getInstance(clazz)
            fmc.setMetaClassForInstance(mockInstance, mpmc)
        }
        return mockInstance
    }
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


println "duck typing mocking".center(80, "-")
def duckTyping = mock()
duckTyping.dynamicMethod("call on groovy side")
println()


println "closure delegate mocking".center(80, "-")
def mockDelegate = mock()
def closure = { ->
    delegateMethod("try delegate method mocking")
}
closure.delegate = mockDelegate
closure()
println()

class ForTestingClosureDelegateProperty {
    static run(mockDelegate) {
        def c = { ->
            setAProperty = "try delegate property mocking"
            println()
            assert getAProperty == null
        }
        c.delegate = mockDelegate
        c()
    }
}
// here I run the closure in a class, because the default resolveStrategy of a closure is OWNER_FIRST,
// if I define a closure in a script, the script will be the owner of the closure, and a script always
// allow setting an undefined property, then our mock delegate will be never invoked for setting the property,
// and I don't want to change the resolveStrategy because it is seldom changed, so I just define the closure in a class
ForTestingClosureDelegateProperty.run(mockDelegate)
println()


println "final class mocking".center(80, "-")
final class AFinalClass {}
def mockGroovyFinalClass = mock(AFinalClass)
mockGroovyFinalClass.someMethod("try groovy final class mocking")
println()
mockGroovyFinalClass.someProperty = "try groovy final class mocking"
println()
closure.delegate = mockGroovyFinalClass
closure()
println()

def mockJavaFinalClass = mock(String)
mockJavaFinalClass.concat("try java final class mocking")
println()
mockJavaFinalClass.someProperty = "try java final class mocking"
println()
closure = { ->
    concat("try java final class mocking")
}
closure.delegate = mockJavaFinalClass
closure()
println()
ForTestingClosureDelegateProperty.run(mockJavaFinalClass)
println()

println "some string: ".concat("try original implements")
println()


println "final method mocking".center(80, "-")
AClassWithFinalMethod mockClassWithFinalMethod = mock(AClassWithFinalMethod)
mockClassWithFinalMethod.aFinalMethod()
println()
// although we can mock final methods on the groovy side, but cannot do so on the java side
new JTest().testFinalMethod(mockClassWithFinalMethod)
println()
