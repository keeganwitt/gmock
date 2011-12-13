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
package org.gmock

import junit.framework.AssertionFailedError
import org.gmock.utils.Loader
import org.gmock.utils.ChainA
import org.gmock.utils.JavaTestHelper

class FunctionalChainsMethodsTest extends GMockTestCase {

    void testBasic() {
        def google = mock('http://www.google.com')
        google.toURL().chains().text.returns('google')
        play {
            assertEquals 'google', 'http://www.google.com'.toURL().text
        }
    }

    void testNoChainsMethodAfterAnyResultSetOnMethodMocking() {
        def mock = mock()
        shouldFailWithMissingChainsMethod {
            mock.dump().returns('test').chains().trim()
        }
        shouldFailWithMissingChainsMethod {
            mock.dump().raises(RuntimeException).chains().trim()
        }
    }

    void testProperty() {
        def mock = mock()
        mock.text.chains().trim().returns('test')
        play {
            assertEquals 'test', mock.text.trim()
        }
    }

    void testNoChainsMethodAfterAnyResultSetOnPropertyMocking() {
        def mock = mock()
        shouldFailWithMissingChainsMethod {
            mock.text.set('test').chains().trim()
        }
        shouldFailWithMissingChainsMethod {
            mock.text.returns('test').chains().trim()
        }
        shouldFailWithMissingChainsMethod {
            mock.text.raises(RuntimeException).chains().trim()
        }
    }

    private shouldFailWithMissingChainsMethod(Closure closure) {
        try {
            closure()
        } catch (MissingMethodException e) {
            if (e.method == 'chains') return
        }
        fail('Should fail with missing chains() method.')
    }

    void testChainsMethodNotCalled() {
        def mock = mock()
        mock.text.chains().trim().returns('test')

        def expected = "Expectation not matched on verify:\n" +
                       "  'text.trim()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play { mock.text }
        }
        assertEquals expected, message
    }

    void testChainsMethodCalledIncorrect() {
        def mock = mock()
        mock.text.chains().trim().returns('test')

        def expected = "Unexpected method call 'text.toUpperCase()'\n" +
                       "  'text.trim()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play { mock.text.toUpperCase() }
        }
        assertEquals expected, message
    }

    void testMultipleChainsMethodNotCalled() {
        def mock = mock()
        mock.a.chains().b.chains().c.chains().d()

        def expected = "Expectation not matched on verify:\n" +
                       "  'a.b.c.d()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play { mock.a.b }
        }
        assertEquals expected, message
    }

    void testMultipleChainsMethodCalledIncorrect() {
        def mock = mock()
        mock.a().chains().b.chains().c().chains().d.returns(1)

        def expected = "Unexpected property getter call 'a().x'\n" +
                       "  'a().b.c().d': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play { mock.a().x.c().d }
        }
        assertEquals expected, message
    }

    void testMultipleMocksWithChainsMockingNotVerified() {
        def m1 = mock(), m2 = mock()
        m1.a.chains().b.returns(1)
        m2.c().chains().d().chains().e()
        m1.f()

        def expected = "Expectation not matched on verify:\n" +
                       "  'a.b' on 'Mock for Object (1)': expected 1, actual 0\n" +
                       "  'c().d().e()' on 'Mock for Object (2)': expected 1, actual 1\n" +
                       "  'f()' on 'Mock for Object (1)': expected 1, actual 1"
        def message = shouldFail(AssertionFailedError) {
            play {
                m1.f()
                m2.c().d().e()
                m1.a
            }
        }
        assertEquals expected, message
    }

    void testMultipleMocksWithChainsMockingUnexpected() {
        def m1 = mock(), m2 = mock(), m3 = mock()
        m1.a.chains().b.chains().c()
        m2.d()
        m3.e().chains().f.chains().g()

        def expected = "Unexpected property getter call 'a.x' on 'Mock for Object (1)'\n" +
                       "  'a.b.c()' on 'Mock for Object (1)': expected 1, actual 0\n" +
                       "  'd()' on 'Mock for Object (2)': expected 1, actual 1\n" +
                       "  'e().f.g()' on 'Mock for Object (3)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                m2.d()
                m1.a.x.c()
            }
        }
        assertEquals expected, message
    }

    void testStaticMockingWithChainsMockingUnexpected() {
        mock(Loader).static.a.chains().b.chains().c.returns(1)

        def expected = "Unexpected property getter call 'Loader.a.x'\n" +
                       "  'Loader.a.b.c': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.a.x.c
            }
        }
        assertEquals expected, message
    }

    void testMultipleChainsMethods() {
        def mock = mock()
        mock.text.chains().trim().chains().toLowerCase().returns('test')
        play {
            assertEquals 'test', mock.text.trim().toLowerCase()
        }
    }

    void testChainsMethodForStaticMocking() {
        mock(Loader).static {
            one().chains().toString().returns('0')
            name.chains().toUpperCase().returns('TEST')
        }
        play {
            assertEquals '0', Loader.one().toString()
            assertEquals 'TEST', Loader.name.toUpperCase()
        }
    }

    void testChainsMethodInOrderedClosure() {
        def test = mock()
        def google = mock('http://www.google.com')
        ordered {
            test.text.chains().trim().returns('test')
            google.toURL().chains().text.returns('goolge')
        }
        play {
            assertEquals 'test', test.text.trim()
            assertEquals 'goolge', 'http://www.google.com'.toURL().text
        }
    }

    void testChainsMethodInJava() {
        def a = mock(ChainA)
        2.times { a.getB().chains().c.chains().text.returns('test') }
        play {
            assertEquals 'test', JavaTestHelper.chainedCallsOn(a)
            assertEquals 'test', a.b.c.text
        }
    }

    void testChainsMethodInJavaShouldChooseTheCorrectReturnType() {
        def a = mock(ChainA)
        2.times { a.methodA(1).chains().methodB(2).chains().text.returns('test') }
        play {
            assertEquals 'test', JavaTestHelper.chainedMethodsOn(a)
            assertEquals 'test', a.methodA(1).methodB(2).text
        }
    }

    void testChainsMockingSetPropertyInRecordState() {
        def mock = mock()
        def expected = "Cannot use property setter in record mode. Are you trying to mock a setter? Use 'b.set(1)' instead."
        def message = shouldFail(MissingPropertyException) {
            mock.a.chains().b = 1
        }
        assertEquals expected, message
    }
    
    void testChainsMissingExpectation() {
        def mock = mock()
        mock.a().chains()
        shouldFail(IllegalStateException) {
            play {}
        }
        
        play {}
        
        mock.a().chains().b().chains()
        shouldFail(IllegalStateException) {
            play {}
        }
    }
    
    void testChainsSetTimes() {
        def mock = mock()
        mock.a().chains().b().returns(1).times(2)
        mock.a().chains().b().chains().c().returns(2).times(2).times(3)
        play {
            2.times {
                assert 1 == mock.a().b()
            }
            5.times {
                assert 2 == mock.a().b().c()
            }
        }
    }

}
