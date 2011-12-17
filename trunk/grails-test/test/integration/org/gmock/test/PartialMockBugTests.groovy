package org.gmock.test

import static org.junit.Assert.*
import org.junit.*
import org.gmock.*

@WithGMock
class PartialMockBugTests extends GroovyTestCase {

    PartialMockBugService partialMockBugService

    @Test
    void partialMock() {
        def m = mock(partialMockBugService)
        m.serviceMethod().raises(new RuntimeException("bar"))
        play {
             shouldFail(RuntimeException) {
                 partialMockBugService.serviceMethod()
             }
        }
    }

}
