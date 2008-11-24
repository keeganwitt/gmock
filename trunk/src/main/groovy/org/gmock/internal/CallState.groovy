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
    def expected = null
    def called = 0

    MethodState(methodSignature){
        this.methodSignature = methodSignature
    }

    def merge(expectation){
        expected = expected ? expectation.times.merge(expected) : expectation.times
        called += expectation.called
    }

    String toString(){
        "  '${methodSignature}': expected $expected, actual $called"
        // TODO: we should improve this message, the actual called count may be one less sometimes
    }

}
