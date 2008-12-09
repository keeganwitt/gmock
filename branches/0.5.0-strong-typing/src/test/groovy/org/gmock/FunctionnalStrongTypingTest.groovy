package org.gmock

import org.gmock.utils.GroovyFinalClass
import org.codehaus.groovy.tools.GroovyClass


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
        mockLoader.load("key").returns("value")
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

    void multiplePlay(){
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

}