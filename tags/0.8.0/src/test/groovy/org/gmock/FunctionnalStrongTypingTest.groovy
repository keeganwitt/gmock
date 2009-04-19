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

import junit.framework.AssertionFailedError
import org.gmock.utils.*

class FunctionnalStrongTypingTest extends GMockTestCase {

    void testBasic(){
        JavaLoader mockLoader = mock(JavaLoader)

        JavaCache cache = new JavaCache(mockLoader)
        mockLoader.load("a key").returns("a value").once()

        play {
            assertEquals("a value", cache.load("a key"))
            assertEquals("a value", cache.load("a key"))
        }
    }

    void testGroovyClass(){
        GroovyClass mockGroovy = mock(GroovyClass)
        mockGroovy.aMethod("args").returns("called")
        play {
            assertEquals("called", mockGroovy.aMethod("args"))
        }
    }

    void testMatchConstructor(){
        Loader mockLoader = mock(Loader, constructor("a name"))
        mockLoader.load("key").returns("a value")
        play {
            Loader loader = new Loader("a name")
            assertEquals "a value", loader.load("key")
        }
    }

    void testMatchConstructorThrowException(){
        Loader mockLoader = mock(Loader, constructor("a name").raises(new RuntimeException()))
        play {
            shouldFail(RuntimeException){
                new Loader("a name")
            }
        }
    }

    void testInterface(){
        List list = mock(List)
        list.size().returns(10)
        play {
            assertEquals(10, list.size())
        }
    }

    void testJavaFinalClass(){
        String mockJavaFinal = mock(String)
        mockJavaFinal.startsWith("ham").returns(true)
        play {
            assertTrue mockJavaFinal.startsWith("ham")
        }
    }

    void testGroovyFinalClass(){
        GroovyFinalClass mockGroovyFinal = mock(GroovyFinalClass)
        mockGroovyFinal.aMethod("args").returns("called")
        play {
            assertEquals("called", mockGroovyFinal.aMethod("args"))
        }
    }

    void testMultiplePlay(){
        JavaLoader mockLoader = mock(JavaLoader)

        JavaCache cache = new JavaCache(mockLoader)
        mockLoader.load("a key").returns("a value").once()

        play {
            assertEquals("a value", cache.load("a key"))
            assertEquals("a value", cache.load("a key"))
        }

        mockLoader.load("another key").returns("another value").once()
        play {
            assertEquals("another value", cache.load("another key"))
            assertEquals("another value", cache.load("another key"))
        }
    }

    void testLoaderInterface() {
        ILoader mockLoader = mock(ILoader)
        mockLoader.load("johnny").returns("jian").once()

        JavaCache cache = new JavaCache(null)

        play {
            3.times {
                assertEquals "jian", cache.load("johnny", mockLoader)
            }
        }
    }

    void testPassAMockGroovyObjectToJava() {
        GroovyLoader mockLoader = mock(GroovyLoader)
        mockLoader.load("johnny").returns("jian").once()

        JavaCache cache = new JavaCache(null)

        play {
            3.times {
                assertEquals "jian", cache.load("johnny", mockLoader)
            }
        }
    }

    void runClosureWithDifferentResolveStrategies(Closure closure) {
        [Closure.OWNER_FIRST, Closure.DELEGATE_FIRST, Closure.DELEGATE_ONLY].each { strategy ->
            closure.resolveStrategy = strategy
            closure()
        }
    }

    void mockClosureDelegate(clazz) {
        def mock = mock(clazz)
        mock.mockMethod().returns("correct").times(3)
        mock.mockProperty.set(0).times(3)
        mock.mockProperty.returns(99).times(3)

        play {
            def closure = {
                this.assertEquals "correct", mockMethod()
                mockProperty = 0
                this.assertEquals 99, mockProperty
            }
            closure.delegate = mock
            runClosureWithDifferentResolveStrategies(closure)
        }
    }

    void testMockGroovyNonFinalClassAsClosureDelegate() {
        mockClosureDelegate(GroovyClass)
    }

    void testMockGroovyFinalClassAsClosureDelegate() {
        mockClosureDelegate(GroovyFinalClass)
    }

    void testMockJavaNonFinalClassAsClosureDelegate() {
        mockClosureDelegate(JavaLoader)
    }

    void testMockJavaFinalClassAsClosureDelegate() {
        mockClosureDelegate(String)
    }

    void testGroovyFinalMethod() {
        GroovyLoader mock = mock(GroovyLoader)
        mock.finalMethod(0).returns(1)
        play {
            assertEquals 1, mock.finalMethod(0)
        }
    }

    void testJavaFinalMethod() {
        JavaLoader mock = mock(JavaLoader)
        mock.finalMethod(0).returns(2)
        play {
            assertEquals 2, mock.finalMethod(0)
        }
    }

    void testClassesWithFinalMethodAsClosureDelegate() {
        def closure = {
            this.assertEquals 3, finalMethod(1)
        }
        def test = { Class clazz ->
            def mock = mock(clazz)
            mock.finalMethod(1).returns(3).times(3)
            play {
                closure.delegate = mock
                runClosureWithDifferentResolveStrategies(closure)
            }
        }
        test(GroovyLoader)
        test(JavaLoader)
    }

    void testMockWithoutExpectationsAsClosureDelegateShouldFailed() {
        def closure = {
            this.assertEquals 4, load(2)
        }
        def test = { Class clazz, String expected ->
            def mock = mock(clazz)
            play {
                closure.delegate = mock
                [Closure.OWNER_FIRST, Closure.DELEGATE_FIRST, Closure.DELEGATE_ONLY].each { strategy ->
                    closure.resolveStrategy = strategy
                    def message = shouldFail(AssertionFailedError) {
                        closure()
                    }
                    assertEquals expected, message
                }
            }
        }
        test(GroovyLoader, "Unexpected method call 'load(2)'")
        test(JavaLoader, "Unexpected method call 'load(2)' on 'Mock for JavaLoader'")
    }

    void testExceptionsShouldNotBeWrappedByInvokerInvocationException() {
        def loader = mock(JavaLoader)
        loader.load("key").raises(NotFoundException)
        def cache = new JavaCache(loader)
        play {
            assertNull cache.load("key")
        }
    }

    void testAddingAMethodToGroovyObjectShouldNotAffectMockingThatMethod() {
        GroovyObject.metaClass.load = { String key -> "" }

        JavaLoader loader = mock(OnceLoader)
        loader.load("key").returns("value")
        JavaCache cache = new JavaCache(loader)
        play {
            assertEquals "value", cache.load("key")
        }
    }

}