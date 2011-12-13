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
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.gmock.internal.Callable;
import org.gmock.internal.InternalMockController;
import org.gmock.internal.MockInternal;
import org.gmock.internal.signature.MethodSignature;
import org.gmock.internal.signature.PropertyGetSignature;
import org.gmock.internal.signature.PropertySetSignature;

import java.beans.IntrospectionException;

public class ConcreteMockProxyMetaClass extends ProxyMetaClass {

    private InternalMockController controller;
    private Object concreteObject;
    private MockInternal mock;

    public ConcreteMockProxyMetaClass(Class clazz, InternalMockController controller, Object concreteObject, MockInternal mock) throws IntrospectionException {
        super(GroovySystem.getMetaClassRegistry(), clazz, InvokerHelper.getMetaClass(concreteObject));
        this.controller = controller;
        this.concreteObject = concreteObject;
        this.mock = mock;
    }

    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        return invokeMethod(theClass, object, methodName, arguments, false, false);
    }

    public Object invokeMethod(final Class sender, final Object receiver, final String methodName, final Object[] arguments, final boolean isCallToSuper, final boolean fromInsideClass) {
        return controller.doInternal(new Callable() {
            public Object call() {
                return adaptee.invokeMethod(sender, concreteObject, methodName, arguments, isCallToSuper, fromInsideClass);
            }
        }, new Callable() {
            public Object call() {
                if (controller.getReplay()) {
                    MethodSignature signature = new MethodSignature(mock, methodName);
                    if (mock.findSignature(signature) != null) {
                        return mock.invokeMockMethod(receiver, methodName, arguments);
                    } else {
                        return controller.doExternal(new Callable() {
                            public Object call() {
                                return adaptee.invokeMethod(sender, concreteObject, methodName, arguments, isCallToSuper, fromInsideClass);
                            }
                        });
                    }
                } else {
                    return mock.invokeMockMethod(receiver, methodName, arguments);
                }
            }
        });
    }

    public Object getProperty(Object object, String property) {
        return getProperty(theClass, object, property, false, false);
    }

    public Object getProperty(final Class sender, final Object receiver, final String property, final boolean isCallToSuper, final boolean fromInsideClass) {
        return controller.doInternal(new Callable() {
            public Object call() {
                return adaptee.getProperty(sender, concreteObject, property, isCallToSuper, fromInsideClass);
            }
        }, new Callable() {
            public Object call() {
                if (controller.getReplay()) {
                    PropertyGetSignature signature = new PropertyGetSignature(mock, property);
                    if (mock.findSignature(signature) != null) {
                        return mock.getMockProperty(receiver, property);
                    } else {
                        return controller.doExternal(new Callable() {
                            public Object call() {
                                return adaptee.getProperty(sender, concreteObject, property, isCallToSuper, fromInsideClass);
                            }
                        });
                    }
                } else {
                    return mock.getMockProperty(receiver, property);
                }
            }
        });
    }

    public void setProperty(Object object, String property, Object newValue) {
        setProperty(theClass, object, property, newValue, false, false);
    }

    public void setProperty(final Class sender, final Object receiver, final String property, final Object value, final boolean isCallToSuper, final boolean fromInsideClass) {
        controller.doInternal(new Callable() {
            public Object call() {
                adaptee.setProperty(sender, concreteObject, property, value, isCallToSuper, fromInsideClass);
                return null;
            }
        }, new Callable() {
            public Object call() {
                if (controller.getReplay()) {
                    PropertySetSignature signature = new PropertySetSignature(mock, property);
                    if (mock.findSignature(signature) != null) {
                        mock.setMockProperty(receiver, property, value);
                    } else {
                        controller.doExternal(new Callable() {
                            public Object call() {
                                adaptee.setProperty(sender, concreteObject, property, value, isCallToSuper, fromInsideClass);
                                return null;
                            }
                        });
                    }
                } else {
                    mock.setMockProperty(receiver, property, value);
                }
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
                return new ProxyMetaMethod(ConcreteMockProxyMetaClass.this, methodName, arguments);
            }
        });
    }

    public void startProxy() {
        DefaultGroovyMethods.setMetaClass(concreteObject, this);
    }

    public void stopProxy() {
        DefaultGroovyMethods.setMetaClass(concreteObject, adaptee);
    }

    public MockInternal getMock() {
        return mock;
    }

}
