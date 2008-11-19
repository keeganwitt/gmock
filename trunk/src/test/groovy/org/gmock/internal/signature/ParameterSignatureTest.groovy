package org.gmock.internal.signature

import static org.hamcrest.Matchers.*

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
        def signature2 = new MethodSignature("name", [1, 2])
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
                is(greaterThanOrEqualTo(3))])
        assertEquals '"test", 3, true, [1, 2], [3, 4], ["a":"b", "c":"d"], [], [:], ' +
                     'is a value greater than or equal to <3>',
                signature.toString()
    }

    void testHamcrestMatcherEquals() {
        def signature1 = new ParameterSignature([greaterThan(1), "test", is(not(10))])
        def signature2 = new ParameterSignature([2, "test", 11])
        assertEquals signature1, signature2
        assertEquals signature2, signature1
    }

}
