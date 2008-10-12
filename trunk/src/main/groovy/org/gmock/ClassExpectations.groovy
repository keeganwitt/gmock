package org.gmock
class ClassExpectations {

    def proxies = [:]

    def addConstructorExpectation(aClass, expectation){
        ClassProxy proxy = proxies.get(aClass)
        if (!proxy){
            proxy = ClassProxy.getInstance(aClass)
            proxies.put(aClass, proxy)
        }
        proxy.constructorExpectations.add(expectation)
    }

    def addStaticExpectation(aClass, expectation){
        ClassProxy proxy = proxies.get(aClass)
        if (!proxy){
            proxy = ClassProxy.getInstance(aClass)
            proxies.put(aClass, proxy)
        }
        proxy.staticExpectations.add(expectation)
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
}