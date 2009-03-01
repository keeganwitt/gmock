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

class StaticSignature {

    Class aClass
    def methodName
    def arguments

    StaticSignature(aClass) {
        this.aClass = aClass
    }

    StaticSignature(aClass, methodName, arguments) {
        this.aClass = aClass
        this.methodName = methodName
        this.arguments = new ParameterSignature(arguments)
    }

    String toString(boolean showMockName = false) {
        "'${aClass.simpleName}.$methodName($arguments)'"
    }

    def validate(){
    }

    private boolean equalsWithoutArguments(Object staticSignature) {
        if (staticSignature == null || getClass() != staticSignature.getClass()) return false
        if (aClass != staticSignature.aClass) return false
        if (methodName != staticSignature.methodName) return false
        return true
    }

    boolean equals(Object staticSignature) {
        if (!equalsWithoutArguments(staticSignature)) return false
        if (arguments != staticSignature.arguments) return false
        return true
    }

    boolean match(Object staticSignature) {
        if (!equalsWithoutArguments(staticSignature)) return false
        if (!arguments.match(staticSignature.arguments)) return false
        return true
    }

    int hashCode() {
        aClass.hashCode() * 51  + methodName.hashCode() * 31 + arguments.hashCode()
    }

}
