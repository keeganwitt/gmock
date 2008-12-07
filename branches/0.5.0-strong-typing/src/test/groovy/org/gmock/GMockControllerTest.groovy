package org.gmock

import junit.framework.AssertionFailedError
import static org.gmock.GMock.match

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
        def mockLoader = gmc.mock(Loader, constructor: [1, 2])
        mockLoader.put(3, 4)

        gmc.play {
            def loader = new Loader(1, 2)
            loader.put(3, 4)
        }

        gmc.mock(Loader, constructor: [1, 2])

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

}
