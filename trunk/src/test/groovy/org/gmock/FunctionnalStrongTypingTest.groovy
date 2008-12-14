package org.gmock

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
        Loader mockLoader = mock(Loader, constructor: ["a name"])
        mockLoader.load("key").returns("a value")
        play {
            Loader loader = new Loader("a name")
            assertEquals "a value", loader.load("key")
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

    void mockClosureDelegate(clazz, strategy) {
        def mock = mock(clazz)
        mock.mockMethod().returns("correct")
        mock.mockProperty.set(0)
        mock.mockProperty.returns(99)

        play {
            def closure = {
                this.assertEquals "correct", mockMethod()
                mockProperty = 0
                this.assertEquals 99, mockProperty
            }
            closure.delegate = mock
            closure.resolveStrategy = strategy
            closure()
        }
    }

    void testMockGroovyNonFinalClassAsClosureDelegate() {
        mockClosureDelegate(GroovyClass, Closure.OWNER_FIRST)
        mockClosureDelegate(GroovyClass, Closure.DELEGATE_FIRST)
        mockClosureDelegate(GroovyClass, Closure.DELEGATE_ONLY)
    }

    void testMockGroovyFinalClassAsClosureDelegate() {
        mockClosureDelegate(GroovyFinalClass, Closure.OWNER_FIRST)
        mockClosureDelegate(GroovyFinalClass, Closure.DELEGATE_FIRST)
        mockClosureDelegate(GroovyFinalClass, Closure.DELEGATE_ONLY)
    }

    void testMockJavaNonFinalClassAsClosureDelegate() {
        mockClosureDelegate(JavaLoader, Closure.OWNER_FIRST)
        mockClosureDelegate(JavaLoader, Closure.DELEGATE_FIRST)
        mockClosureDelegate(JavaLoader, Closure.DELEGATE_ONLY)
    }

    void testMockJavaFinalClassAsClosureDelegate() {
        mockClosureDelegate(String, Closure.OWNER_FIRST)
        mockClosureDelegate(String, Closure.DELEGATE_FIRST)
        mockClosureDelegate(String, Closure.DELEGATE_ONLY)
    }

}
