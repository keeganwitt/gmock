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

import static org.gmock.internal.metaclass.MetaClassHelper.findExpectation;
import static org.gmock.internal.metaclass.MetaClassHelper.getGetterMethodName;
import static org.gmock.internal.metaclass.MetaClassHelper.getSetterMethodName;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MetaClassRegistryChangeEventListener;
import groovy.lang.ProxyMetaClass;

import java.beans.IntrospectionException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;

import org.gmock.internal.Callable;
import org.gmock.internal.InternalMockController;
import org.gmock.internal.MockHelper;
import org.gmock.internal.expectation.Expectation;
import org.gmock.internal.expectation.ExpectationCollection;
import org.gmock.internal.signature.ConstructorSignature;
import org.gmock.internal.signature.StaticMethodSignature;
import org.gmock.internal.signature.StaticPropertyGetSignature;
import org.gmock.internal.signature.StaticPropertySetSignature;

/**
 * ClassProxyMetaClass capture all static and constructor call in replay mode.
 */
public class ClassProxyMetaClass extends ProxyMetaClass {

    private static final String META_CLASS_REGISTRY_CLEANER_CLASS_NAME = "org.codehaus.groovy.grails.cli.support.MetaClassRegistryCleaner";

    private ExpectationCollection constructorExpectations;
    private ExpectationCollection staticExpectations;
    private boolean constructorExpectationsEmpty;
    private boolean staticExpectationsEmpty;
    private InternalMockController controller;

    private Class mockClass;

    private ClassProxyMetaClass(MetaClassRegistry metaClassRegistry, Class aClass, MetaClass adaptee, InternalMockController controller) throws IntrospectionException {
        super(metaClassRegistry, aClass, adaptee);
        this.controller = controller;
        reset();
    }

    public static ClassProxyMetaClass getInstance(Class theClass, InternalMockController controller) throws IntrospectionException {
        MetaClassRegistry registry = GroovySystem.getMetaClassRegistry();
        MetaClass adaptee = registry.getMetaClass(theClass);
        return new ClassProxyMetaClass(registry, theClass, adaptee, controller);
    }

    public Class getTheClass() {
        return mockClass;
    }

    public void addConstructorExpectation(Expectation expectation) {
        controller.addToExpectations(expectation, constructorExpectations);
        constructorExpectationsEmpty = false;
    }

    public void addStaticExpectation(Expectation expectation) {
        controller.addToExpectations(expectation, staticExpectations);
        staticExpectationsEmpty = false;
    }

    public void startProxy(){
        if (!empty()) {
            mockClass = theClass;

            Collection<MetaClassRegistryChangeEventListener> cleaners = disableMetaClassRegistryCleaners();
            try {
                adaptee = registry.getMetaClass(theClass);
                registry.setMetaClass(theClass, this);
            } finally {
                enableMetaClassRegistryCleaners(cleaners);
            }
        }
    }

    public void stopProxy(){
        if (!empty()) {
            Collection<MetaClassRegistryChangeEventListener> cleaners = disableMetaClassRegistryCleaners();
            try {
                registry.setMetaClass(theClass, adaptee);
            } finally {
                enableMetaClassRegistryCleaners(cleaners);
            }

            mockClass = null;
        }
    }
    
    private void enableMetaClassRegistryCleaners(Collection<MetaClassRegistryChangeEventListener> cleaners) {
        for (MetaClassRegistryChangeEventListener cleaner : cleaners) {
            registry.addMetaClassRegistryChangeEventListener(cleaner);
        }
    }

    private Collection<MetaClassRegistryChangeEventListener> disableMetaClassRegistryCleaners() {
        // A hacky fix for issue 125
        MetaClassRegistryChangeEventListener[] listeners = registry
                .getMetaClassRegistryChangeEventListeners();
        Collection<MetaClassRegistryChangeEventListener> cleaners =
                new LinkedList<MetaClassRegistryChangeEventListener>();
        for (MetaClassRegistryChangeEventListener listener : listeners) {
            if (META_CLASS_REGISTRY_CLEANER_CLASS_NAME.equals(listener.getClass().getName())) {
                cleaners.add(listener);
                registry.removeMetaClassRegistryChangeEventListener(listener);
            }
        }
        return cleaners;
    }

    public void validate(){
        staticExpectations.validate();
    }

    public void verify(){
        constructorExpectations.verify();
        staticExpectations.verify();
    }

    public void reset() {
        constructorExpectations = new ExpectationCollection(controller);
        staticExpectations = new ExpectationCollection(controller);
        constructorExpectationsEmpty = true;
        staticExpectationsEmpty = true;
    }

    public Object invokeConstructor(final Object[] arguments) {
        return checkAndDo(constructorExpectationsEmpty, new Callable() {
            public Object call() {
                return adaptee.invokeConstructor(arguments);
            }
        }, new Callable() {
            public Object call() {
                Object[] removed = removeFirstArgumentOfInnerClass(arguments);
                Object[] evaluated = MockHelper.evaluateGStrings(removed, controller);
                ConstructorSignature signature = new ConstructorSignature(theClass, evaluated);
                return findExpectation(constructorExpectations, signature, "Unexpected constructor call", theClass, null, evaluated, controller);
            }
        });
    }

    private Object[] removeFirstArgumentOfInnerClass(Object[] arguments) {
        if (isInnerClass(mockClass)) {
            Object[] removed = new Object[arguments.length - 1];
            System.arraycopy(arguments, 1, removed, 0, removed.length);
            return removed;
        } else {
            return arguments;
        }
    }

    private boolean isInnerClass(Class<?> clazz) {
        return clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
    }

    public Object invokeStaticMethod(final Object clazz, final String method, final Object[] arguments) {
        return checkAndDo(staticExpectationsEmpty, new Callable() {
            public Object call() {
                return adaptee.invokeStaticMethod(clazz, method, arguments);
            }
        }, new Callable() {
            public Object call() {
                Object[] evaluated = MockHelper.evaluateGStrings(arguments, controller);
                StaticMethodSignature signature = new StaticMethodSignature(clazz, method, evaluated);
                return findExpectation(staticExpectations, signature, "Unexpected static method call", clazz, method, evaluated, controller);
            }
        });
    }

    public Object getProperty(final Object clazz, final String property) {
        return checkAndDo(staticExpectationsEmpty || !(clazz instanceof Class), new Callable() {
            public Object call() {
                return adaptee.getProperty(clazz, property);
            }
        }, new Callable() {
            public Object call() {
                StaticPropertyGetSignature signature = new StaticPropertyGetSignature(clazz, property);
                return findExpectation(staticExpectations, signature, "Unexpected static property getter call", clazz, getGetterMethodName(property), new Object[0], controller);
            }
        });
    }

    public void setProperty(final Object clazz, final String property, final Object value) {
        checkAndDo(staticExpectationsEmpty || !(clazz instanceof Class), new Callable() {
            public Object call() {
                adaptee.setProperty(clazz, property, value);
                return null;
            }
        }, new Callable() {
            public Object call() {
                StaticPropertySetSignature signature = new StaticPropertySetSignature(clazz, property, value);
                findExpectation(staticExpectations, signature, "Unexpected static property setter call", clazz, getSetterMethodName(property), new Object[] {value}, controller);
                return null;
            }
        });
    }

    private Object checkAndDo(boolean condition, Callable invokeOriginal, Callable work) {
        if (condition) {
            return invokeOriginal.call();
        } else {
            return controller.doInternal(invokeOriginal, work);
        }
    }

    private boolean empty() {
        return constructorExpectationsEmpty && staticExpectationsEmpty;
    }

}
