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

import org.gmock.utils.Loader

class ConstructorSignatureTest extends GroovyTestCase {

    void testEquals() {
        def signature1 = new ConstructorSignature(Loader, [1, 2])
        def signature2 = new ConstructorSignature(Loader, [1, 2])
        assertEquals signature1, signature2
    }

    void testNotEqualsToNull() {
        def signature = new ConstructorSignature(Loader, [1, 2])
        assert signature != null
    }

    void testNotEqualsDifferentClass() {
        def signature1 = new ConstructorSignature(Loader, [1, 2])
        def signature2 = new MethodSignature(new Object(), "Loader", [1, 2])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentMockClass() {
        def signature1 = new ConstructorSignature(Loader, [1, 2])
        def signature2 = new ConstructorSignature(Object, [1, 2])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentArguments() {
        def signature1 = new ConstructorSignature(Loader, [1, 2])
        def signature2 = new ConstructorSignature(Loader, [3, 4])
        assert signature1 != signature2
    }

    void testHashCode() {
        def signature1 = new ConstructorSignature(Loader, [1, 2])
        def signature2 = new ConstructorSignature(Loader, [1, 2])
        assertEquals signature1.hashCode(), signature2.hashCode()
    }

    void testToString() {
        def signature = new ConstructorSignature(Loader, ["test", 3, true])
        assertEquals "'new Loader(\"test\", 3, true)'", signature.toString()
    }

}
