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

class FunctionalStrictOrderTest extends GMockTestCase {

    void testOneStrictClosureWithOneMock() {
        def mock = mock()
        ordered {
            mock.a().returns(1)
            mock.b().returns(2)
            mock.c().returns(3)
        }
        play {
            assertEquals 1, mock.a()
            assertEquals 2, mock.b()
            assertEquals 3, mock.c()
        }
    }

    void testOneStrictClosureWithOneMockAndUnorderedExpectations() {
        def mock = mock()
        ordered {
            mock.a().returns(1)
            mock.b().returns(2)
            mock.c().returns(3)
        }
        mock.b().returns(4)
        mock.d().returns(5)
        play {
            assertEquals 5, mock.d()
            assertEquals 1, mock.a()
            assertEquals 2, mock.b()
            assertEquals 4, mock.b()
            assertEquals 3, mock.c()
        }
    }

    void testOneStrictClosureWithOneMockAndFailed() {
        def mock = mock()
        ordered {
            mock.a()
            mock.b()
            mock.c()
        }

        def expected = "Unexpected method call 'c()'\n" +
                       "  ordered:\n" +
                       "    'a()': expected 1, actual 1\n" +
                       "    'b()': expected 1, actual 0\n" +
                       "    'c()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                mock.a()
                mock.c()
            }
        }
        assertEquals expected, message
    }

    void testStrictClosureWithTimesExpectations() {
        def mock = mock()
        ordered {
            mock.a().returns(1).times(2)
            mock.a().returns(2).times(1..3)
            mock.b().returns(3).stub()
            mock.c().returns(4).atLeast(2)
            mock.d().returns(5).never()
        }
        play {
            2.times { assertEquals 1, mock.a() }
            2.times { assertEquals 2, mock.a() }
            3.times { assertEquals 4, mock.c() }
        }
    }

    void testStrictClosureShouldReset() {
        def mock = mock()
        ordered {
            mock.a()
            mock.b().stub()
        }
        play {
            mock.a()
            mock.b()
        }

        ordered {
            mock.c()
            mock.d()
        }
        def expected = "Unexpected method call 'b()'\n" +
                       "  ordered:\n" +
                       "    'c()': expected 1, actual 0\n" +
                       "    'd()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                mock.b()
            }
        }
        assertEquals expected, message
    }

    void testStrictClosureShouldVerify() {
        def mock = mock()
        ordered {
            mock.a()
            mock.b()
        }
        def expected = "Expectation not matched on verify:\n" +
                       "  ordered:\n" +
                       "    'a()': expected 1, actual 1\n" +
                       "    'b()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                mock.a()
            }
        }
        assertEquals expected, message
    }

    void testOneStrictClosureWithMultiMocks() {
        def m1 = mock(), m2 = mock(), m3 = mock()
        ordered {
            m1.a().returns(1)
            m2.b().returns(2)
            m3.b().returns(3)
            m1.c().returns(4)
        }
        play {
            assertEquals 1, m1.a()
            assertEquals 2, m2.b()
            assertEquals 3, m3.b()
            assertEquals 4, m1.c()
        }
    }

    void testOneStrictClosureWithMultiMocksAndUnorderedExpectations() {
        def m1 = mock(), m2 = mock(), m3 = mock()
        m1.a().returns(5)
        m1.b().returns(6)
        ordered {
            m1.a().returns(1)
            m2.b().returns(2)
            m3.b().returns(3)
            m1.c().returns(4)
        }
        m3.b().returns(7)
        m1.c().returns(8)
        play {
            assertEquals 1, m1.a()
            assertEquals 6, m1.b()
            assertEquals 2, m2.b()
            assertEquals 5, m1.a()
            assertEquals 3, m3.b()
            assertEquals 7, m3.b()
            assertEquals 4, m1.c()
            assertEquals 8, m1.c()
        }
    }

    void testOneStrictClosureWithMultiMocksAndFailed() {
        def m1 = mock(), m2 = mock(), m3 = mock()
        ordered {
            m1.a()
            m2.b()
            m3.b()
            m1.c()
        }
        def expected = "Unexpected method call 'b()' on 'Mock for Object (3)'\n" +
                       "  ordered:\n" +
                       "    'a()' on 'Mock for Object (1)': expected 1, actual 1\n" +
                       "    'b()' on 'Mock for Object (2)': expected 1, actual 0\n" +
                       "    'b()' on 'Mock for Object (3)': expected 1, actual 0\n" +
                       "    'c()' on 'Mock for Object (1)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                m1.a()
                m3.b()
            }
        }
        assertEquals expected, message
    }

    void testTwoStrictClosuresWithOneMock() {
        def mock = mock()
        def setUp = { ->
            ordered {
                mock.a().returns(1)
                mock.b().returns(2)
                mock.c().returns(3)
            }
            ordered {
                mock.a().returns(4)
                mock.c().returns(5)
                mock.d().returns(6)
            }
        }
        setUp()
        play {
            assertEquals 1, mock.a()
            assertEquals 2, mock.b()
            assertEquals 3, mock.c()
            assertEquals 4, mock.a()
            assertEquals 5, mock.c()
            assertEquals 6, mock.d()
        }

        setUp()
        play {
            assertEquals 1, mock.a()
            assertEquals 4, mock.a()
            assertEquals 5, mock.c()
            assertEquals 2, mock.b()
            assertEquals 6, mock.d()
            assertEquals 3, mock.c()
        }
    }

    void testTwoStrictClosuresWithOneMockAndFailed() {
        def mock = mock()
        ordered {
            mock.a()
            mock.b()
        }
        ordered {
            mock.a()
            mock.c()
        }
        def expected = "Unexpected method call 'c()'\n" +
                       "  ordered:\n" +
                       "    'a()': expected 1, actual 1\n" +
                       "    'b()': expected 1, actual 0\n" +
                       "  ordered:\n" +
                       "    'a()': expected 1, actual 0\n" +
                       "    'c()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                mock.a()
                mock.c()
            }
        }
        assertEquals expected, message
    }

    void testTwoStrictClosuresWithOneMockAndTimesExpectations() {
        def mock = mock()
        ordered {
            mock.a().returns(1).times(2..3)
            mock.b().returns(2).times(2)
        }
        mock.b().returns(3).atLeast(2)
        ordered {
            mock.b().returns(4)
            mock.c().returns(5).atMost(3)
        }
        play {
            assertEquals 4, mock.b()
            2.times { assertEquals 3, mock.b() }
            3.times { assertEquals 1, mock.a() }
            2.times { assertEquals 2, mock.b() }
            assertEquals 5, mock.c()
        }
    }

    void testThreeStrictClosuresWithOneMockAndUnorderedExpectations() {
        def mock = mock()
        mock.a().returns(0)
        ordered {
            mock.a().returns(1)
            mock.b().returns(2)
        }
        mock.b().returns(3)
        ordered {
            mock.a().returns(4)
            mock.c().returns(5)
            mock.d().returns(6)
        }
        ordered {
            mock.b().returns(7)
            mock.d().returns(8)
        }
        mock.e().returns(9)
        play {
            assertEquals 1, mock.a()
            assertEquals 4, mock.a()
            assertEquals 9, mock.e()
            assertEquals 2, mock.b()
            assertEquals 0, mock.a()
            assertEquals 5, mock.c()
            assertEquals 7, mock.b()
            assertEquals 6, mock.d()
            assertEquals 8, mock.d()
            assertEquals 3, mock.b()
        }
    }

    void testMultiStrictClosuresWithMultiMocksAndOneForEach() {
        def m1 = mock(), m2 = mock(), m3 = mock()
        ordered {
            m1.a().returns(1)
            m1.b().returns(2)
        }
        ordered {
            m2.a().returns(3)
            m2.b().returns(4)
        }
        ordered {
            m3.a().returns(5)
            m3.c().returns(6)
        }
        play {
            assertEquals 5, m3.a()
            assertEquals 3, m2.a()
            assertEquals 1, m1.a()
            assertEquals 2, m1.b()
            assertEquals 4, m2.b()
            assertEquals 6, m3.c()
        }
    }

    void testTwoStrictClosuresWithThreeMocks() {
        def m1 = mock(), m2 = mock(), m3 = mock()
        ordered {
            m1.a().returns(1)
            m2.b().returns(2)
            m3.c().returns(3)
        }
        ordered {
            m2.b().returns(4)
            m1.a().returns(5)
            m3.d().returns(6)
        }
        m1.a().returns(7).stub()
        play {
            assertEquals 4, m2.b()
            assertEquals 1, m1.a()
            assertEquals 5, m1.a()
            assertEquals 7, m1.a()
            assertEquals 2, m2.b()
            assertEquals 3, m3.c()
            assertEquals 6, m3.d()
            assertEquals 7, m1.a()
        }
    }

    void testStrictClosuresWithPropertyMocking() {
        def m1 = mock(), m2 = mock()
        ordered {
            m1.a.set(1)
            m2.a.set(2)
        }
        m1.b.set(3)
        ordered {
            m2.a.set(4).atMost(2)
            m1.b.set(5)
            m1.c.returns(6)
        }
        m2.a.returns(7)
        play {
            m1.a = 1
            m1.b = 3
            assertEquals 7, m2.a
            m2.a = 2
            m1.b = 5
            assertEquals 6, m1.c
        }
    }

    void testStrictClosuresWithPropertyMockingAndFailed() {
        def m1 = mock(), m2 = mock()
        ordered {
            m1.a.set(1)
            m2.a.set(2)
            m1.a.returns(0)
        }
        def expected = "Unexpected property getter call 'a' on 'Mock for Object (1)'\n" +
                       "  ordered:\n" +
                       "    'a = 1' on 'Mock for Object (1)': expected 1, actual 1\n" +
                       "    'a = 2' on 'Mock for Object (2)': expected 1, actual 0\n" +
                       "    'a' on 'Mock for Object (1)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                m1.a = 1
                assertEquals 0, m1.a
            }
        }
        assertEquals expected, message
    }

    void testStrictClosureWithStaticMocking() {
        def mockLoader = mock(Loader), mockString = mock(String)
        ordered {
            mockLoader.static.a().returns(1)
            mockLoader.static.b().returns(2)
            mockLoader.static.c().returns(3)
        }
        ordered {
            mockString.static.a().returns(4)
            mockString.static.b().returns(5)
        }
        play {
            assertEquals 1, Loader.a()
            assertEquals 4, String.a()
            assertEquals 2, Loader.b()
            assertEquals 3, Loader.c()
            assertEquals 5, String.b()
        }
    }

    void testStrictClosureWithStaticMockingAndFailed() {
        def mockLoader = mock(Loader)
        ordered {
            mockLoader.static.a()
            mockLoader.static.b()
            mockLoader.static.c()
        }
        def expected = "Unexpected static method call 'Loader.c()'\n" +
                       "  ordered:\n" +
                       "    'Loader.a()': expected 1, actual 1\n" +
                       "    'Loader.b()': expected 1, actual 0\n" +
                       "    'Loader.c()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.a()
                Loader.c()
            }
        }
        assertEquals expected, message
    }

    void testStrictClosureWithStaticPropertyMocking() {
        def mockLoader = mock(Loader), mockString = mock(String)
        ordered {
            mockLoader.static.a.set(1)
            mockString.static.a.set(2)
        }
        ordered {
            mockLoader.static.b.returns(3)
            mockString.static.b.returns(4)
        }
        mockString.static.b.returns(5).stub()
        play {
            assertEquals 5, String.b
            assertEquals 3, Loader.b
            Loader.a = 1
            String.a = 2
            assertEquals 4, String.b
            assertEquals 5, String.b
        }
    }

    void testStrictClosureWithStaticPropertyMockingAndFailed() {
        def mockLoader = mock(Loader)
        ordered {
            mockLoader.static.a.set(1)
            mockLoader.static.b.set(2)
            mockLoader.static.c.set(3)
            mockLoader.static.a.returns(4)
        }
        def expected = "Unexpected static property getter call 'Loader.a'\n" +
                       "  ordered:\n" +
                       "    'Loader.a = 1': expected 1, actual 1\n" +
                       "    'Loader.b = 2': expected 1, actual 1\n" +
                       "    'Loader.c = 3': expected 1, actual 0\n" +
                       "    'Loader.a': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.a = 1
                Loader.b = 2
                assertEquals 4, Loader.a
            }
        }
        assertEquals expected, message
    }

    void testStrictClosureWithConstructorMocking() {
        ordered {
            mock(Loader, constructor(1))
            mock(String, constructor(2))
        }
        ordered {
            mock(String, constructor(3))
            mock(Loader, constructor(4))
        }
        play {
            new String(3)
            new Loader(1)
            new Loader(4)
            new String(2)
        }
    }

    void testStrictClosureWithConstructorMockingAndFailed() {
        ordered {
            mock(Loader, constructor(1))
            mock(Loader, constructor(2))
            mock(Loader, constructor(3))
        }
        def expected = "Unexpected constructor call 'new Loader(3)'\n" +
                       "  ordered:\n" +
                       "    'new Loader(1)': expected 1, actual 1\n" +
                       "    'new Loader(2)': expected 1, actual 0\n" +
                       "    'new Loader(3)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                new Loader(1)
                new Loader(3)
            }
        }
        assertEquals expected, message
    }

    void testStrictOrderingWithAllKindOfMocking() {
        ordered {
            mock(Loader).static.init()
            mock(Loader, constructor()) {
                load(1).returns(2)
                name.set('test')
                name.returns('correct')
            }
            mock(Loader).static.finalize()
        }
        play {
            Loader.init()
            def loader = new Loader()
            assertEquals 2, loader.load(1)
            loader.name = 'test'
            assertEquals 'correct', loader.name
            Loader.finalize()
        }
    }

    void testStrictOrderingWithAllKindOfMockingAndFailed() {
        ordered {
            mock(Loader).static.init()
            mock(Loader, constructor()) {
                load(1).returns(2)
                name.set('test')
                name.returns('correct')
            }
            mock(Loader).static.finalize()
        }
        def expected = "Unexpected static method call 'Loader.finalize()'\n" +
                       "  ordered:\n" +
                       "    'Loader.init()': expected 1, actual 1\n" +
                       "    'new Loader()': expected 1, actual 1\n" +
                       "    'load(1)' on 'Mock for Loader (2)': expected 1, actual 1\n" +
                       "    'name = \"test\"' on 'Mock for Loader (2)': expected 1, actual 1\n" +
                       "    'name' on 'Mock for Loader (2)': expected 1, actual 0\n" +
                       "    'Loader.finalize()': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                Loader.init()
                def loader = new Loader()
                assertEquals 2, loader.load(1)
                loader.name = 'test'
                Loader.finalize()
            }
        }
        assertEquals expected, message
    }

    void testStrictClosureShouldValidate1() {
        def mock = mock()
        ordered {
            mock.a
        }
        def expected = "Missing property expectation for 'a' on 'Mock for Object'"
        def message = shouldFail(IllegalStateException) {
            play {}
        }
        assertEquals expected, message
    }

    void testStrictClosureShouldValidate2() {
        def mock = mock()
        ordered {
            mock.static.a
        }
        def expected = "Missing property expectation for 'Object.a'"
        def message = shouldFail(IllegalStateException) {
            play {}
        }
        assertEquals expected, message
    }

    void testStrictClosureShouldValidate3() {
        def mock = mock()
        ordered {
            mock.static
        }
        def expected = "Missing static expectation for Object"
        def message = shouldFail(IllegalStateException) {
            play {}
        }
        assertEquals expected, message
    }

    void testStrictClosureInsideExpectationClosure() {
        def mock = mock {
            ordered {
                a().returns(1)
                b().returns(2)
                c().returns(3)
                setUp().returns(4)
            }
        }
        play {
            assertEquals 1, mock.a()
            assertEquals 2, mock.b()
            assertEquals 3, mock.c()
            assertEquals 4, mock.setUp()
        }
    }

    void testStrictClosureInsideStaticClosure() {
        mock(Loader).static {
            ordered {
                a().returns(1)
                b().returns(2)
                setUp().returns(3)
            }
        }
        play {
            assertEquals 1, Loader.a()
            assertEquals 2, Loader.b()
            assertEquals 3, Loader.setUp()
        }
    }

    void testStrictClosureInsideStaticClosureWhichIsInsideMockClosure() {
        mock(Loader) {
            'static' {
                ordered {
                    a().returns(1)
                    b().returns(2)
                    setUp().returns(3)
                }
            }
        }
        play {
            assertEquals 1, Loader.a()
            assertEquals 2, Loader.b()
            assertEquals 3, Loader.setUp()
        }
    }

    void testStrictClosureInsideWithClosure() {
        def mock = mock()
        with(mock) {
            ordered {
                a().returns(1)
                setUp().returns(2)
            }
        }
        play {
            assertEquals 1, mock.a()
            assertEquals 2, mock.setUp()
        }
    }

    void testStrictClosureInsideStaticClosureWhichIsInsideWithClosure() {
        def mock = mock(Loader)
        with(mock) {
            'static' {
                ordered {
                    a().returns(1)
                    setUp().returns(2)
                }
            }
        }
        play {
            assertEquals 1, Loader.a()
            assertEquals 2, Loader.setUp()
        }
    }

    void testClosureMatcherInsideStrictClosureWhichIsInsideExpectationClosure() {
        def mock = mock {
            ordered {
                a(match { it == 1 }).returns(2)
                it.match(match { it == 3 }).returns(4)
            }
        }
        play {
            assertEquals 2, mock.a(1)
            assertEquals 4, mock.match(3)
        }
    }

    void testClosureMatcherInsideStrictClosureWhichIsInsideStaticClosure() {
        mock(Loader).static {
            ordered {
                a(match { it == 1 }).returns(2)
                it.match(match { it == 3 }).returns(4)
            }
        }
        play {
            assertEquals 2, Loader.a(1)
            assertEquals 4, Loader.match(3)
        }
    }

    void testMultipleStrictClosuresInsideExpectationClosure() {
        def mock = mock {
            ordered {
                a().returns(1)
                b().returns(2)
            }
            ordered {
                a().returns(3)
                b().returns(4)
            }
        }
        play {
            assertEquals 1, mock.a()
            assertEquals 3, mock.a()
            assertEquals 2, mock.b()
            assertEquals 4, mock.b()
        }
    }

    void testMultipleStrictClosuresInsideStaticClosure() {
        mock(Loader).static {
            ordered {
                a().returns(1)
                b().returns(2)
            }
            ordered {
                a().returns(3)
                b().returns(4)
            }
        }
        play {
            assertEquals 1, Loader.a()
            assertEquals 3, Loader.a()
            assertEquals 2, Loader.b()
            assertEquals 4, Loader.b()
        }
    }

    void testMultipleStrictClosuresInsideStaticClosureWhichIsInsideExpectationClosure() {
        def mock = mock(Loader) {
            'static' {
                ordered {
                    a().returns(1)
                    b().returns(2)
                }
            }
            ordered {
                a().returns(3)
                b().returns(4)
            }
        }
        play {
            assertEquals 1, Loader.a()
            assertEquals 2, Loader.b()
            assertEquals 3, mock.a()
            assertEquals 4, mock.b()
        }
    }

    void testCannotNestStrictClosures() {
        ordered {
            shouldFail(IllegalStateException) {
                ordered {}
            }
        }

        mock {
            ordered {
                shouldFail(IllegalStateException) {
                    ordered {}
                }
            }
        }
    }

    void testStrictClosureCannotBeInsidePlayClosure() {
        play {
            shouldFail(IllegalStateException) {
                ordered {}
            }
        }
    }

    void testPlayClosureCannotBeInsideStrictClosure() {
        ordered {
            shouldFail(IllegalStateException) {
                play {}
            }
        }
    }

    void testTimesShouldBeCheckedForStrictOrdering() {
        def mock = mock()
        ordered {
            mock.a().atLeast(2)
            shouldFail(IllegalStateException) {
                mock.a().times(2)
            }
        }
    }

    void testNonfixedTimesForStrictOrderingButNotTheSameMockShouldBeFine() {
        def m1 = mock(), m2 = mock()
        ordered {
            m1.a().atLeast(2)
            m2.a().atMost(2)
        }
    }

    void testNonfixedTimesInDifferentStrictClosuresShouldBeFine() {
        def mock = mock()
        ordered {
            mock.a().times(2)
            mock.a().times(1..2)
        }
        ordered {
            mock.a()
            mock.a().atMostOnce()
        }
        mock.a().times(3)
        mock.a().stub()
        play {
            9.times { mock.a() }
        }
    }

    void testNonfixedTimesNotHeelInStrictClosureShouldBeFine() {
        def mock = mock()
        ordered {
            mock.a().stub()
            mock.b()
            mock.a().atLeastOnce()
        }
        play {
            mock.a()
            mock.b()
            mock.a()
        }
    }

    void testTimesShouldBeCheckedForStrictOrderingWithStaticMocking() {
        ordered {
            mock(Loader).static.a().stub()
            shouldFail(IllegalStateException) {
                mock(Loader).static.a().times(1..2)
            }
        }
    }

    void testNoDefaultBehaviorsIfExpectationsAreSetInStrictClosure() {
        def mock = mock {
            ordered {
                it.toString().returns('test')
                it.hashCode().returns(1)
                it.equals(match { true }).returns(false)
            }
        }
        play {
            assertEquals 'test', mock.toString()
            assertEquals 1, mock.hashCode()
            assertFalse mock == new Object()
            shouldFail { mock.toString() }
            shouldFail { mock.hashCode() }
            shouldFail { mock.equals(mock) }
        }
    }

    void testLooseClosureInsideStrictClosure() {
        def mockLock = mock(), mockOther = mock()
        ordered {
            mockLock.lock().returns(true)
            unordered {
                mockLock.a().returns(1)
                mockOther.b().returns(2).times(1..2)
                mockOther.c().returns(3).stub()
            }
            mockLock.unlock().returns(true)
        }
        play {
            assertTrue mockLock.lock()
            assertEquals 3, mockOther.c()
            assertEquals 1, mockLock.a()
            assertEquals 2, mockOther.b()
            assertEquals 3, mockOther.c()
            assertEquals 3, mockOther.c()
            assertTrue mockLock.unlock()
        }
    }

    void testLooseClosureInsideStrictClosureAndFailed() {
        def mockLock = mock(), mockOther = mock()
        ordered {
            mockLock.lock().returns(true)
            unordered {
                mockOther.b().returns(2).times(2..3)
                mockOther.c().returns(3).stub()
            }
            mockLock.unlock().returns(true)
        }
        def expected = "Unexpected method call 'unlock()' on 'Mock for Object (1)'\n" +
                       "  ordered:\n" +
                       "    'lock()' on 'Mock for Object (1)': expected 1, actual 1\n" +
                       "    unordered:\n" +
                       "      'b()' on 'Mock for Object (2)': expected 2..3, actual 1\n" +
                       "      'c()' on 'Mock for Object (2)': expected any times, actual 3\n" +
                       "    'unlock()' on 'Mock for Object (1)': expected 1, actual 0"
        def message = shouldFail(AssertionFailedError) {
            play {
                assertTrue mockLock.lock()
                assertEquals 3, mockOther.c()
                assertEquals 2, mockOther.b()
                assertEquals 3, mockOther.c()
                assertEquals 3, mockOther.c()
                mockLock.unlock()
            }
        }
        assertEquals expected, message
    }

    void testLooseClosureInsideStrictClosureShouldValidate() {
        def mock = mock()
        ordered {
            mock.a()
            unordered {
                mock.b()
                mock.c
            }
            mock.d()
        }
        def expected = "Missing property expectation for 'c' on 'Mock for Object'"
        def message = shouldFail(IllegalStateException) {
            play {}
        }
        assertEquals expected, message
    }

    void testLooseClosureInsideStrictClosureShouldVerify() {
        def mock = mock()
        ordered {
            mock.a()
            unordered {
                mock.b()
                mock.c()
            }
        }
        def expected = "Expectation not matched on verify:\n" +
                       "  ordered:\n" +
                       "    'a()': expected 1, actual 1\n" +
                       "    unordered:\n" +
                       "      'b()': expected 1, actual 0\n" +
                       "      'c()': expected 1, actual 1"
        def message = shouldFail(AssertionFailedError) {
            play {
                mock.a()
                mock.c()
            }
        }
        assertEquals expected, message
    }

    void testNonfixedTimesAfterLooseClosureShouldBeFine() {
        def mock = mock()
        ordered {
            mock.a().returns(1).atLeast(1)
            unordered {
                mock.a().returns(2)
                mock.b().returns(3).stub()
                mock.a().returns(4).times(1..2)
            }
            mock.a().returns(5).times(1..2)
        }
        play {
            assertEquals 1, mock.a()
            assertEquals 3, mock.b()
            assertEquals 2, mock.a()
            2.times { assertEquals 4, mock.a() }
            assertEquals 5, mock.a()
        }
    }

    void testNonfixedTimesShouldBeCheckedInsideLooseClosure() {
        def mock = mock()
        ordered {
            unordered {
                mock.a().times(1..2)
                shouldFail {
                    mock.a()
                }
            }
        }
    }

    void testNoDefaultBehaviorsIfExpectationsAreSetInLooseClosure() {
        def mock = mock()
        ordered {
            unordered {
                mock.toString().returns('test')
                mock.hashCode().returns(2)
                mock.equals(match { true }).returns(false)
            }
        }
        play {
            assertEquals "test", mock.toString()
            assertEquals 2, mock.hashCode()
            assertFalse mock == new Object()
            shouldFail { mock.toString() }
            shouldFail { mock.hashCode() }
            shouldFail { mock.equals(mock) }
        }
    }

    void testLooseClosureCanOnlyBeInsideStrictClosure() {
        shouldFail(IllegalStateException) {
            unordered {}
        }
    }

    void testLooseClosuresCannotBeNested() {
        ordered {
            unordered {
                shouldFail(IllegalStateException) {
                    unordered {}
                }
            }
        }
    }

    void testStrictClosureCannotBeInsideLooseClosure() {
        ordered {
            unordered {
                shouldFail(IllegalStateException) {
                    ordered {}
                }
            }
        }
    }

    void testLooseClosureInsideExpectationClosure() {
        def mock = mock {
            ordered {
                a().returns(1)
                unordered {
                    b().returns(2)
                    c().returns(3)
                }
                d().returns(4)
            }
        }
        play {
            assertEquals 1, mock.a()
            assertEquals 3, mock.c()
            assertEquals 2, mock.b()
            assertEquals 4, mock.d()
        }
    }

    void testLooseClosureInsideStaticClosure() {
        mock(Loader).static {
            ordered {
                a().returns(1)
                unordered {
                    b().returns(2)
                    c().returns(3)
                }
                d().returns(4)
            }
        }
        play {
            assertEquals 1, Loader.a()
            assertEquals 3, Loader.c()
            assertEquals 2, Loader.b()
            assertEquals 4, Loader.d()
        }
    }

    void testErrorMessageWithMultipleMocksAndMultipleStrictClosures() {
        def m1 = mock(), m2 = mock(), m3 = mock()
        ordered {
            m1.a().times(2)
            m1.a().times(1..2)
            unordered {
                m2.b()
                m3.c()
                m2.b().times(2..3)
            }
            m3.c().atMost(2)
        }
        m1.a()
        ordered { unordered {}; unordered {} }
        ordered {
            m1.a()
            m2.b()
            m1.a().times(2)
        }
        m2.b().never()
        m1.a().atLeast(2)

        def expected = "Unexpected method call 'b()' on 'Mock for Object (2)'\n" +
                       "  ordered:\n" +
                       "    'a()' on 'Mock for Object (1)': expected 3..4, actual 0\n" +
                       "    unordered:\n" +
                       "      'b()' on 'Mock for Object (2)': expected 3..4, actual 0\n" +
                       "      'c()' on 'Mock for Object (3)': expected 1, actual 0\n" +
                       "    'c()' on 'Mock for Object (3)': expected at most 2, actual 0\n" +
                       "  ordered:\n" +
                       "    'a()' on 'Mock for Object (1)': expected 1, actual 0\n" +
                       "    'b()' on 'Mock for Object (2)': expected 1, actual 0\n" +
                       "    'a()' on 'Mock for Object (1)': expected 2, actual 0\n" +
                       "  unordered:\n" +
                       "    'a()' on 'Mock for Object (1)': expected at least 3, actual 0\n" +
                       "    'b()' on 'Mock for Object (2)': expected never, actual 0 (+1)"
        def message = shouldFail(AssertionFailedError) {
            play {
                m2.b()
            }
        }
        assertEquals expected, message
    }

    void testMultipleReturnsAndRaisesOnOneExpectation() {
        def mock = mock {
            ordered {
                fun().returns(1).raises(RuntimeException).returns(2).times(2)
                unordered {
                    foo().returns(1).returns(2)
                }
            }
        }
        play {
            assertEquals 1, mock.fun()
            shouldFail(RuntimeException) { mock.fun() }
            2.times {
                assertEquals 2, mock.fun()
            }
            assertEquals 1, mock.foo()
            assertEquals 2, mock.foo()
        }
    }

}
