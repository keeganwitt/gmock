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

class MetaClassHelper {

    static Object findExpectation(expectations, signature, message, mock, method, arguments, controller) {
        def expectation = controller.orderingController.orderedExpectations.findMatching(signature) ?: expectations.findMatching(signature)
        if (expectation){
            return expectation.answer(mock, method, arguments)
        } else {
            controller.fail(message, signature)
        }
    }


    static String getGetterMethodName(String property) {
        'get' + capitalize(property)
    }

    static String getSetterMethodName(String property) {
        'set' + capitalize(property)
    }

    private static capitalize(String s) {
        s[0].toUpperCase() + s.substring(1)
    }

}
