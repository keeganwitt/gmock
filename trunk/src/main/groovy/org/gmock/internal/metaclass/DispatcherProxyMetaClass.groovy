package org.gmock.internal.metaclass

import static org.gmock.internal.InternalModeHelper.doInternal

class DispatcherProxyMetaClass extends ProxyMetaClass {

    private Map metaClasses = [:]
    def controller

    private DispatcherProxyMetaClass(MetaClassRegistry registry, Class clazz, MetaClass originalMetaClass) {
        super(registry, clazz, originalMetaClass)
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
        invokeMethod(theClass, object, methodName, arguments, false, false)
    }

    Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        getMetaClassForInstance(receiver).invokeMethod(sender, receiver, methodName, arguments, isCallToSuper, fromInsideClass)
    }

    Object getProperty(Object object, String property) {
        getProperty(theClass, object, property, false, false)
    }

    Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
        getMetaClassForInstance(receiver).getProperty(sender, receiver, property, isCallToSuper, fromInsideClass)
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

    void setMetaClassForInstance(Object instance, MetaClass mc) {
        doInternal(controller) {
            metaClasses.put(new InstanceWrapper(instance), mc)
        }
    }

    MetaClass getMetaClassForInstance(Object instance) {
        doInternal(controller, { adaptee }) {
            MetaClass mc = metaClasses.get(new InstanceWrapper(instance))
            return mc ?: adaptee
        }
    }

    /**
     * This method should be called to restore the original meta class, but I don't know where to call it between unit
     * tests except in the tearDown() method, although it is OK to leave the dispatcher meta class in the meta class
     * registry
     */
    void stopProxy() {
        registry.setMetaClass(theClass, adaptee)
    }

}

/**
 * This wrapper overrides the equals() method so that the wrapped instances are equal if and only if they are exactly
 * the same instance.
 */
class InstanceWrapper {

    def instance

    InstanceWrapper(instance) {
        this.instance = instance
    }

    public int hashCode() {
        instance.hashCode()
    }

    public boolean equals(Object obj) {
        if (obj instanceof InstanceWrapper) {
            return instance.is(obj.instance)
        } else {
            return instance.is(obj)
        }
    }

}
