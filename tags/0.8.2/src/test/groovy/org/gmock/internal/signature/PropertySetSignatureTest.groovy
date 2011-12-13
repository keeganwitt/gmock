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

import org.gmock.GMockTestCase

class PropertySetSignatureTest extends GMockTestCase {

    void testEquals() {
        def object = new Object()
        def signature1 = new PropertySetSignature(object, "name", 3)
        def signature2 = new PropertySetSignature(object, "name", 3)
        assertEquals signature1, signature2
    }

    void testNotEqualsToNull() {
        def signature = new PropertySetSignature(new Object(), "property", 1)
        assert signature != null
    }

    void testNotEqualsDifferentClass() {
        def object = new Object()
        def signature1 = new PropertySetSignature(object, "property", 1)
        def signature2 = new MethodSignature(object, "property", [1])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentObject() {
        def object1 = new Object(), object2 = new Object()
        def signature1 = new PropertySetSignature(object1, "name", 1)
        def signature2 = new PropertySetSignature(object2, "name", 1)
        assert signature1 != signature2
    }

    void testNotEqualsDifferentPropertyName() {
        def object = new Object()
        def signature1 = new PropertySetSignature(object, "name1", 3)
        def signature2 = new PropertySetSignature(object, "name2", 3)
        assert signature1 != signature2
    }

    void testNotEqualsDifferentArgument() {
        def object = new Object()
        def signature1 = new PropertySetSignature(object, "name", 1)
        def signature2 = new PropertySetSignature(object, "name", 2)
        assert signature1 != signature2
    }

    void testHashCode() {
        def object = new Object()
        def signature1 = new PropertySetSignature(object, "name", 5)
        def signature2 = new PropertySetSignature(object, "name", 5)
        assertEquals signature1, signature2
    }

    void testToString() {
        def object = new Object()
        def signature = new PropertySetSignature(object, "name", "tostring")
        assertEquals 'name = "tostring"', signature.toString()
    }

    void testToStringWithMockName() {
        def object = mock()
        object.mockName.chains().toString(true).returns(" on 'Mock'")
        def signature = new PropertySetSignature(object, "name", "tostring")
        play {
            assertEquals "'name = \"tostring\"' on 'Mock'", signature.toString(true)
        }
    }

}
