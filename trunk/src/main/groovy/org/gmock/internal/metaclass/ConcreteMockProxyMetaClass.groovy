package org.gmock.internal.metaclass

import org.gmock.internal.MockInternal
import static org.gmock.internal.InternalModeHelper.doInternal

class ConcreteMockProxyMetaClass extends ProxyMetaClass {

    def controller
    def mpmc
    def concreteObject
    def mockName
    MockInternal mock

    LinkedHashSet mockedMethod = new LinkedHashSet()

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
            if (mockedMethod.contains(methodName)) {
                return mock.invokeMockMethod(methodName, arguments)
            } else {
                return adaptee.invokeMethod(sender, receiver, methodName, arguments, isCallToSuper, fromInsideClass)
            }
        } else {
            mockedMethod.add(methodName)
            return mock.invokeMockMethod(methodName, arguments)
        }
    }


}
