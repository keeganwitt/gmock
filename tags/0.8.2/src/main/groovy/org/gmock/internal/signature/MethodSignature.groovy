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
package org.gmock.internal.signature

class MethodSignature extends BaseMethodSignature {

    def object

    MethodSignature(object, methodName, arguments) {
        super(methodName, arguments)
        this.object = object
    }

    MethodSignature(object, methodName) {
        super(methodName)
        this.object = object
    }

    String toString() {
        "$methodName($arguments)"
    }

    String toString(boolean showMockName, String postfix = '') {
        "'${toString()}$postfix'${object.mockName.toString(showMockName)}"
    }

    boolean equals(Object signature) {
        if (!super.equals(signature)) return false
        if (!object.is(signature.object)) return false
        return true
    }

    boolean match(Object signature) {
        if (!super.match(signature)) return false
        if (!object.is(signature.object)) return false
        return true
    }

    protected Class getGetterClass() {
        PropertyGetSignature
    }

    protected Class getSetterClass() {
        PropertySetSignature
    }

    int hashCode() {
        object.hashCode() * 51 + super.hashCode()
    }

}
