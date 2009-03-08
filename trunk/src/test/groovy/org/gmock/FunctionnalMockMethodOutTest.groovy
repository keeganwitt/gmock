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

import org.gmock.utils.FakeTagLib

class FunctionnalMockMethodOutTest extends GMockTestCase{


    void testMockMethodOut(){
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)

        mockTabLib.link([some: "attr"], "hello").returns("<a>link</a>")

        play {
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

    void testMockMethodResetAfterPlay(){
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)

        mockTabLib.saySomething().returns("other thing")

        play {
            assertEquals "other thing", tagLib.saySomething()
        }
        play {
            assertEquals "something", tagLib.saySomething()
        }
    }



}
