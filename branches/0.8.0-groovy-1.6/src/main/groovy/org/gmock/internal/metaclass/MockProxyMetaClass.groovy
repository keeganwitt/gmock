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

class MockProxyMetaClass extends ProxyMetaClass {

    MockInternal mock

    def classExpectations
    def controller
    def mockName

    MockProxyMetaClass(Class clazz, classExpectations, controller, mockName) {
        super(GroovySystem.metaClassRegistry, clazz, GroovySystem.metaClassRegistry.getMetaClass(clazz))
        this.classExpectations = classExpectations
        this.controller = controller
        this.mockName = mockName
    }

    Object invokeMethod(Object object, String methodName, Object[] arguments) {
        invokeMethod(theClass, object, methodName, arguments, false, false)
    }

    Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        controller.doInternal {
            adaptee.invokeMethod(receiver, methodName, arguments)
        } {
            return mock.invokeMockMethod(methodName, arguments)
        }
    }

    Object getProperty(Object object, String property) {
        getProperty(theClass, object, property, false, false)
    }

    Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
        controller.doInternal {
            adaptee.getProperty(receiver, property)
        } {
            return mock.getMockProperty(property)
        }
    }

    void setProperty(Object object, String property, Object newValue) {
        setProperty(theClass, object, property, newValue, false, false)
    }

    void setProperty(Class sender, Object receiver, String property, Object value, boolean isCallToSuper, boolean fromInsideClass) {
        controller.doInternal {
            adaptee.setProperty(receiver, property, value)
        } {
            return mock.setMockProperty(property, value)
        }
    }

    MetaMethod pickMethod(String methodName, Class[] arguments) {
        controller.doInternal {
            adaptee.pickMethod(methodName, arguments)
        } {
            return new ProxyMetaMethod(this, methodName, arguments)
        }
    }

}
