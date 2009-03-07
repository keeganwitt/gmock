package org.gmock.utils

class FakeTagLib {


    def hello = { attrs ->
        out << "hello"
    }

    def linkHello = { attrs ->
        link(attrs, "hello")
    }

    def saySomething(){
        return "something"
    }

}
