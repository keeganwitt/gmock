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

class PropertySetSignature extends BasePropertySetSignature {

    def object
    @Delegate PropertySignature propertySignature

    PropertySetSignature(object, setterName, arguments) {
        super(setterName, arguments)
        this.object = object
        propertySignature = new PropertySignature(object, setterName)
    }

    PropertySetSignature(object, setterName) {
        super(setterName)
        this.object = object
    }

    String toString() {
        "$setterName = $arguments"
    }

    String toString(boolean showMockName) {
        "'${toString()}'${object.mockName.toString(showMockName)}"
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

    protected List getAcceptedClasses() {
        [PropertySetSignature, MethodSignature]
    }

    int hashCode() {
        object.hashCode() * 51 + super.hashCode()
    }

}
