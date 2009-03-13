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

import org.gmock.utils.JavaLoader
import static org.hamcrest.Matchers.lessThan

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

    void testExpectationClosureWithMatcher() {
        def mock = mock {
            load(match { it > 3 }).returns(2)
            load(lessThan(3)).returns(1)
        }
        play {
            assertEquals 1, mock.load(2)
            assertEquals 2, mock.load(5)
        }
    }

    void testExpectationClosureWithExistingMethods() {
        mock(JavaLoader, constructor("test"), invokeConstructor("original")) {
            load("yes").returns("no")
            finalMethod(1).returns(2)
            setUp().returns(0)
            name.returns("test")
        }
        play {
            def loader = new JavaLoader("test")
            assertEquals "no", loader.load("yes")
            assertEquals 2, loader.finalMethod(1)
            assertEquals 0, loader.setUp()
            assertEquals "test", loader.getName()
            assertEquals "original", loader.@name
        }
    }

    void testMockMatchMethodUsingIt() {
        def m = mock {
            it.match(match { it() }).returns("yes").stub()
        }
        play {
            assertEquals "yes", m.match { true }
            { ->
                delegate = m
                resolveStrategy = Closure.DELEGATE_FIRST
                this.assertEquals "yes", match { true }
            }()
        }
    }

    void testMockMatchMethodOfFinalJavaClassUsingIt() {
        def s = mock(String) {
            it.match(match { it() }).returns("yes").stub()
        }
        play {
            assertEquals "yes", s.match { true }
            { ->
                delegate = s
                resolveStrategy = Closure.DELEGATE_FIRST
                this.assertEquals "yes", match { true }
            }()
        }
    }

    void testMockMethodsNamedMockInExpectationClosure() {
        def closure = { -> }
        def m = mock {
            mock(closure).returns("yes")
            it.mock(closure).returns("no")
        }
        play {
            assertEquals "yes", m.mock(closure)
            assertEquals "no", m.mock(closure)
        }
    }

    void testDelegateOfExpectationClosureShouldBehaveTheSame() {
        def s = mock {
            it.match(match{it}, delegate.match{it}).returns('yes')
        }
        play {
            assertEquals 'yes', s.match {true}{true}
        }
    }

}
