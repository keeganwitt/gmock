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
            mock.a().returns(1)
            mock.b().returns(2)
            mock.c().returns(3)
        }
        shouldFail { // TODO: check the message
            play {
                assertEquals 1, mock.a()
                mock.c()
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

}
