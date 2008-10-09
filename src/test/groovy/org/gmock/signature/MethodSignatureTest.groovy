package org.gmock.signature

class MethodSignatureTest extends GroovyTestCase {

    void testEquals(){
        def signature1 = new MethodSignature("name", ["arg1", 1])
        def signature2 = new MethodSignature("name", ["arg1", 1])
        assertEquals signature1, signature2
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

}