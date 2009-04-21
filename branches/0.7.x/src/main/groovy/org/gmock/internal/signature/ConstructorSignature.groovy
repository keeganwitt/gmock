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

class ConstructorSignature {

    Class aClass
    def arguments

    ConstructorSignature(aClass, arguments) {
        this.aClass = aClass
        this.arguments = new ParameterSignature(arguments)
    }

    String toString(boolean showMockName = false) {
        "'new $aClass.simpleName($arguments)'"
    }

    def validate(){
    }

    private boolean equalsWithoutArguments(Object constructor) {
        if (constructor == null || getClass() != constructor.getClass()) return false
        if (aClass != constructor.aClass) return false
        return true
    }

    boolean equals(Object constructor) {
        if (!equalsWithoutArguments(constructor)) return false
        if (arguments != constructor.arguments) return false
        return true
    }

    boolean match(Object constructor) {
        if (!equalsWithoutArguments(constructor)) return false
        if (!arguments.match(constructor.arguments)) return false
        return true
    }

    int hashCode() {
        aClass.hashCode() * 31  + arguments.hashCode()
    }

}
