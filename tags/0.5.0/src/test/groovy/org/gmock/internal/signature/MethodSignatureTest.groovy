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

class MethodSignatureTest extends GroovyTestCase {

    void testEquals(){
        def signature1 = new MethodSignature("name", ["arg1", 1])
        def signature2 = new MethodSignature("name", ["arg1", 1])
        assertEquals signature1, signature2
    }

    void testNotEqualsToNull() {
        def signature = new MethodSignature("name", [1, 2])
        assert signature != null
    }

    void testNotEqualsDifferentClass() {
        def signature1 = new MethodSignature("String", [1, 2])
        def signature2 = new ConstructorSignature(String, [1, 2])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentMethodName(){
        def signature1 = new MethodSignature("name", ["arg1", 1])
        def signature2 = new MethodSignature("other", ["arg1", 1])
        assertTrue signature1 != signature2
    }

    void testNotEqualsDifferentArguments(){
        def signature1 = new MethodSignature("name", ["arg1", 1])
        def signature2 = new MethodSignature("name", ["arg1", 2])
        assertTrue signature1 != signature2
    }

    void testHashCode(){
        def signature1 = new MethodSignature("name", ["arg1", 1] as Object[])
        def signature2 = new MethodSignature("name", ["arg1", 1] as Object[])
        assertEquals signature1.hashCode(), signature2.hashCode()
    }

    void testToString() {
        def signature = new MethodSignature("someOperate", ["test", 3, true])
        assertEquals 'someOperate("test", 3, true)', signature.toString()
    }

}