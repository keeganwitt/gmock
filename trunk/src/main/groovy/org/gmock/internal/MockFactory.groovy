package org.gmock.internal

import org.gmock.internal.recorder.ConstructorRecorder
import org.gmock.internal.recorder.InvokeConstructorRecorder
import org.gmock.internal.recorder.MockNameRecorder
import org.gmock.GMockController

class MockFactory {

    def parseMockArgument(clazz, args) {
        def mockArgs = [:]
        if (clazz == Object && args.size() == 1 && !(instanceOfKnowClass(args[0]))) {
            mockArgs.concreteInstance = args[0]
            mockArgs.clazz = args[0].class
        } else {
            mockArgs.clazz = clazz
            args.each {arg ->
                if (arg instanceof ConstructorRecorder) {
                    mockArgs.constructorRecorder = checkUnset(mockArgs.constructorRecorder, arg, clazz, args)
                } else if (arg instanceof InvokeConstructorRecorder) {
                    mockArgs.invokeConstructorRecorder = checkUnset(mockArgs.invokeConstructorRecorder, arg, clazz, args)
                } else if (arg instanceof MockNameRecorder) {
                    mockArgs.mockNameRecorder = checkUnset(mockArgs.mockNameRecorder, arg, clazz, args)
                } else if (arg instanceof Closure) {
                    mockArgs.expectationClosure = checkUnset(mockArgs.expectationClosure, arg, clazz, args)
                } else {
                    invalidMockMethod(clazz, args)
                }
            }
        }
        return mockArgs
    }
    private instanceOfKnowClass(object){
        if (object instanceof ConstructorRecorder) return true
        if (object instanceof InvokeConstructorRecorder) return true
        if (object instanceof MockNameRecorder) return true
        if (object instanceof Closure) return true
        return false
    }

    private checkUnset(value, arg, Class clazz, Object[] args) {
        if (value) {
            invalidMockMethod(clazz, args)
        } else {
            return arg
        }
    }

    private invalidMockMethod(Class clazz, Object[] args) {
        throw new MissingMethodException("mock", GMockController, [clazz, * args] as Object[])
    }


}