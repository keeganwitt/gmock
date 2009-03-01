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

class PropertyGetSignature {

    def object
    def propertyName
    def methodName

    PropertyGetSignature(object, propertyName, methodName = null) {
        this.object = object
        this.propertyName = propertyName
        this.methodName = methodName
    }

    String toString() {
        if (methodName) {
            return "$methodName()"
        } else {
            return propertyName
        }
    }

    def validate(){
    }

    private boolean equalsWithoutName(Object getSignature) {
        if (getSignature == null || getClass() != getSignature.getClass()) return false
        if (!object.is(getSignature.object)) return false
        return true
    }

    boolean equals(Object getSignature) {
        if (!equalsWithoutName(getSignature)) return false
        if (methodName || getSignature.methodName) {
            if (methodName != getSignature.methodName) return false
        } else {
            if (propertyName != getSignature.propertyName) return false
        }
        return true
    }

    boolean match(Object getSignature) {
        if (!equalsWithoutName(getSignature)) return false
        if (propertyName != getSignature.propertyName) return false
        return true
    }

    int hashCode() {
        object.hashCode() * 31 + propertyName.hashCode()
    }

}
