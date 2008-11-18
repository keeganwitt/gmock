package org.gmock.internal

class CallState {

    def methodStates = [:]

    def append(expectation){
        def signature = expectation.signature
        if (!methodStates.containsKey(signature)){
            methodStates[signature] = new MethodState(signature)
        }
        methodStates[signature].merge(expectation)
    }

    String toString(){
        methodStates.values().join("\n")
    }

}

class MethodState {

    def methodSignature
    def expected = 0
    def called = 0
    def stubed = false

    MethodState(methodSignature){
        this.methodSignature = methodSignature
    }

    def merge(expectation){
        expected ++
        if (expectation.called){
            called ++
        }
        if (expectation.stubed){
            stubed = true
        }
    }

    String toString(){
        def stubbedStatement = ""
        if (stubed){
            stubbedStatement = ", and stubbed"
        }
        "  '${methodSignature}': expected $expected, actual $called$stubbedStatement"
    }

}