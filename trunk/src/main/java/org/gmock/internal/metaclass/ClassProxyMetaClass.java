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

import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.ProxyMetaClass;
import org.gmock.internal.Callable;
import org.gmock.internal.InternalMockController;
import org.gmock.internal.MockHelper;
import org.gmock.internal.expectation.Expectation;
import org.gmock.internal.expectation.ExpectationCollection;
import static org.gmock.internal.metaclass.MetaClassHelper.*;
import org.gmock.internal.signature.ConstructorSignature;
import org.gmock.internal.signature.StaticMethodSignature;
import org.gmock.internal.signature.StaticPropertyGetSignature;
import org.gmock.internal.signature.StaticPropertySetSignature;

import java.beans.IntrospectionException;
import java.lang.reflect.Modifier;

/**
 * ClassProxyMetaClass capture all static and constructor call in replay mode.
 */
public class ClassProxyMetaClass extends ProxyMetaClass {

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

            adaptee = registry.getMetaClass(theClass);
            registry.setMetaClass(theClass, this);
        }
    }

    public void stopProxy(){
        if (!empty()) {
            registry.setMetaClass(theClass, adaptee);

            mockClass = null;
        }
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
