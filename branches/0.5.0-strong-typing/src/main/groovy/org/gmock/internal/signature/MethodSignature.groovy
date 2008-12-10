package org.gmock.internal.signature

class MethodSignature {

    def methodName
    def arguments

    MethodSignature(methodName, arguments) {
        this.methodName = methodName
        this.arguments = new ParameterSignature(arguments)
    }

    String toString() {
        "$methodName($arguments)"
    }

    def validate(){
    }

    private boolean equalsWithoutArguments(Object method) {
        if (method == null || getClass() != method.getClass()) return false
        if (methodName != method.methodName) return false
        return true
    }

    boolean equals(Object method) {
        if (!equalsWithoutArguments(method)) return false
        if (arguments != method.arguments) return false
        return true
    }

    boolean match(Object method) {
        if (!equalsWithoutArguments(method)) return false
        if (!arguments.match(method.arguments)) return false
        return true
    }

    int hashCode() {
        methodName.hashCode() * 31  + arguments.hashCode()
    }

}
