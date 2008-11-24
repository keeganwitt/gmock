package org.gmock.internal

class ClassExpectations {

    def proxies = [:]

    def addConstructorExpectation(aClass, expectation){
        ClassProxy proxy = proxies.get(aClass)
        if (!proxy){
            proxy = ClassProxy.getInstance(aClass)
            proxies.put(aClass, proxy)
        }
        proxy.constructorExpectations.add(expectation)
        expectation.expectations = proxy.constructorExpectations
    }

    def addStaticExpectation(aClass, expectation){
        ClassProxy proxy = proxies.get(aClass)
        if (!proxy){
            proxy = ClassProxy.getInstance(aClass)
            proxies.put(aClass, proxy)
        }
        proxy.staticExpectations.add(expectation)
        expectation.expectations = proxy.staticExpectations
    }

    def startProxy() {
        proxies.values()*.startProxy()
    }

    def stopProxy() {
        proxies.values()*.stopProxy()
    }

    def verify() {
        proxies.values()*.verify()
    }

    def reset() {
        proxies.values()*.reset()
    }

}
