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
                       "  'text' on 'Mock for Object': expected 1, actual 1\n" +
                       "  'trim()' on '': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play { mock.text }
        }
        assertEquals expected, message
    }

    void testChainsMethodCalledIncorrect() {
        def mock = mock()
        mock.text.chains().trim().returns('test')

        def expected = "Unexpected method call 'toUpperCase()' on ''\n" +
                       "  'text' on 'Mock for Object': expected 1, actual 1\n" +
                       "  'trim()' on '': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play { mock.text.toUpperCase() }
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

    // TODO: refactor the chains() method for better error message
    // TODO: set times for the whole chain

}
