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

    void testStrictClosureWithTimesExpectation() {
        def mock = mock()
        strict {
            mock.a().times(2)
            mock.a().times(1..3)
            mock.b().stub()
            mock.c().atLeast(2)
            mock.d().never()
        }
        play {
            4.times { mock.a() }
            3.times { mock.c() }
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

}
