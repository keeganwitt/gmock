package org.gmock.internal.metaclass

import java.lang.reflect.Modifier
import org.codehaus.groovy.reflection.CachedClass
import org.codehaus.groovy.reflection.ReflectionCache

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
        Modifier.PUBLIC
    }

    Class getReturnType() {
        Object
    }

    Object invoke(Object object, Object[] arguments) {
        theMetaClass.invokeMethod(object, name, arguments)
    }

}
