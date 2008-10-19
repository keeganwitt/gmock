package org.gmock.internal.signature

class MethodSignature extends ParameterSignature {

    def methodName

    MethodSignature(methodName, arguments) {
        super(arguments)
        this.methodName = methodName
    }

    String toString() {
        "$methodName(${super.toString()})"
    }

    boolean equals(Object method) {
        if (method == null || getClass() != method.getClass()) return false
        if (methodName != method.methodName) return false
        return super.equals(method)
    }

    int hashCode() {
        methodName.hashCode() * 31  + super.hashCode()
    }

}
