package org.gmock

class GMockController {

    def mocks = []

    ProxyCollection proxyCollection = new ProxyCollection()

    def mock(){
        def mock = new Mock()
        mocks.add(mock)
        return mock
    }

    def mock(Map constraints, Class aClass){
        def mock = mock()
        if (constraints.constructor != null){
            proxyCollection.expectConstructor(aClass, constraints.constructor, mock)
        }
        return mock
    }

    def play(Closure closure) {
        mocks.each {it._replay()}
        proxyCollection.startProxy()
        try {
            closure.call()
        } finally {
            proxyCollection.stopProxy()
        }
        mocks.each {it._verify()}
        proxyCollection.verify()
        mocks.each {it._reset()}
    }

}
