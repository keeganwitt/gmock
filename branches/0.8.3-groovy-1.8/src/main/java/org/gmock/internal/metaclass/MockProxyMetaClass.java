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
import groovy.lang.MetaMethod;
import groovy.lang.ProxyMetaClass;
import org.gmock.internal.Callable;
import org.gmock.internal.InternalMockController;
import org.gmock.internal.MockInternal;
import org.codehaus.groovy.runtime.wrappers.PojoWrapper;

import java.beans.IntrospectionException;

public class MockProxyMetaClass extends ProxyMetaClass {

    private MockInternal mock;
    private InternalMockController controller;

    MockProxyMetaClass(Class clazz, InternalMockController controller, MockInternal mock) throws IntrospectionException {
        super(GroovySystem.getMetaClassRegistry(), clazz, GroovySystem.getMetaClassRegistry().getMetaClass(clazz));
        this.controller = controller;
        this.mock = mock;
    }

    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        return invokeMethod(theClass, object, methodName, arguments, false, false);
    }

    public Object invokeMethod(final Class sender, final Object receiver, final String methodName, final Object[] arguments, final boolean isCallToSuper, final boolean fromInsideClass) {
        unWrappArguments(arguments);
        return controller.doInternal(new Callable() {
            public Object call() {
                return adaptee.invokeMethod(sender, receiver, methodName, arguments, isCallToSuper, fromInsideClass);
            }
        }, new Callable() {
            public Object call() {
                return mock.invokeMockMethod(receiver, methodName, arguments);
            }
        });
    }

    public Object getProperty(Object object, String property) {
        return getProperty(theClass, object, property, false, false);
    }

    public Object getProperty(final Class sender, final Object receiver, final String property, final boolean isCallToSuper, final boolean fromInsideClass) {
        return controller.doInternal(new Callable() {
            public Object call() {
                return adaptee.getProperty(sender, receiver, property, isCallToSuper, fromInsideClass);
            }
        }, new Callable() {
            public Object call() {
                return mock.getMockProperty(receiver, property);
            }
        });
    }

    public void setProperty(Object object, String property, Object newValue) {
        setProperty(theClass, object, property, newValue, false, false);
    }

    public void setProperty(final Class sender, final Object receiver, final String property, final Object value, final boolean isCallToSuper, final boolean fromInsideClass) {
        controller.doInternal(new Callable() {
            public Object call() {
                adaptee.setProperty(sender, receiver, property, value, isCallToSuper, fromInsideClass);
                return null;
            }
        }, new Callable() {
            public Object call() {
                mock.setMockProperty(receiver, property, value);
                return null;
            }
        });
    }

    public MetaMethod pickMethod(final String methodName, final Class[] arguments) {
        return (MetaMethod) controller.doInternal(new Callable() {
            public Object call() {
                return adaptee.pickMethod(methodName, arguments);
            }
        }, new Callable() {
            public Object call() {
                return new ProxyMetaMethod(MockProxyMetaClass.this, methodName, arguments);
            }
        });
    }

    public MockInternal getMock() {
        return mock;
    }


    private void unWrappArguments(Object[] arguments){
        for (int i=0; i<arguments.length; i++){
            if (arguments[i] instanceof PojoWrapper){
                PojoWrapper wrapper = (PojoWrapper) arguments[i];
                arguments[i] = wrapper.unwrap();
            }
        }
    }

}
