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

class MethodSignature {

    def object
    def methodName
    def arguments

    MethodSignature(object, methodName, arguments) {
        this.object = object
        this.methodName = methodName
        this.arguments = new ParameterSignature(arguments)
    }

    String toString() {
        "$methodName($arguments)"
    }

    def validate(){
    }

    private boolean equalsWithoutArguments(Object method) {
        if (method == null || getClass() != method.getClass()) return false
        if (!object.is(method.object)) return false
        if (methodName != method.methodName) return false
        return true
    }

    boolean equals(Object method) {
        if (!equalsWithoutArguments(method)) return false
        if (arguments != method.arguments) return false
        return true
    }

    boolean match(Object method) {
        if (!equalsWithoutArguments(method)) return false
        if (!arguments.match(method.arguments)) return false
        return true
    }

    int hashCode() {
        object.hashCode() * 51 + methodName.hashCode() * 31 + arguments.hashCode()
    }

}
