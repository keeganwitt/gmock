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

import static org.gmock.utils.JavaTestHelper.SCHEMA_LANGUAGE
import static org.gmock.utils.JavaTestHelper.SCHEMA_LANGUAGE_VALUE

import javax.servlet.http.HttpServletRequest
import javax.sound.sampled.AudioSystem

import org.dom4j.io.SAXReader
import org.gmock.utils.Echo
import org.gmock.utils.JavaTestHelper
import org.gmock.utils.Meh
import org.gmock.utils.SimpleUtility
import org.gmock.utils.TestClass
import org.w3c.dom.Element

class BugTest extends GMockTestCase {

    void testHttpServletRequest() {
        HttpServletRequest request = mock(HttpServletRequest)
        def mock = mock()
        mock.foo(request)

        shouldFail(AssertionError) {
            play {}
        }
    }

    void testIgnoreFinalize() {
        def m = mock()
        m.fun()
        play {
            m.fun()
        }

        m = mock()
        System.gc()
        play {}
    }

    void testPrivateConstructor() {
        mock(AudioSystem).static.mixerInfo.returns(null)
        play {
            assertNull AudioSystem.getMixerInfo()
        }
    }

    void testPOJOSetProperty() {
        def m = mock(SAXReader)
        m.setProperty(SCHEMA_LANGUAGE, SCHEMA_LANGUAGE_VALUE)
        play {
            JavaTestHelper.setSAXReaderProperty(m)
        }
    }

    void testPartialMockingWithClosure() {
        def meh = new Meh()
        ordered {
            mock(meh).doSomethingElse()
        }
        play {
            meh.doSomething()
        }
    }

    void testAsStringThrowANullPointerException() {
        def mockOutput = mock()
        mockOutput.print(["stuff"] as String[])

        play {
            def echo = new Echo()
            echo.output = mockOutput
            echo.echo(["stuff"])
        }
    }

    void testNullParameterThrowANullPointerException() {
        def user = mock() {
            setEmail(null)
        }
        play {
            user.setEmail(null)
        }
    }

    void testMockedElementParameter() {
        def m = mock(TestClass)
        def element = mock(Element)
        m.hello(element).returns('world')
        shouldFail(AssertionError) {
            play {}
        }
    }

    void testNullParameterAndExpectationNotMatchedThrowsNPE() {
        def m = mock()
        m.fun(null)
        shouldFail(AssertionError) {
            play {}
        }
    }

    void testCglibClassNotFoundException() {
        def jarUrl = this.class.getResource('/org/gmock/utils/JavaLoader.jar')
        def classLoader = new URLClassLoader([jarUrl] as URL[], (ClassLoader) null)
        def clazz = classLoader.loadClass('org.gmock.utils.JavaLoader')

        def mock = mock(clazz)
        mock.load('a') returns 'b'

        play {
            assert mock.load('a') == 'b'
        }
    }

    void testUseMockFileAsParameterThrowsNullPointerException() {
        SimpleUtility utility = mock(SimpleUtility)

        File file1 = mock(File)
        utility.deleteDirectory(file1)

        File file2 = mock(File)
        utility.deleteDirectory(file2)

        play {
            utility.deleteDirectory(file1)
            utility.deleteDirectory(file2)
        }
    }

}
