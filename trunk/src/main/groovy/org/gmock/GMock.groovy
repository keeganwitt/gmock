package org.gmock

import org.gmock.internal.ClosureMatcher
import org.gmock.internal.recorder.ConstructorRecorder
import org.gmock.internal.recorder.InvokeConstructorRecorder

class GMock {

    static Matcher match(Closure matcher) {
        new ClosureMatcher(matcher)
    }

    static constructor(Object[] args) {
        new ConstructorRecorder(args)
    }

    static invokeConstructor(Object[] args) {
        new InvokeConstructorRecorder(args)
    }

}
