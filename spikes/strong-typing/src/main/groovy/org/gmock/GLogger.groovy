package org.gmock

class GLogger implements Logger {

    GLogger(arg) {
    }

    void error(String message) {
        println "Groovy Error: $message"
    }

    void error(Throwable error) {
        println "Groovy Error: $error.message"
    }

    void debug(String message) {
        println "Groovy Debug: $message"
    }

    void warning(String message) {
        println "Groovy Warning: $message"
    }

    void info(String message) {
        println "Groovy Info: $message"
    }

}
