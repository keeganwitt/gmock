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

class PropertyGetSignatureTest extends GMockTestCase {

    void testEquals() {
        def object = new Object()
        def signature1 = new PropertyGetSignature(object, "something")
        def signature2 = new PropertyGetSignature(object, "something")
        assertEquals signature1, signature2
    }

    void testNotEqualsToNull() {
        def signature = new PropertyGetSignature(new Object(), "property")
        assert signature != null
    }

    void testNotEqualsDifferentClass() {
        def object = new Object()
        def signature1 = new PropertyGetSignature(object, "property")
        def signature2 = new MethodSignature(object, "property", [])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentObject() {
        def object1 = new Object(), object2 = new Object()
        def signature1 = new PropertyGetSignature(object1, "name")
        def signature2 = new PropertyGetSignature(object2, "name")
        assert signature1 != signature2
    }

    void testNotEqualsDifferentPropertyName() {
        def object = new Object()
        def signature1 = new PropertyGetSignature(object, "name1")
        def signature2 = new PropertyGetSignature(object, "name2")
        assert signature1 != signature2
    }

    void testHashCode() {
        def object = new Object()
        def signature1 = new PropertyGetSignature(object, "hashcode")
        def signature2 = new PropertyGetSignature(object, "hashcode")
        assertEquals signature1.hashCode(), signature2.hashCode()
    }

    void testToString() {
        def object = new Object()
        def signature = new PropertyGetSignature(object, "tostring")
        assertEquals "tostring", signature.toString()
    }

    void testToStringWithMockName() {
        def object = mock()
        object.mockName.chains().toString(true).returns(" on 'Mock'")
        def signature = new PropertyGetSignature(object, "tostring")
        play {
            assertEquals "'tostring' on 'Mock'", signature.toString(true)
        }
    }

    void testToStringWithMockNameAndPostfix() {
        def object = mock()
        object.mockName.chains().toString(true).returns(" on 'Mock'")
        def signature = new PropertyGetSignature(object, "tostring")
        play {
            assertEquals "'tostring.other' on 'Mock'", signature.toString(true, '.other')
        }
    }

}
