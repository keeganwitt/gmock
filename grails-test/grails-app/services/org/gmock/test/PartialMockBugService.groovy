package org.gmock.test

class PartialMockBugService {

    static transactional = false

    def serviceMethod() {
        'foo'
    }
}
