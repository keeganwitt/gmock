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

class ChainsSignatureTest extends GMockTestCase {

    void testValidate() {
        def mockDelegate = mock()
        mockDelegate.validate()
        def signature = new ChainsSignature(null, mockDelegate)

        play {
            signature.validate()
        }
    }

    void testEquals() {
        def object = new Object()
        def signature = new ChainsSignature(object, object)
        assertFalse signature.equals(object)
    }

    void testEqualsChainsSignature() {
        def mockDelegate = mock()
        mockDelegate.equals(mockDelegate).returns(true)
        def mockSignature = mock {
            it.class.returns(ChainsSignature)
            it.delegate.returns(mockDelegate)
        }
        def signature = new ChainsSignature(null, mockDelegate)

        play {
            assertTrue signature.equals(mockSignature)
        }
    }

    void testMatch() {
        def object = new Object()
        def mockDelegate = mock()
        mockDelegate.match(object).returns(true)
        def signature = new ChainsSignature(null, mockDelegate)

        play {
            assertTrue signature.match(object)
        }
    }

    void testMatchChainsSignature() {
        def mockDelegate = mock()
        mockDelegate.match(mockDelegate).returns(true)
        def mockSignature = mock {
            it.class.returns(ChainsSignature)
            it.delegate.returns(mockDelegate)
        }
        def signature = new ChainsSignature(null, mockDelegate)

        play {
            assertTrue signature.match(mockSignature)
        }
    }

    void testHashCode() {
        def mockDelegate = mock()
        mockDelegate.hashCode().returns(2)
        def signature = new ChainsSignature(null, mockDelegate)

        play {
            assertEquals 2, signature.hashCode()
        }
    }

    void testToStringWithMockName() {
        def mockPrevious = mock()
        mockPrevious.toString(true, '.method2()').returns("'method1().method2()' on 'Mock'")
        def mockDelegate = mock()
        mockDelegate.toString().returns('method2()')
        def signature = new ChainsSignature(mockPrevious, mockDelegate)

        play {
            assertEquals "'method1().method2()' on 'Mock'", signature.toString(true)
        }
    }

    void testToStringWithMockNameAndPostfix() {
        def mockPrevious = mock()
        mockPrevious.toString(true, '.method2().method3()').returns("'method1().method2().method3()' on 'Mock'")
        def mockDelegate1 = mock(), mockDelegate2 = mock()
        mockDelegate1.toString().returns('method2()')
        mockDelegate2.toString().returns('method3()')
        def signature1 = new ChainsSignature(mockPrevious, mockDelegate1)
        def signature2 = new ChainsSignature(signature1, mockDelegate2)

        play {
            assertEquals "'method1().method2().method3()' on 'Mock'", signature2.toString(true)
        }
    }

    void testGetSetter() {
        def previous = new Object()
        def setter = new Object()
        def mockDelegate = mock()
        mockDelegate.getSetter(1).returns(setter)
        def signature = new ChainsSignature(previous, mockDelegate)

        play {
            def newSignature = signature.getSetter(1)
            assert newSignature instanceof ChainsSignature
            assertSame previous, newSignature.previous
            assertSame setter, newSignature.delegate
        }
    }

    void testGetGetter() {
        def previous = new Object()
        def getter = new Object()
        def mockDelegate = mock()
        mockDelegate.getGetter().returns(getter)
        def signature = new ChainsSignature(previous, mockDelegate)

        play {
            def newSignature = signature.getGetter()
            assert newSignature instanceof ChainsSignature
            assertSame previous, newSignature.previous
            assertSame getter, newSignature.delegate
        }
    }

}
