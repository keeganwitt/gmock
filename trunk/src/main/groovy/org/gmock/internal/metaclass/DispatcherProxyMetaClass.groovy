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
package org.gmock.internal.metaclass

import static org.gmock.internal.metaclass.MetaClassHelper.getGMockMethod
import org.gmock.internal.util.WeakIdentityHashMap

class DispatcherProxyMetaClass extends ProxyMetaClass {

    private Map metaClasses = new WeakIdentityHashMap()
    def controller

    private DispatcherProxyMetaClass(MetaClassRegistry registry, Class clazz, MetaClass originalMetaClass) {
        super(registry, clazz, originalMetaClass)
    }

    static DispatcherProxyMetaClass getInstance(Class clazz) {
        MetaClassRegistry registry = GroovySystem.metaClassRegistry
        MetaClass metaClass = registry.getMetaClass(clazz)
        if (metaClass instanceof DispatcherProxyMetaClass) {
            return metaClass
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
        controller.doInternal {
            adaptee.pickMethod(methodName, arguments)
        } {
            if (!controller.replay) {
                def method = getGMockMethod(methodName, arguments, this, controller)
                if (method) return method
            }
            return new ProxyMetaMethod(this, methodName, arguments)
        }
    }

    void setMetaClassForInstance(Object instance, MetaClass mc) {
        controller.doInternal {
            metaClasses.put(instance, mc)
        }
    }

    MetaClass getMetaClassForInstance(Object instance) {
        controller.doInternal {
            adaptee
        } {
            if (metaClasses.empty) {
                stopProxy()
                return adaptee
            } else {
                MetaClass mc = metaClasses.get(instance)
                return mc ?: adaptee
            }
        }
    }

    private void stopProxy() {
        MetaClass metaClass = registry.getMetaClass(theClass)
        if (metaClass.is(this)) {
            registry.setMetaClass(theClass, adaptee)
        }
    }

}
