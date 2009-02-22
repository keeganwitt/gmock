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

}
