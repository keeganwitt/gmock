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

    void testPrivateConstructor() {
        mock(AudioSystem).static.mixerInfo.returns(null)
        play {
            assertNull AudioSystem.getMixerInfo()
        }
    }

}
