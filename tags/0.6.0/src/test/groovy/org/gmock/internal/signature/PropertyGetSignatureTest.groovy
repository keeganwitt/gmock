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

class PropertyGetSignatureTest extends GroovyTestCase {

    void testEquals() {
        def signature1 = new PropertyGetSignature("something")
        def signature2 = new PropertyGetSignature("something")
        assertEquals signature1, signature2
    }

    void testNotEqualsToNull() {
        def signature = new PropertyGetSignature("property")
        assert signature != null
    }

    void testNotEqualsDifferentClass() {
        def signature1 = new PropertyGetSignature("property")
        def signature2 = new MethodSignature("property", [])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentPropertyName() {
        def signature1 = new PropertyGetSignature("name1")
        def signature2 = new PropertyGetSignature("name2")
        assert signature1 != signature2
    }

    void testHashCode() {
        def signature1 = new PropertyGetSignature("hashcode")
        def signature2 = new PropertyGetSignature("hashcode")
        assertEquals signature1.hashCode(), signature2.hashCode()
    }

    void testToString() {
        def signature = new PropertyGetSignature("tostring")
        assertEquals "tostring", signature.toString()
    }

}
