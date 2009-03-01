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

class StaticPropertyGetSignature {

    def clazz
    def property
    def method

    StaticPropertyGetSignature(clazz, property, method = null) {
        this.clazz = clazz
        this.property = property
        this.method = method
    }

    def validate() {}

    String toString(boolean showMockName = false) {
        if (method) {
            return "'${clazz.simpleName}.$method()'"
        } else {
            return "'${clazz.simpleName}.$property'"
        }
    }

    private boolean equalsWithoutArguments(Object signature) {
        if (signature == null || getClass() != signature.getClass()) return false
        if (clazz != signature.clazz) return false
        return true
    }

    boolean equals(Object signature) {
        if (!equalsWithoutArguments(signature)) return false
        if (method || signature.method) {
            if (method != signature.method) return false
        } else {
            if (property != signature.property) return false
        }
        return true
    }

    boolean match(Object signature) {
        if (!equalsWithoutArguments(signature)) return false
        if (property != signature.property) return false
        return true
    }

    int hashCode() {
        clazz.hashCode() * 31 + property.hashCode()
    }

}
