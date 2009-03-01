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

class StaticPropertySetSignature {

    def clazz
    def property
    def argument
    def method

    StaticPropertySetSignature(clazz, property, argument, method = null) {
        this.clazz = clazz
        this.property = property
        this.argument = new ParameterSignature([argument])
        this.method = method
    }

    def validate() {}

    String toString(boolean showMockName = false) {
        if (method) {
            return "'${clazz.simpleName}.$method($argument)'"
        } else {
            return "'${clazz.simpleName}.$property = $argument'"
        }
    }

    private boolean equalsWithoutArgument(Object signature) {
        if (signature == null || getClass() != signature.getClass()) return false
        if (clazz != signature.clazz) return false
        return true
    }

    boolean equals(Object signature) {
        if (!equalsWithoutArgument(signature)) return false
        if (method || signature.method) {
            if (method != signature.method) return false
        } else {
            if (property != signature.property) return false
        }
        if (argument != signature.argument) return false
        return true
    }

    boolean match(Object signature) {
        if (!equalsWithoutArgument(signature)) return false
        if (property != signature.property) return false
        if (!argument.match(signature.argument)) return false
        return true
    }

    int hashCode() {
        clazz.hashCode() * 51 + property.hashCode() * 31 + argument.hashCode()
    }

}
