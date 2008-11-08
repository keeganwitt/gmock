package org.gmock.internal.signature

class PropertySetSignatureTest extends GroovyTestCase {

    void testEquals() {
        def signature1 = new PropertySetSignature("name", 3)
        def signature2 = new PropertySetSignature("name", 3)
        assertEquals signature1, signature2
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
