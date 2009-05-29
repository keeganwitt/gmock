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

class MethodSignatureTest extends GMockTestCase {

    void testEquals(){
        def object = new Object()
        def signature1 = new MethodSignature(object, "name", ["arg1", 1])
        def signature2 = new MethodSignature(object, "name", ["arg1", 1])
        assertEquals signature1, signature2
    }

    void testNotEqualsToNull() {
        def signature = new MethodSignature(new Object(), "name", [1, 2])
        assert signature != null
    }

    void testNotEqualsDifferentClass() {
        def signature1 = new MethodSignature(new Object(), "String", [1, 2])
        def signature2 = new ConstructorSignature(String, [1, 2])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentObject() {
        def object1 = new Object(), object2 = new Object()
        def signature1 = new MethodSignature(object1, "String", [1, 2])
        def signature2 = new MethodSignature(object2, "String", [1, 2])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentMethodName(){
        def object = new Object()
        def signature1 = new MethodSignature(object, "name", ["arg1", 1])
        def signature2 = new MethodSignature(object, "other", ["arg1", 1])
        assertTrue signature1 != signature2
    }

    void testNotEqualsDifferentArguments(){
        def object = new Object()
        def signature1 = new MethodSignature(object, "name", ["arg1", 1])
        def signature2 = new MethodSignature(object, "name", ["arg1", 2])
        assertTrue signature1 != signature2
    }

    void testHashCode(){
        def object = new Object()
        def signature1 = new MethodSignature(object, "name", ["arg1", 1] as Object[])
        def signature2 = new MethodSignature(object, "name", ["arg1", 1] as Object[])
        assertEquals signature1.hashCode(), signature2.hashCode()
    }

    void testToString() {
        def object = new Object()
        def signature = new MethodSignature(object, "someOperate", ["test", 3, true])
        assertEquals 'someOperate("test", 3, true)', signature.toString()
    }

    void testToStringWithMockName() {
        def object = mock()
        object.mockName.chains().toString(true).returns(" on 'Mock'")
        def signature = new MethodSignature(object, "someOperate", ["test", 3, true])
        play {
            assertEquals "'someOperate(\"test\", 3, true)' on 'Mock'", signature.toString(true)
        }
    }

    void testToStringWithMockNameAndPostfix() {
        def object = mock()
        object.mockName.chains().toString(true).returns(" on 'Mock'")
        def signature = new MethodSignature(object, "someOperate", ["test", 3, true])
        play {
            assertEquals "'someOperate(\"test\", 3, true).other()' on 'Mock'", signature.toString(true, '.other()')
        }
    }

}
