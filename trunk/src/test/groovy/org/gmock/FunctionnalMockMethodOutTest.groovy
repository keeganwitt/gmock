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
import junit.framework.AssertionFailedError

class FunctionnalMockMethodOutTest extends GMockTestCase {


    void testMockMethodOut() {
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)

        mockTabLib.link([some: "attr"], "hello").returns("<a>link</a>")

        play {
            tagLib.linkHello([some: "attr"])
        }
    }


    void testMockMethodOutUnexpectedCall() {
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)

        mockTabLib.link([some: "attr"], "goodBye").returns("<a>link</a>")

        def message = shouldFail(AssertionFailedError) {
            play {
                tagLib.linkHello([some: "attr"])
            }
        }
        def expected = """Unexpected method call 'link(["some":"attr"], "hello")'
  'link(["some":"attr"], "goodBye")': expected 1, actual 0"""
        assertEquals expected, message

    }


    void testMockMethodCanStillCallOriginalImplementation() {
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)


        mockTabLib.link([some: "attr"], "hello").returns("<a>link</a>")

        play {
            tagLib.linkHello([some: "attr"])
            assertEquals "something", tagLib.saySomething()
        }

    }

    void testMockGetPropertyOut() {
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)
        def mockOut = mock()

        mockTabLib.out.returns(mockOut)
        mockOut << "hello"

        play {
            tagLib.hello()
        }

    }

    void testMockSetPropertyOut() {
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)

        mockTabLib.something.set("hello")

        play {
            tagLib.something = "hello"
            tagLib.somethingElse = "else"
        }

    }

    void testMockGetPropertyOutUsingGetter() {
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)
        def mockOut = mock()

        mockTabLib.getOut().returns(mockOut)
        mockOut.leftShift("hello")

        play {
            tagLib.hello()
        }

    }

    void testMockGetPropertyOutCanStillCallOriginalProperty() {
        def tagLib = new FakeTagLib()
        def mockTabLib = mock(tagLib)
        def mockOut = mock()

        mockTabLib.out.returns(mockOut)
        mockOut << "hello"

        play {
            tagLib.hello()
            assertEquals "something", tagLib.something
        }

    }



    void testMockMethodResetAfterPlay() {
        def tagLib = new FakeTagLib()
        def mockTagLib = mock(tagLib)

        mockTagLib.saySomething().returns("other thing")

        play {
            assertEquals "other thing", tagLib.saySomething()
        }
        play {
            assertEquals "something", tagLib.saySomething()
        }
    }

    void testRegexMethodName() {
        def tagLib = new FakeTagLib()
        mock(tagLib)./say.*/().returns('regex')
        play {
            assertEquals 'regex', tagLib.saySomething()
        }
    }

    void testConcreteObjectShouldNotBeMockedOutsidePlayClosure() {
        def tagLib = new FakeTagLib()
        mock(tagLib)
        tagLib.saySomething()
        tagLib.something
        tagLib.something = "goodbye"
        play {}
    }

    void testCallOnMockDuringPlayDelegateToConcrete() {
        def tagLib = new FakeTagLib()
        def mockTagLib = mock(tagLib)
        mockTagLib.say().stub()
        play {
            tagLib.say()
            mockTagLib.say()
            assertEquals("something", tagLib.getSomething())
            assertEquals("something", mockTagLib.getSomething())
        }
    }

}
