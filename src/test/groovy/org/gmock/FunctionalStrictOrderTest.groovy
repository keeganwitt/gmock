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

import org.gmock.utils.Loader

class FunctionalStrictOrderTest extends GMockTestCase {

    void testOneStrictClosureWithOneMock() {
        def mock = mock()
        strict {
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
        strict {
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
        strict {
            mock.a()
            mock.b()
            mock.c()
        }
        shouldFail { // TODO: check the message
            play {
                mock.a()
                mock.c()
                mock.b()
            }
        }
    }

    void testStrictClosureWithTimesExpectations() {
        def mock = mock()
        strict {
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
        strict {
            mock.a()
            mock.b().stub()
        }
        play {
            mock.a()
            mock.b()
        }

        strict {
            mock.c()
            mock.d()
        }
        shouldFail { // TODO: check the message
            play {
                mock.b()
                mock.c()
                mock.d()
            }
        }
    }

    void testStrictClosureShouldVerify() {
        def mock = mock()
        strict {
            mock.a()
            mock.b()
        }
        shouldFail { // TODO: check the message
            play {
                mock.a()
            }
        }
    }

    void testOneStrictClosureWithMultiMocks() {
        def m1 = mock(), m2 = mock(), m3 = mock()
        strict {
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
        strict {
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
        strict {
            m1.a()
            m2.b()
            m3.b()
            m1.c()
        }
        shouldFail { // TODO: check the message
            play {
                m1.a()
                m3.b()
                m2.b()
                m1.c()
            }
        }
    }

    void testTwoStrictClosuresWithOneMock() {
        def mock = mock()
        def setUp = { ->
            strict {
                mock.a().returns(1)
                mock.b().returns(2)
                mock.c().returns(3)
            }
            strict {
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
        strict {
            mock.a()
            mock.b()
        }
        strict {
            mock.a()
            mock.c()
        }
        shouldFail { // TODO: check the message
            play {
                mock.a()
                mock.c()
                mock.a()
                mock.b()
            }
        }
    }

    void testTwoStrictClosuresWithOneMockAndTimesExpectations() {
        def mock = mock()
        strict {
            mock.a().returns(1).times(2..3)
            mock.b().returns(2).times(2)
        }
        mock.b().returns(3).atLeast(2)
        strict {
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
        strict {
            mock.a().returns(1)
            mock.b().returns(2)
        }
        mock.b().returns(3)
        strict {
            mock.a().returns(4)
            mock.c().returns(5)
            mock.d().returns(6)
        }
        strict {
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
        strict {
            m1.a().returns(1)
            m1.b().returns(2)
        }
        strict {
            m2.a().returns(3)
            m2.b().returns(4)
        }
        strict {
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
        strict {
            m1.a().returns(1)
            m2.b().returns(2)
            m3.c().returns(3)
        }
        strict {
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
        strict {
            m1.a.set(1)
            m2.a.set(2)
        }
        m1.b.set(3)
        strict {
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
        strict {
            m1.a.set(1)
            m2.a.set(2)
            m1.a.returns(0)
        }
        shouldFail { // TODO: check the message
            play {
                m1.a = 1
                assertEquals 0, m1.a
                m2.a = 2
            }
        }
    }

    void testStrictClosureWithStaticMocking() {
        def mockLoader = mock(Loader), mockString = mock(String)
        strict {
            mockLoader.static.a().returns(1)
            mockLoader.static.b().returns(2)
            mockLoader.static.c().returns(3)
        }
        strict {
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
        strict {
            mockLoader.static.a()
            mockLoader.static.b()
            mockLoader.static.c()
        }
        shouldFail { // TODO: check the message
            play {
                Loader.a()
                Loader.c()
                Loader.b()
            }
        }
    }

    void testStrictClosureWithStaticPropertyMocking() {
        def mockLoader = mock(Loader), mockString = mock(String)
        strict {
            mockLoader.static.a.set(1)
            mockString.static.a.set(2)
        }
        strict {
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
        strict {
            mockLoader.static.a.set(1)
            mockLoader.static.b.set(2)
            mockLoader.static.c.set(3)
            mockLoader.static.a.returns(4)
        }
        shouldFail { // TODO: check the message
            play {
                Loader.a = 1
                Loader.b = 2
                assertEquals 4, Loader.a
                Loader.c = 3
            }
        }
    }

    void testStrictClosureWithConstructorMocking() {
        strict {
            mock(Loader, constructor(1))
            mock(String, constructor(2))
        }
        strict {
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
        strict {
            mock(Loader, constructor(1))
            mock(Loader, constructor(2))
            mock(Loader, constructor(3))
        }
        shouldFail { // TODO: check the message
            play {
                new Loader(1)
                new Loader(3)
                new Loader(2)
            }
        }
    }

    void testStrictOrderingWithAllKindOfMocking() {
        strict {
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
        strict {
            mock(Loader).static.init()
            mock(Loader, constructor()) {
                load(1).returns(2)
                name.set('test')
                name.returns('correct')
            }
            mock(Loader).static.finalize()
        }
        shouldFail { // TODO: check the message
            play {
                Loader.init()
                def loader = new Loader()
                assertEquals 2, loader.load(1)
                loader.name = 'test'
                Loader.finalize()
                assertEquals 'correct', loader.name
            }
        }
    }

    void testStrictClosureShouldValidate1() {
        def mock = mock()
        strict {
            mock.a
        }
        def expected = "Missing property expectation for 'a'"
        def message = shouldFail(IllegalStateException) {
            play {}
        }
        assertEquals expected, message
    }

    void testStrictClosureShouldValidate2() {
        def mock = mock()
        strict {
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
        strict {
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
            strict {
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
            strict {
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
                strict {
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
            strict {
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
                strict {
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
            strict {
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
            strict {
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
            strict {
                a().returns(1)
                b().returns(2)
            }
            strict {
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
            strict {
                a().returns(1)
                b().returns(2)
            }
            strict {
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
                strict {
                    a().returns(1)
                    b().returns(2)
                }
            }
            strict {
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
        strict {
            shouldFail(IllegalStateException) {
                strict {}
            }
        }

        mock {
            strict {
                shouldFail(IllegalStateException) {
                    strict {}
                }
            }
        }
    }

    void testStrictClosureCannotBeInsidePlayClosure() {
        play {
            shouldFail(IllegalStateException) {
                strict {}
            }
        }
    }

    void testPlayClosureCannotBeInsideStrictClosure() {
        strict {
            shouldFail(IllegalStateException) {
                play {}
            }
        }
    }

    void testTimesShouldBeCheckedForStrictOrdering() {
        def mock = mock()
        strict {
            mock.a().atLeast(2)
            shouldFail(IllegalStateException) {
                mock.a().times(2)
            }
        }
    }

    void testNonfixedTimesForStrictOrderingButNotTheSameMockShouldBeFine() {
        def m1 = mock(), m2 = mock()
        strict {
            m1.a().atLeast(2)
            m2.a().atMost(2)
        }
    }

    void testNonfixedTimesInDifferentStrictClosuresShouldBeFine() {
        def mock = mock()
        strict {
            mock.a().times(2)
            mock.a().times(1..2)
        }
        strict {
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
        strict {
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
        strict {
            mock(Loader).static.a().stub()
            shouldFail(IllegalStateException) {
                mock(Loader).static.a().times(1..2)
            }
        }
    }

    void testNoDefaultBehaviorsIfExpectationsAreSetInStrictClosure() {
        def mock = mock {
            strict {
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

    // TODO: loose closure
    // TODO: error messages

}
