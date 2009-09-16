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

import org.gmock.internal.MockInternal
import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature
import static org.gmock.internal.metaclass.MetaClassHelper.getMetaClassFrom
import static org.gmock.internal.metaclass.MetaClassHelper.setMetaClassTo

class ConcreteMockProxyMetaClass extends ProxyMetaClass {

    def controller
    def mpmc
    def concreteObject
    def mockName
    MockInternal mock

    ConcreteMockProxyMetaClass(Class clazz, controller, concreteObject, mockName) {
        super(GroovySystem.metaClassRegistry, clazz, getMetaClassFrom(concreteObject))
        this.controller = controller
        this.concreteObject = concreteObject
        this.mockName = mockName
    }


    Object invokeMethod(Object object, String methodName, Object[] arguments) {
        invokeMethod(theClass, object, methodName, arguments, false, false)
    }

    Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        controller.doInternal {
            return adaptee.invokeMethod(sender, concreteObject, methodName, arguments, isCallToSuper, fromInsideClass)
        } {
            if (controller.replay) {
                def signature = new MethodSignature(this, methodName)
                if (mock.findSignature(signature)) {
                    return mock.invokeMockMethod(methodName, arguments)
                } else {
                    return controller.doExternal {
                        adaptee.invokeMethod(sender, concreteObject, methodName, arguments, isCallToSuper, fromInsideClass)
                    }
                }
            } else {
                return mock.invokeMockMethod(methodName, arguments)
            }
        }
    }

    Object getProperty(Object object, String property) {
        getProperty(theClass, object, property, false, false)
    }

    Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
        controller.doInternal {
            adaptee.getProperty(sender, concreteObject, property, isCallToSuper, fromInsideClass)
        } {
            if (controller.replay) {
                def signature = new PropertyGetSignature(this, property)
                if (mock.findSignature(signature)) {
                    return mock.getMockProperty(property)
                } else {
                    return controller.doExternal {
                        adaptee.getProperty(sender, concreteObject, property, isCallToSuper, fromInsideClass)
                    }
                }
            } else {
                return mock.getMockProperty(property)
            }
        }
    }

    void setProperty(Object object, String property, Object newValue) {
        setProperty(theClass, object, property, newValue, false, false)
    }

    void setProperty(Class sender, Object receiver, String property, Object value, boolean isCallToSuper, boolean fromInsideClass) {
        controller.doInternal {
            adaptee.setProperty(sender, concreteObject, property, value, isCallToSuper, fromInsideClass)
        } {
            if (controller.replay) {
                def signature = new PropertySetSignature(this, property)
                if (mock.findSignature(signature)) {
                    return mock.setMockProperty(property, value)
                } else {
                    return controller.doExternal {
                        adaptee.setProperty(sender, concreteObject, property, value, isCallToSuper, fromInsideClass)
                    }
                }
            } else {
                return mock.setMockProperty(property, value)
            }
        }
    }

    MetaMethod pickMethod(String methodName, Class[] arguments) {
        controller.doInternal {
            adaptee.pickMethod(methodName, arguments)
        } {
            return new ProxyMetaMethod(this, methodName, arguments)
        }
    }

    def startProxy() {
        setMetaClassTo(concreteObject, theClass, this, controller)
    }

    def stopProxy() {
        setMetaClassTo(concreteObject, theClass, adaptee, controller)
    }

}
