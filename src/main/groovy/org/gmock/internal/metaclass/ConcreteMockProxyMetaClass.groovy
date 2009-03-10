package org.gmock.internal.metaclass

import org.gmock.internal.MockInternal
import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.signature.MatchAnyParameterSignature
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.matcher.AlwaysMatchMatcher
import org.gmock.internal.signature.PropertySetSignature

class ConcreteMockProxyMetaClass extends ProxyMetaClass {

    def controller
    def mpmc
    def concreteObject
    def mockName
    MockInternal mock

    ConcreteMockProxyMetaClass(Class clazz, controller, concreteObject, mockName) {
        super(GroovySystem.metaClassRegistry, clazz, GroovySystem.metaClassRegistry.getMetaClass(clazz))
        this.controller = controller
        this.concreteObject = concreteObject
        this.mockName = mockName
    }

    Object invokeMethod(Object object, String methodName, Object[] arguments) {
        invokeMethod(theClass, object, methodName, arguments, false, false)
    }

    Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        if (controller.replay) {
            def signature = new MethodSignature(this, methodName)
            if (mock.findSignature(signature)) {
                return mock.invokeMockMethod(methodName, arguments)
            } else {
                return adaptee.invokeMethod(sender, receiver, methodName, arguments, isCallToSuper, fromInsideClass)
            }
        } else {
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
            if (controller.replay) {
                def signature = new PropertyGetSignature(this, property)
                if (mock.findSignature(signature)) {
                    return mock.getMockProperty(property)
                } else {
                    return adaptee.getProperty(sender, receiver, property, isCallToSuper, fromInsideClass)
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
            adaptee.setProperty(receiver, property, value)
        } {
            if (controller.replay) {
                def signature = new PropertySetSignature(this, property)
                if (mock.findSignature(signature)) {
                    return mock.setMockProperty(property, value)
                } else {
                    return adaptee.setProperty(sender, receiver, property, value, isCallToSuper, fromInsideClass)
                }
            } else {
                return mock.setMockProperty(property, value)
            }
        }
    }



}
