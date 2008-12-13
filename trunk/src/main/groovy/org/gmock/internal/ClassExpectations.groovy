package org.gmock.internal

import org.gmock.internal.metaclass.ClassProxyMetaClass

class ClassExpectations {

    def proxies = [:]
    def controller

    ClassExpectations(controller) {
        this.controller = controller
    }

    def addConstructorExpectation(aClass, expectation){
        ClassProxyMetaClass proxy = proxies.get(aClass)
        if (!proxy){
            proxy = ClassProxyMetaClass.getInstance(aClass)
            proxy.controller = controller
            proxies.put(aClass, proxy)
        }
        proxy.constructorExpectations.add(expectation)
        expectation.expectations = proxy.constructorExpectations
    }

    def addStaticExpectation(aClass, expectation){
        ClassProxyMetaClass proxy = proxies.get(aClass)
        if (!proxy){
            proxy = ClassProxyMetaClass.getInstance(aClass)
            proxy.controller = controller
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

    def validate(){
        proxies.values()*.validate()
    }

    def verify() {
        proxies.values()*.verify()
    }

    def reset() {
        proxies.values()*.reset()
    }

}
