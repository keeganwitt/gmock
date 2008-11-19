package org.gmock.internal

import org.gmock.Matcher

class ClosureMatcher implements Matcher {

    Closure matcher

    ClosureMatcher(Closure matcher) {
        this.matcher = matcher
    }

    boolean matches(object) {
        return matcher(object)
    }

    String toString() {
        return "a value matching the closure matcher" // TODO: improve the message
    }

}
