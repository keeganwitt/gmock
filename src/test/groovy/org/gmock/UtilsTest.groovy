package org.gmock

class UtilsTest extends GroovyTestCase {

    void testAbreviateClassName(){
        assertEquals "Foo", Utils.abreviateClassName("org.gmock.Foo")
        assertEquals "FooBar", Utils.abreviateClassName("org.gmock.FooBar")
        assertEquals "Car", Utils.abreviateClassName("Car")
    }

}