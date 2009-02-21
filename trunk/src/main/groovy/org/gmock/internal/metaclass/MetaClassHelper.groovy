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

import static junit.framework.Assert.fail
import org.gmock.internal.signature.*

class MetaClassHelper {

    static findExpectation(expectations, signature, message, arguments) { // TODO: remove it
        findExpectation(expectations, signature, message, arguments, null)
    }

    static findExpectation(expectations, signature, message, arguments, controller) {
        def expectation = null

        if (controller) { // TODO: remove it
            expectation = controller.orderedExpectations.findMatching(signature)
        }

        if (!expectation) {
            expectation = expectations.findMatching(signature)
        }

        if (expectation){
            return expectation.answer(arguments)
        } else {
            def callState = expectations.callState(signature).toString()
            if (callState) { callState = "\n$callState" }
            fail("$message '${signature}'$callState")
        }
    }

    static newSignatureForMethod(methodName, arguments) {
        if (isGetter(methodName, arguments)) {
            return new PropertyGetSignature(getPropertyForGetter(methodName), methodName)
        } else if (isSetter(methodName, arguments)) {
            return new PropertySetSignature(getPropertyForSetter(methodName), arguments[0], methodName)
        } else {
            return new MethodSignature(methodName, arguments)
        }
    }

    static newSignatureForStaticMethod(aClass, methodName, arguments) {
        if (isGetter(methodName, arguments)) {
            return new StaticPropertyGetSignature(aClass, getPropertyForGetter(methodName), methodName)
        } else if (isSetter(methodName, arguments)) {
            return new StaticPropertySetSignature(aClass, getPropertyForSetter(methodName), arguments[0], methodName)
        } else {
            return new StaticSignature(aClass, methodName, arguments)
        }
    }

    static isGMockMethod(String methodName, Class[] arguments) {
        methodName == "match" && arguments.length == 1 && Closure.isAssignableFrom(arguments[0])
    }

    private static isSetter(method, arguments) {
        return method.startsWith("set") && method.size() > 3 && Character.isUpperCase(method.charAt(3)) && arguments.size() == 1
    }

    private static isGetter(method, arguments) {
        def firstChar
        if (method.startsWith("get") && method.size() > 3) {
            firstChar = method.charAt(3)
        } else if (method.startsWith("is") && method.size() > 2) {
            firstChar = method.charAt(2)
        } else {
            return false
        }
        return Character.isUpperCase(firstChar) && arguments.size() == 0
    }

    private static getPropertyForSetter(method) {
        return convertPropertyName(method.substring(3))
    }

    private static getPropertyForGetter(method) {
        if (method.startsWith("get")) {
            return convertPropertyName(method.substring(3))
        } else {
            return convertPropertyName(method.substring(2))
        }
    }

    private static convertPropertyName(method) {
        method[0].toLowerCase() + method.substring(1)
    }

}
