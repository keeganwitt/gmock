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

import org.junit.Test
import org.gmock.utils.Loader

@WithGMock
class FunctionalCommandExpressionBasedDslTest {
    
    @Test void "returns without parentheses and dots"() {
        def mock = mock()
        mock.load(2) returns 3
        play {
            assert mock.load(2) == 3
        }
    }
    
    @Test void "mock without parentheses"() {
        def mock = mock Loader
        mock.load(2) returns 3
        play {
            assert mock.load(2) == 3
        }
    }
    
    @Test void "never without parentheses"() {
        def mock = mock()
        mock.load(2) never
        play {}
    }
    
    @Test void "once without parentheses"() {
        def mock = mock()
        mock.load(2) returns 3 once
        play {
            assert mock.load(2) == 3
        }
    }
    
    @Test void "atLeastOnce without parentheses"() {
        def mock = mock()
        mock.load(2) returns 3 atLeastOnce
        play {
            2.times {
                assert mock.load(2) == 3
            }
        }
    }
    
    @Test void "atMostOnce without parentheses"() {
        def mock = mock()
        mock.load(2) returns 3 atMostOnce
        play {
            assert mock.load(2) == 3
        }
    }
    
    @Test void "stub without parentheses"() {
        def mock = mock()
        mock.load(2) returns 3 stub
        play {
            3.times {
                assert mock.load(2) == 3
            }
        }
    }
    
    @Test void "chains without parentheses"() {
        def mock = mock()
        mock.load(2).chains.put(1, 3)
        play {
            mock.load(2).put(1, 3)
        }
    }
    
}
