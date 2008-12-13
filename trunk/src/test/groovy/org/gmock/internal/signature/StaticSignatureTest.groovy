package org.gmock.internal.signature

import org.gmock.utils.Loader

class StaticSignatureTest extends GroovyTestCase {

    void testEquals() {
        def signature1 = new StaticSignature(Loader, "put", [1, 2])
        def signature2 = new StaticSignature(Loader, "put", [1, 2])
        assertEquals signature1, signature2
    }

    void testNotEqualsToNull() {
        def signature = new StaticSignature(Loader, "put", [1, 2])
        assert signature != null
    }

    void testNotEqualsDifferentClass() {
        def signature1 = new StaticSignature(Loader, "put", [1, 2])
        def signature2 = new MethodSignature("put", [1, 2])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentMockClass() {
        def signature1 = new StaticSignature(Loader, "put", [1, 2])
        def signature2 = new StaticSignature(Object, "put", [1, 2])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentMethodName() {
        def signature1 = new StaticSignature(Loader, "put", [1, 2])
        def signature2 = new StaticSignature(Loader, "get", [1, 2])
        assert signature1 != signature2
    }

    void testNotEqualsDifferentParameters() {
        def signature1 = new StaticSignature(Loader, "put", [1, 2])
        def signature2 = new StaticSignature(Loader, "put", [3, 4])
        assert signature1 != signature2
    }

    void testHashCode() {
        def signature1 = new StaticSignature(Loader, "put", [1, 2])
        def signature2 = new StaticSignature(Loader, "put", [1, 2])
        assertEquals signature1.hashCode(), signature2.hashCode()
    }

    void testToString() {
        def signature = new StaticSignature(Loader, "put", ["test", 3, true])
        assertEquals 'Loader.put("test", 3, true)', signature.toString()
    }

    void testValidateWithMethodName(){
        def signature = new StaticSignature(Loader, "put", [1, 2])
        signature.validate()
    }

    void testValidateDoesntHaveMethodName(){
        def signature = new StaticSignature(Loader)
        shouldFail(IllegalStateException){
            signature.validate()
        }
    }

}
