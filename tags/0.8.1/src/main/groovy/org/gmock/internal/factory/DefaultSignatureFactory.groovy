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
package org.gmock.internal.factory

import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature

class DefaultSignatureFactory {

    def createMethodSignature(mock, method, arguments) {
        new MethodSignature(mock, method, arguments)
    }

    def createPropertyGetSignature(mock, property) {
        new PropertyGetSignature(mock, property)
    }

    def createPropertySetSignature(mock, property, value) {
        new PropertySetSignature(mock, property, value)
    }

}
