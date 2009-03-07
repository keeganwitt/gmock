package org.gmock

import org.gmock.utils.FakeTagLib

class FunctionnalMockMethodOutTest extends GMockTestCase{


    void testMockMethodOut(){
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)

        mockTabLib.link([some: "attr"], "hello").returns("<a>link</a>")

        play {
            println "playing"
            tagLib.linkHello([some: "attr"])
        }

    }

    void testMockMethodCanStillCallOriginalImplementation(){
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)


        mockTabLib.link([some: "attr"], "hello").returns("<a>link</a>")

        play {
            tagLib.linkHello([some: "attr"])
            assertEquals "something", tagLib.saySomething()
        }

    }

}
