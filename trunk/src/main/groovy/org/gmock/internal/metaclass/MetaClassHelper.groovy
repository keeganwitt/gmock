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

import org.gmock.GMock

class MetaClassHelper {

    static findExpectation(expectations, signature, message, arguments, controller) {
        def expectation = controller.orderedExpectations.findMatching(signature) ?: expectations.findMatching(signature)
        if (expectation){
            return expectation.answer(arguments)
        } else {
            controller.fail(message, signature)
        }
    }

    static addToExpectations(expectation, expectations, controller) {
        if (!controller.ordered) {
            controller.unorderedExpectations.add(expectation, expectations)
        } else {
            controller.orderedExpectations.add(expectation)
        }
    }

    static getGMockMethod(String methodName, Class[] arguments, MetaClass metaClass, controller) {
        def method = null, delegator = null
        if (arguments.length == 1 && Closure.isAssignableFrom(arguments[0])) {
            if (methodName == "match") {
                method = GMock.metaClass.pickMethod(methodName, arguments)
                delegator = GMock
            } else if (methodName == "strict" || methodName == "loose") {
                method = controller.metaClass.pickMethod(methodName, arguments)
                delegator = controller
            }
        }
        return method ? new DelegateMetaMethod(metaClass, methodName, arguments, method, delegator) : null
    }

}
