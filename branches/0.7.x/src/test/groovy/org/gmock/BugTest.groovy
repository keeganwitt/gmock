package org.gmock

import javax.servlet.http.HttpServletRequest
import junit.framework.AssertionFailedError

class BugTest extends GMockTestCase {

    void testHttpServletRequest() {
        HttpServletRequest request = mock(HttpServletRequest)
        def mock = mock()
        mock.foo(request)

        shouldFail(AssertionFailedError) {
            play {}
        }
    }

}
