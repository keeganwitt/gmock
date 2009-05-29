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

class ChainsSignature {

    def previous
    def delegate

    ChainsSignature(previous, delegate) {
        this.previous = previous
        this.delegate = delegate
    }

    def validate() {
        delegate.validate()
    }

    boolean equals(Object signature) {
        signature?.class == this.class && delegate.equals(signature.delegate)
    }

    boolean match(Object signature) {
        signature?.class == this.class ? delegate.match(signature.delegate) : delegate.match(signature)
    }

    int hashCode() {
        delegate.hashCode()
    }

    String toString(boolean showMockName, String postfix = '') {
        previous.toString(showMockName, ".${delegate.toString()}$postfix")
    }

    def getSetter(value) {
        delegate = delegate.getSetter(value)
        return this
    }

    def getGetter() {
        delegate = delegate.getGetter()
        return this
    }

}
