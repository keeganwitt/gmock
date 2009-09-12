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

import javax.servlet.http.HttpServletRequest
import junit.framework.AssertionFailedError
import javax.sound.sampled.AudioSystem

class BugTest extends GMockTestCase {

    void testHttpServletRequest() {
        HttpServletRequest request = mock(HttpServletRequest)
        def mock = mock()
        mock.foo(request)

        shouldFail(AssertionFailedError) {
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

}
