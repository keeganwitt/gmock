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

class PropertySetSignatureTest extends GroovyTestCase {

    void testEquals() {
        def signature1 = new PropertySetSignature("name", 3)
        def signature2 = new PropertySetSignature("name", 3)
        assertEquals signature1, signature2
    }

    void testNotEqualsToNull() {
        def signature = new PropertySetSignature("property", 1)
        assert signature != null
    }

    void testNotEqualsDifferentClass() {
        def signature1 = new PropertySetSignature("property", 1)
        def signature2 = new MethodSignature("property", [1])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentPropertyName() {
        def signature1 = new PropertySetSignature("name1", 3)
        def signature2 = new PropertySetSignature("name2", 3)
        assert signature1 != signature2
    }

    void testNotEqualsDifferentArgument() {
        def signature1 = new PropertySetSignature("name", 1)
        def signature2 = new PropertySetSignature("name", 2)
        assert signature1 != signature2
    }

    void testHashCode() {
        def signature1 = new PropertySetSignature("name", 5)
        def signature2 = new PropertySetSignature("name", 5)
        assertEquals signature1, signature2
    }

    void testToString() {
        def signature = new PropertySetSignature("name", "tostring")
        assertEquals 'name = "tostring"', signature.toString()
    }

}
