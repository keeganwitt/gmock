package org.gmock

import org.gmock.internal.ClosureMatcher

class GMock {

    static Matcher match(Closure matcher) {
        new ClosureMatcher(matcher)
    }

}
