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
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;

import java.beans.IntrospectionException;
import java.util.List;

public class GeneratedMockProxyMetaClass extends ProxyMetaClass {

    private GeneratedMockProxyMetaClass(MetaClassRegistry registry, Class clazz, MetaClass adaptee) throws IntrospectionException {
        super(registry, clazz, adaptee);
    }

    public static void startProxy(Object object, MetaClass adaptee) throws IntrospectionException {
        MetaClassRegistryImpl registry = (MetaClassRegistryImpl) GroovySystem.getMetaClassRegistry();
        MetaClass metaClass = new GeneratedMockProxyMetaClass(registry, object.getClass(), adaptee);
        registry.setMetaClass(object, metaClass);
    }

    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        return adaptee.invokeMethod(object, methodName, arguments);
    }

    public Object invokeMethod(Object object, String methodName, Object arguments) {
        return adaptee.invokeMethod(object, methodName, arguments);
    }

    public Object invokeMethod(final Class sender, final Object receiver, final String methodName, final Object[] arguments, final boolean isCallToSuper, final boolean fromInsideClass) {
        return adaptee.invokeMethod(sender, receiver, methodName, arguments, isCallToSuper, fromInsideClass);
    }

    public Object getProperty(Object object, String property) {
        return adaptee.getProperty(object, property);
    }

    public Object getProperty(final Class sender, final Object receiver, final String property, final boolean isCallToSuper, final boolean fromInsideClass) {
        return adaptee.getProperty(sender, receiver, property, isCallToSuper, fromInsideClass);
    }

    public void setProperty(Object object, String property, Object newValue) {
        adaptee.setProperty(object, property, newValue);
    }

    public Object getAttribute(Object object, String attribute) {
        return adaptee.getAttribute(object, attribute);
    }

    public void setAttribute(Object object, String attribute, Object newValue) {
        adaptee.setAttribute(object, attribute, newValue);
    }

    public void setProperty(final Class sender, final Object receiver, final String property, final Object value, final boolean isCallToSuper, final boolean fromInsideClass) {
        adaptee.setProperty(sender, receiver, property, value, isCallToSuper, fromInsideClass);
    }

    public Object invokeMissingMethod(Object instance, String methodName, Object[] arguments) {
        return adaptee.invokeMissingMethod(instance, methodName, arguments);
    }

    public Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        return adaptee.invokeMissingProperty(instance, propertyName, optionalValue, isGetter);
    }

    public Object getAttribute(Class sender, Object receiver, String messageName, boolean useSuper) {
        return adaptee.getAttribute(sender, receiver, messageName, useSuper);
    }

    public void setAttribute(Class sender, Object receiver, String messageName, Object messageValue, boolean useSuper, boolean fromInsideClass) {
        adaptee.setAttribute(sender, receiver, messageName, messageValue, useSuper, fromInsideClass);
    }

    public List getProperties() {
        return adaptee.getProperties();
    }

    public List getMethods() {
        return adaptee.getMethods();
    }

    public List respondsTo(Object obj, String name, Object[] argTypes) {
        return adaptee.respondsTo(obj, name, argTypes);
    }

    public List respondsTo(Object obj, String name) {
        return adaptee.respondsTo(obj, name);
    }

    public MetaProperty hasProperty(Object obj, String name) {
        return adaptee.hasProperty(obj, name);
    }

    public MetaProperty getMetaProperty(String name) {
        return adaptee.getMetaProperty(name);
    }

    public MetaMethod getMetaMethod(String name, Object[] args) {
        return adaptee.getMetaMethod(name, args);
    }

    public List getMetaMethods() {
        return adaptee.getMetaMethods();
    }

    public MetaMethod pickMethod(final String methodName, final Class[] arguments) {
        return adaptee.pickMethod(methodName, arguments);
    }

}
