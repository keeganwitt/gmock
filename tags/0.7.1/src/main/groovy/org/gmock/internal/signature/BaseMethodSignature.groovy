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

abstract class BaseMethodSignature {

    def methodName
    def arguments

    private setterCache = null
    private getterCache = null

    BaseMethodSignature(methodName, arguments) {
        this.methodName = new IdentifierSignature(methodName)
        this.arguments = new ParameterSignature(arguments)
    }
    BaseMethodSignature(methodName) {
        this.methodName = new IdentifierSignature(methodName)
        this.arguments = new MatchAnyParameterSignature()
    }

    def validate() {}

    boolean equals(Object signature) {
        if (signature?.class != this.class) return false
        if (methodName != signature.methodName) return false
        if (arguments != signature.arguments) return false
        return true
    }

    boolean match(Object signature) {
        if (signature == null) return false
        switch (signature.class) {
            case this.class:
                if (!methodName.match(signature.methodName)) return false
                if (!arguments.match(signature.arguments)) return false
                return true
            case getterClass:
                if (!getterName?.match(signature.getterName)) return false
                return true
            case setterClass:
                if (!setterName?.match(signature.setterName)) return false
                if (!arguments.match(signature.arguments)) return false
                return true
            default:
                return false
        }
    }

    protected abstract Class getGetterClass()

    protected abstract Class getSetterClass()

    int hashCode() {
        methodName.hashCode() * 31 + arguments.hashCode()
    }

    boolean isSetter() {
        methodName.setter && arguments.size() == 1
    }

    boolean isGetter() {
        methodName.getter && arguments.size() == 0
    }

    def getSetterName() {
        if (!setterCache) {
            setterCache = new Reference(setter ? methodName.setterName : null)
        }
        return setterCache.get()
    }

    def getGetterName() {
        if (!getterCache) {
            getterCache = new Reference(getter ? methodName.getterName : null)
        }
        return getterCache.get()
    }

}
