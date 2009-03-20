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

    static setMetaClassTo(object, Class clazz, MetaClass mc, controller) {
        if (GroovyObject.isInstance(object)) {
            object.metaClass = mc
        } else {
            def dpmc = DispatcherProxyMetaClass.getInstance(clazz)
            dpmc.controller = controller
            dpmc.setMetaClassForInstance(object, mc)
        }
    }

    static getMetaClassFrom(object) {
        def metaClass = object.metaClass
        if (metaClass instanceof DispatcherProxyMetaClass) {
            return metaClass.getMetaClassForInstance(object)
        } else {
            return metaClass
        }
    }

}
