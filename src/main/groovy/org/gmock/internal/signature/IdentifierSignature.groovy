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

class IdentifierSignature {

    String identifier

    IdentifierSignature(String identifier) {
        this.identifier = identifier
    }

    String toString() {
        valid ? identifier : identifier.inspect()
    }

    boolean equals(Object signature) {
        if (signature?.class != IdentifierSignature) return false
        if (identifier != signature.identifier) return false
        return true
    }

    boolean match(Object signature) {
        if (signature?.class != IdentifierSignature) return false
        if (bidirectRegexMatch(signature.identifier, identifier)) return true
        return false
    }

    int hashCode() {
        identifier.hashCode()
    }

    boolean isValid() {
        Character.isJavaIdentifierStart(identifier.charAt(0)) && identifier.substring(1).every { Character.isJavaIdentifierPart(it as char) }
    }

    boolean isSetter() {
        valid && identifier.startsWith("set") && identifier.size() > 3 && Character.isUpperCase(identifier.charAt(3))
    }

    boolean isGetter() {
        def firstChar
        if (identifier.startsWith("get") && identifier.size() > 3) {
            firstChar = identifier.charAt(3)
        } else if (identifier.startsWith("is") && identifier.size() > 2) {
            firstChar = identifier.charAt(2)
        } else {
            return false
        }
        return valid && Character.isUpperCase(firstChar)
    }

    def getSetterName() {
        convertPropertyName(identifier.substring(3))
    }

    def getGetterName() {
        if (identifier.startsWith("get")) {
            return convertPropertyName(identifier.substring(3))
        } else {
            return convertPropertyName(identifier.substring(2))
        }
    }

    private convertPropertyName(String name) {
        new IdentifierSignature(name[0].toLowerCase() + name.substring(1))
    }

    private bidirectRegexMatch(a,b){
        return a ==~ b || b ==~ a
    }

}
