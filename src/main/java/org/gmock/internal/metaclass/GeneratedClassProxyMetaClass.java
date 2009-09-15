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
package org.gmock.internal.metaclass;

import groovy.lang.*;

import java.beans.IntrospectionException;

public class GeneratedClassProxyMetaClass extends MetaClassImpl implements AdaptingMetaClass {

    private Class adapteeClass;

    private GeneratedClassProxyMetaClass(MetaClassRegistry registry, Class clazz, Class adapteeClass) throws IntrospectionException {
        super(registry, clazz);
        this.adapteeClass = adapteeClass;
    }

    public static void startProxy(Class clazz, Class adapteeClass) throws IntrospectionException {
        MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();
        MetaClass metaClass = registry.getMetaClass(clazz);
        if (!(metaClass instanceof GeneratedClassProxyMetaClass)) { // has not started proxy yet
            metaClass = new GeneratedClassProxyMetaClass(registry, clazz, adapteeClass);
            metaClass.initialize();
            registry.setMetaClass(clazz, metaClass);
        }
    }

    public Object getAttribute(Object object, String attribute) {
        return getAdaptee().getAttribute(object, attribute);
    }

    public void setAttribute(Object object, String attribute, Object newValue) {
        getAdaptee().setAttribute(object, attribute, newValue);
    }

    public Object getAttribute(Class sender, Object receiver, String messageName, boolean useSuper) {
        return getAdaptee().getAttribute(sender, receiver, messageName, useSuper);
    }

    public void setAttribute(Class sender, Object receiver, String messageName, Object messageValue, boolean useSuper, boolean fromInsideClass) {
        getAdaptee().setAttribute(sender, receiver, messageName, messageValue, useSuper, fromInsideClass);
    }

    public MetaClass getAdaptee() {
        return registry.getMetaClass(adapteeClass);
    }

    public void setAdaptee(MetaClass metaClass) {
        throw new UnsupportedOperationException();
    }

}
