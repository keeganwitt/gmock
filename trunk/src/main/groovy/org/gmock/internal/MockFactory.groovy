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
package org.gmock.internal

import org.gmock.internal.recorder.ConstructorRecorder
import org.gmock.internal.recorder.InvokeConstructorRecorder
import org.gmock.internal.recorder.MockNameRecorder
import org.gmock.GMockController

class MockFactory {

    private static final ARG_CLASSES = [constructorRecorder: ConstructorRecorder,
                                        invokeConstructorRecorder: InvokeConstructorRecorder,
                                        mockNameRecorder: MockNameRecorder,
                                        expectationClosure: Closure]

    static parseMockArgument(clazz, args) {
        def mockArgs = [:]
        if (clazz == Object && args.size() == 1 && !instanceOfKnowClass(args[0])) {
            mockArgs.concreteInstance = args[0]
            mockArgs.clazz = args[0].class
        } else {
            mockArgs.clazz = clazz
            args.each {arg ->
                def name = findArgName(arg)
                if (name) {
                    checkAndSet(mockArgs, name, arg, clazz, args)
                } else {
                    invalidMockMethod(clazz, args)
                }
            }
        }
        return mockArgs
    }

    private static findArgName(arg) {
        ARG_CLASSES.find {k, Class c -> c.isInstance(arg) }?.key
    }

    private static boolean instanceOfKnowClass(object) {
        findArgName(object)
    }

    private static checkAndSet(Map mockArgs, String name, Object arg, Class clazz, Object[] args) {
        if (mockArgs[name]) {
            invalidMockMethod(clazz, args)
        } else {
            mockArgs[name] = arg
        }
    }

    private static invalidMockMethod(Class clazz, Object[] args) {
        throw new MissingMethodException("mock", GMockController, [clazz, *args] as Object[])
    }

}
