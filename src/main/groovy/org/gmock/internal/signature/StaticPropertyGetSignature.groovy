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

    StaticPropertyGetSignature(clazz, property) {
        this.clazz = clazz
        this.property = property
    }

    def validate() {}

    String toString() {
        "${clazz.simpleName}.$property"
    }

    boolean equals(Object signature) {
        if (signature == null || getClass() != signature.getClass()) return false
        if (clazz != signature.clazz) return false
        if (property != signature.property) return false
        return true
    }

    boolean match(Object signature) {
        return equals(signature)
    }

    int hashCode() {
        clazz.hashCode() * 31 + property.hashCode()
    }

}
