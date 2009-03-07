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

abstract class BasePropertyGetSignature {

    def getterName

    BasePropertyGetSignature(getterName) {
        this.getterName = new IdentifierSignature(getterName)
    }

    def validate() {}

    boolean equals(Object signature) {
        if (signature?.class != this.class) return false
        if (getterName != signature.getterName) return false
        return true
    }

    boolean match(Object signature) {
        if (!(signature?.class in acceptedClasses)) return false
        if (!getterName.match(signature.getterName)) return false
        return true
    }

    protected abstract List getAcceptedClasses()

    int hashCode() {
        getterName.hashCode()
    }

}
