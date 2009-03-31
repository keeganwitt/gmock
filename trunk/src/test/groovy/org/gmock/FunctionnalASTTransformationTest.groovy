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

import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.gmock.WithGMock
import org.gmock.utils.JavaCache
import org.gmock.utils.JavaLoader
import org.testng.annotations.Test
import org.testng.TestListenerAdapter
import org.testng.TestNG

@WithGMock
class FunctionnalASTTransformationTest extends GroovyTestCase {

    void testBasic() {
        def loader = mock()
        loader.load("key").returns("value")

        String mockString = mock(String)
        mockString.contains("ham").returns(true)

        play {
            assertEquals "value", loader.load("key")
            assertTrue mockString.contains("ham")
        }
    }

    void testWithMock() {
        def loader = mock()
        with(loader) {
            loader.load("key").returns("value")
        }
        play {
            assertEquals "value", loader.load("key")
        }
    }

    void testOrdering() {
        def mock = mock()
        ordered {
            mock.a().returns(1)
            unordered {
                mock.b().returns(2)
                mock.c().returns(3)
            }
            mock.d().returns(4)
        }
        play {
            assertEquals 1, mock.a()
            assertEquals 3, mock.c()
            assertEquals 2, mock.b()
            assertEquals 4, mock.d()
        }

    }

    void testMatching() {
        def mockLoader = mock()
        mockLoader.load(match { it.startsWith("k")}).returns("value")

        play {
            assertEquals "value", mockLoader.load("key")
        }
    }


    void testConstructor() {
        JavaLoader mock = mock(JavaLoader, constructor("my loader"))
        mock.getName().returns("name")

        play {
            JavaLoader loader = new JavaLoader("my loader")
            assertEquals "name", loader.getName()
        }
    }

    void testInvokeConstructor() {
        JavaLoader mock = mock(JavaLoader, invokeConstructor("loader"))
        mock.getName().returns("name")

        def cache = new JavaCache(mock)

        play {
            assertEquals "name", mock.getName()
            assertEquals "loader", cache.getLoaderName()
        }
    }

    void testName(){
        def mockDate = mock(Date, name('my mock date'))
        play {
            assertEquals "my mock date", mockDate.toString()
        }
    }

    private void shouldFailWithGMock(Closure closure) {
        try {
            closure()
        } catch (MultipleCompilationErrorsException e) {
            def errors = e.errorCollector.errors
            if (errors.size() == 1 && errors[0].cause.message.startsWith('@WithGMock')) {
                return // success
            }
        } catch (e) {}
        fail("Should fail to compile the code because of applying @WithGMock on a wrong target.")
    }

    void testAnnotationCannotBeAppliedOnProperties() {
        shouldFailWithGMock {
            Eval.me '''
class A {
    @org.gmock.WithGMock
    def aProperty
}
'''
        }
    }

    void testAnnotationCannotBeAppliedOnMethods() {
        shouldFailWithGMock {
            Eval.me '''
class A {
    @org.gmock.WithGMock
    def aMethod() {}
}
'''
        }
    }

    void testAnnotationCannotBeAppliedOnInterfaces() {
        shouldFailWithGMock {
            Eval.me '''
@org.gmock.WithGMock
interface I {}
'''
        }
    }

    void testDoNotAddMethodsIfTheyAreAlreadyExists() {
        Eval.me '''
@org.gmock.WithGMock
class A {
    def name(String s) { "name" }
    def with(object, Closure closure) { "with" }
    def test() {
        assert "name" == name("test")
        assert "with" == with(null, {})
        assert constructor() instanceof org.gmock.internal.recorder.ConstructorRecorder
    }
}
new A().test()
'''
    }

    void testTestNGWithGMock() {
        new TestNG().with {
            outputDirectory = 'build/reports/testng-tests'
            testClasses = TestNGWithGMock as Class[]
            it.run()
            assertFalse hasFailure()
        }
    }

}

@WithGMock
class TestNGWithGMock {

    @Test void basic() {
        def loader = mock()
        loader.load("key").returns("value")

        String mockString = mock(String)
        mockString.contains("ham").returns(true)

        play {
            assert "value" == loader.load("key")
            assert mockString.contains("ham")
        }
    }

    @Test void withMock() {
        def loader = mock()
        with(loader) {
            loader.load("key").returns("value")
        }
        play {
            assert "value" == loader.load("key")
        }
    }

    @Test void ordering() {
        def mock = mock()
        ordered {
            mock.a().returns(1)
            unordered {
                mock.b().returns(2)
                mock.c().returns(3)
            }
            mock.d().returns(4)
        }
        play {
            assert 1 == mock.a()
            assert 3 == mock.c()
            assert 2 == mock.b()
            assert 4 == mock.d()
        }

    }

    @Test void matching() {
        def mockLoader = mock()
        mockLoader.load(match { it.startsWith("k")}).returns("value")

        play {
            assert "value" == mockLoader.load("key")
        }
    }

    @Test void constructor() {
        JavaLoader mock = mock(JavaLoader, constructor("my loader"))
        mock.getName().returns("name")

        play {
            JavaLoader loader = new JavaLoader("my loader")
            assert "name" == loader.getName()
        }
    }

    @Test void invokeConstructor() {
        JavaLoader mock = mock(JavaLoader, invokeConstructor("loader"))
        mock.getName().returns("name")

        def cache = new JavaCache(mock)

        play {
            assert "name" == mock.getName()
            assert "loader" == cache.getLoaderName()
        }
    }

    @Test void name() {
        def mockDate = mock(Date, name('my mock date'))
        play {
            assert "my mock date" == mockDate.toString()
        }
    }

}
