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
import static org.gmock.GMock.constructor
import static org.gmock.GMock.match
import org.gmock.utils.JavaCache
import org.gmock.utils.JavaLoader
import org.gmock.utils.Loader

class GMockControllerTest extends GroovyTestCase {

	void testMock() {
        def gmc = new GMockController()
        def mockLoader = gmc.mock()
        mockLoader.load('key').returns('value')
        mockLoader.put(1, 2).raises(IllegalArgumentException)

        gmc.play {
            assertEquals "value", mockLoader.load('key')
            shouldFail(IllegalArgumentException) {
                mockLoader.put(1, 2)
            }
        }

        mockLoader.put(1, 2)

        shouldFail(AssertionFailedError) {
            gmc.play {}
        }
    }

    void testMockConstructor() {
        def gmc = new GMockController()
        def mockLoader = gmc.mock(Loader, constructor(1, 2))
        mockLoader.put(3, 4)

        gmc.play {
            def loader = new Loader(1, 2)
            loader.put(3, 4)
        }

        gmc.mock(Loader, constructor(1, 2))

        shouldFail(AssertionFailedError) {
            gmc.play {}
        }
    }

    void testMockStaticMethods() {
        def gmc = new GMockController()
        def mockLoader = gmc.mock(Loader)
        mockLoader.static.initialise().returns(true)

        gmc.play {
            assertTrue Loader.initialise()
        }
    }

    void testMockUsingClosureMatcher() {
        def gmc = new GMockController()
        def mockLoader = gmc.mock()
        mockLoader.load(match { it > 5 }).returns('correct')

        gmc.play {
            assertEquals "correct", mockLoader.load(8)
        }
    }

    void testStrongTyping() {
        def gmc = new GMockController()
        JavaLoader mockLoader = gmc.mock(JavaLoader)
        mockLoader.load("key").returns("something").once()

        JavaCache cache = new JavaCache(mockLoader)

        gmc.play {
            3.times {
                assertEquals "something", cache.load("key")
            }
        }
    }

    void testDefaultWithMethodOnGMockController() {
        new GMockController().with {
            def mock = mock()
            mock.get(match { true }).returns('correct')
            with(mock) {
                load(1).returns(2)
            }

            play {
                assertEquals 'correct', mock.get(1)
                assertEquals 2, mock.load(1)
            }
        }
    }

}
