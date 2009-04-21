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

import static org.hamcrest.Matchers.*
import static org.gmock.GMock.match

class ParameterSignatureTest extends GroovyTestCase {

    void testEquals(){
        def signature1 = new ParameterSignature(["arg1", 1])
        def signature2 = new ParameterSignature(["arg1", 1])
        assertEquals signature1, signature2
    }

    void testNotEqualsToNull() {
        def signature = new ParameterSignature([1, 2])
        assert signature != null
    }

    void testNotEqualsDifferentClass() {
        def signature1 = new ParameterSignature([1, 2])
        def signature2 = new MethodSignature(new Object(), "name", [1, 2])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentArguments(){
        def signature1 = new ParameterSignature(["arg1", 1])
        def signature2 = new ParameterSignature(["arg1", 2])
        assertTrue signature1 != signature2
    }

    void testHashCode(){
        def signature1 = new ParameterSignature(["arg1", 1])
        def signature2 = new ParameterSignature(["arg1", 1])
        assertEquals signature1.hashCode(), signature2.hashCode()
    }

    void testToString() {
        def signature = new ParameterSignature(["test", 3, true, [1, 2], [3, 4] as Object[], [a: "b", c: "d"], [], [:],
                is(greaterThanOrEqualTo(3)), match { it > 3 }])
        assertEquals '"test", 3, true, [1, 2], [3, 4], ["a":"b", "c":"d"], [], [:], ' +
                     'is a value greater than or equal to <3>, a value matching the closure matcher',
                signature.toString()
    }

    void testHamcrestMatcherMatch() {
        def signature1 = new ParameterSignature([greaterThan(1), "test", is(not(10))])
        def signature2 = new ParameterSignature([2, "test", 11])
        assert signature1.match(signature2)
    }

    void testHamcrestMatcherNotMatch() {
        def signature1 = new ParameterSignature([1, lessThan(1), 3])
        def signature2 = new ParameterSignature([1, 2, 3])
        assert !signature1.match(signature2)
    }

    void testClosureMatcherMatch() {
        def signature1 = new ParameterSignature([1, match { it > 1 }, 3])
        def signature2 = new ParameterSignature([1, 2, 3])
        assert signature1.match(signature2)
    }

    void testClosureMatcherNotMatch() {
        def signature1 = new ParameterSignature([match { it != "test" }, "correct"])
        def signature2 = new ParameterSignature(["test", "correct"])
        assert !signature1.match(signature2)
    }

}
