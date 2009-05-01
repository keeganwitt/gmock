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
package org.gmock.internal.result

import org.codehaus.groovy.runtime.InvokerHelper
import org.gmock.internal.Result
import static org.codehaus.groovy.runtime.MetaClassHelper.convertToTypeArray

class ReturnMock implements Result {

    def controller
    def mock
    MetaClass proxyMetaClass

    ReturnMock(controller, mock) {
        this.controller = controller
        this.mock = mock
        this.proxyMetaClass = InvokerHelper.getMetaClass(mock)
    }

    def answer(Object receiver, String method, Object[] arguments) {
        def clazz = receiver.class
        def argTypes = convertToTypeArray(arguments)
        def mockClass
        try {
            mockClass = clazz.getMethod(method, argTypes).returnType
        } catch (e) {
            mockClass = clazz.metaClass.pickMethod(method, argTypes)?.returnType ?: Object
        }
        if (mockClass == Object) {
            return mock
        } else {
            return controller.mockWithMetaClass(mockClass, proxyMetaClass)
        }
    }

}
