package org.gmock

import org.gmock.internal.ClassExpectations

class GMockController {

    def mocks = []
    def classExpectation = new ClassExpectations()

    def mock(Map constraints = [:], Class aClass = null){
        def mock = new Mock(classExpectation, constraints, aClass)
        mocks.add(mock)
        return mock
    }

    def mock(Class aClass){
        return mock([:], aClass)
    }


    def play(Closure closure) {
        mocks.each {it._replay()}
        classExpectation.startProxy()
        try {
            closure.call()
        } finally {
            classExpectation.stopProxy()
        }
        mocks.each {it._verify()}
        classExpectation.verify()
        mocks.each {it._reset()}
    }

}
