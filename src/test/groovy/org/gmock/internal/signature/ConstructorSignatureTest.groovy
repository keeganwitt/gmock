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
        def signature2 = new MethodSignature("Loader", [1, 2])
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
        assertEquals 'new Loader("test", 3, true)', signature.toString()
    }

}
