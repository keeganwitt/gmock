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

class PropertySetSignature {

    def object
    def propertyName
    def argument
    def methodName

    PropertySetSignature(object, propertyName, argument, methodName = null) {
        this.object = object
        this.propertyName = propertyName
        this.argument = new ParameterSignature([argument])
        this.methodName = methodName
    }

    String toString() {
        if (methodName) {
            return "$methodName($argument)"
        } else {
            return "$propertyName = $argument"
        }
    }

    def validate(){
    }

    private boolean equalsWithoutName(Object setSignature) {
        if (setSignature == null || getClass() != setSignature.getClass()) return false
        if (!object.is(setSignature.object)) return false
        return true
    }

    boolean equals(Object setSignature) {
        if (!equalsWithoutName(setSignature)) return false
        if (methodName || setSignature.methodName) {
            if (methodName != setSignature.methodName) return false
        } else {
            if (propertyName != setSignature.propertyName) return false
        }
        if (argument != setSignature.argument) return false
        return true
    }

    boolean match(Object setSignature) {
        if (!equalsWithoutName(setSignature)) return false
        if (propertyName != setSignature.propertyName) return false
        if (!argument.match(setSignature.argument)) return false
        return true
    }

    int hashCode() {
        object.hashCode() * 51 + propertyName.hashCode() * 31 + argument.hashCode()
    }

}
