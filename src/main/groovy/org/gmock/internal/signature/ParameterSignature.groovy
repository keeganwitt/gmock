package org.gmock.internal.signature

class ParameterSignature {

    def arguments

    ParameterSignature(arguments) {
        this.arguments = arguments.toList()
    }

    String toString() {
        arguments*.inspect().join(", ")
    }

    boolean equals(Object signature) {
        if (signature == null || !(signature instanceof ParameterSignature)) return false
        if (arguments != signature.arguments) return false
        return true;
    }

    int hashCode() {
        arguments.hashCode()
    }

}
