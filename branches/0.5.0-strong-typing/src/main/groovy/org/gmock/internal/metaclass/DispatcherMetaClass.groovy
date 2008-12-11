package org.gmock.internal.metaclass

import java.lang.reflect.Modifier
import org.codehaus.groovy.reflection.CachedClass
import org.codehaus.groovy.reflection.ReflectionCache
import static org.gmock.internal.InternalModeHelper.doInternal

class DispatcherProxyMetaClass extends ProxyMetaClass {

    private List metaClasses
    def controller

    private DispatcherProxyMetaClass(MetaClassRegistry registry, Class clazz, MetaClass originalMetaClass) {
        super(registry, clazz, originalMetaClass)
        metaClasses = new LinkedList()
    }

    static DispatcherProxyMetaClass getInstance(Class clazz) {
        MetaClassRegistry registry = GroovySystem.metaClassRegistry
        MetaClass metaClass = registry.getMetaClass(clazz)
        if (metaClass instanceof DispatcherProxyMetaClass) {
            return metaClass as DispatcherProxyMetaClass
        } else {
            DispatcherProxyMetaClass filterMetaClass = new DispatcherProxyMetaClass(registry, clazz, metaClass)
            registry.setMetaClass(clazz, filterMetaClass)
            return filterMetaClass
        }
    }

    Object invokeMethod(Object object, String methodName, Object[] arguments) {
        return invokeMethod(theClass, object, methodName, arguments, false, false)
    }

    Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        return getMetaClassForInstance(receiver).invokeMethod(sender, receiver, methodName, arguments, isCallToSuper, fromInsideClass)
    }

    Object getProperty(Object object, String property) {
        return getProperty(theClass, object, property, false, false)
    }

    Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
        return getMetaClassForInstance(receiver).getProperty(sender, receiver, property, isCallToSuper, fromInsideClass)
    }

    void setProperty(Object object, String property, Object newValue) {
        setProperty(theClass, object, property, newValue, false, false)
    }

    void setProperty(Class sender, Object receiver, String property, Object value, boolean isCallToSuper, boolean fromInsideClass) {
        getMetaClassForInstance(receiver).setProperty(sender, receiver, property, value, isCallToSuper, fromInsideClass)
    }

    MetaMethod pickMethod(String methodName, Class[] arguments) {
        new ProxyMetaMethod(this, methodName, arguments)
    }

    void setMetaClassForInstance(Object instance, MetaClass metaClass) {
        metaClasses << [instance, metaClass]
    }

    MetaClass getMetaClassForInstance(Object instance) {
        doInternal(controller, { adaptee }) {
            MetaClass metaClass = metaClasses.find { obj, mc -> instance.is(obj) }?.get(1)
            return metaClass ?: adaptee
        }
    }

    /**
     * This method should be called to restore the original meta class, but I don't know where to call it between unit
     * tests, although it is OK to leave the dispatcher meta class in the meta class registry
     */
    // TODO: found out where to restore the original meta class
    void stopProxy() {
        registry.setMetaClass(theClass, adaptee)
    }

}

class ProxyMetaMethod extends MetaMethod {

    private MetaClass theMetaClass
    String name
    CachedClass declaringClass

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

    Class getReturnType() {
        return Object
    }

    Object invoke(Object object, Object[] arguments) {
        return theMetaClass.invokeMethod(object, name, arguments)
    }

}
