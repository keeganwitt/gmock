package org.gmock.internal.signature

class PropertyGetSignatureTest extends GroovyTestCase {

    void testEquals() {
        def signature1 = new PropertyGetSignature("something")
        def signature2 = new PropertyGetSignature("something")
        assertEquals signature1, signature2
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
