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

class FunctionnalExpectationClosureTest extends GMockTestCase {

    void testExpectationClosure(){
        def mock = mock {
            amethod().returns("a value")
        }

        play {
            assertEquals "a value", mock.amethod()
        }
    }

    void testExpectationClosureUsingIt(){
        def mock = mock {
            it.amethod().returns("a value")
        }

        play {
            assertEquals "a value", mock.amethod()
        }
    }

    void testExpectationClosureWithStaticExpectaction(){
        mock(Math) {
            it.static.random().returns(0.543)
        }

        play {
            assertEquals 0.543, Math.random()
        }
    }

    void testExpectationClosureRaiseException(){
        def mock = mock(Math) {
            failMethod().raises(new RuntimeException())
        }

        play {
            shouldFail(RuntimeException){
                mock.failMethod()
            }
        }
    }

    void testExpectationClosureSupportProperty(){
        def mock = mock {
            aproperty.set("a value")
            aproperty.returns("another value")
        }

        play {
            mock.aproperty = "a value"
            assertEquals "another value", mock.aproperty
        }
    }

    void testExpectationClosureMixWithNormalClosure(){
        def mock = mock {
            amethod().returns("a value")
        }
        mock.another().returns("another value")
        mock.aproperty.returns("a property")

        play {
            assertEquals "a value", mock.amethod()
            assertEquals "another value", mock.another()
            assertEquals "a property", mock.aproperty
        }
    }

    void testExpectationClosureWithStrongTyping(){
        Date mock = mock(Date){
            now().returns("NOW")
        }
        play {
            assertEquals "NOW", mock.now()
        }
    }

    void testExpectationClosureWithConstructor(){
        mock(Date, constructor("arg1")){
            now().returns("NOW")
        }
        play {
            Date date = new Date("arg1")
            assertEquals "NOW", date.now()
        }
    }

}
