package org.gmock

import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.gmock.WithGMock
import org.gmock.utils.JavaCache
import org.gmock.utils.JavaLoader

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

}