package org.gmock.internal.signature

class StaticSignature {

    Class aClass
    def methodName
    def arguments

    StaticSignature(aClass, methodName, arguments) {
        this.aClass = aClass
        this.methodName = methodName
        this.arguments = new ParameterSignature(arguments)
    }

    String toString() {
        "${aClass.simpleName}.$methodName($arguments)"
    }

    boolean equals(Object staticSignature) {
        if (staticSignature == null || getClass() != staticSignature.getClass()) return false
        if (aClass != staticSignature.aClass) return false
        if (methodName != staticSignature.methodName) return false
        if (arguments != staticSignature.arguments) return false
        return true
    }

    int hashCode() {
        aClass.hashCode() * 51  + methodName.hashCode() * 31 + arguments.hashCode()
    }

}
