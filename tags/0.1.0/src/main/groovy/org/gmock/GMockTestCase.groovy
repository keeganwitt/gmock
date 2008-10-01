package org.gmock

import groovy.mock.interceptor.MockProxyMetaClass

class GMockTestCase extends GroovyTestCase {

    def mocks = []

    ExpectationCollection expectationCollection = new ExpectationCollection()

    def mock(){
        def mock = new Mock()
        mocks.add(mock)
        return mock
    }

    def mockNew(Class aClass, Object[] args){
        def mock = mock()
        expectationCollection.expectConstructor(aClass, args, mock)
        return mock
    }


    def play = { closure ->
        mocks.each {it._replay()}
        expectationCollection.startProxy()
        try {
            closure.call()
        } finally {
            expectationCollection.stopProxy()            
        }
        mocks.each {it._verify()}
        mocks.each {it._reset()}
    }


}