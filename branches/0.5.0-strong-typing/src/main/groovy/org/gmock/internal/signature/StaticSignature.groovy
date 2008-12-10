package org.gmock.internal.signature

class StaticSignature {

    Class aClass
    def methodName
    def arguments

    StaticSignature(aClass) {
        this.aClass = aClass
    }

    StaticSignature(aClass, methodName, arguments) {
        this.aClass = aClass
        this.methodName = methodName
        this.arguments = new ParameterSignature(arguments)
    }

    String toString() {
        "${aClass.simpleName}.$methodName($arguments)"
    }

    def validate(){
        if (!methodName){
            throw new IllegalStateException("Missing static expectation for ${aClass.simpleName}")
        }
    }

    private boolean equalsWithoutArguments(Object staticSignature) {
        if (staticSignature == null || getClass() != staticSignature.getClass()) return false
        if (aClass != staticSignature.aClass) return false
        if (methodName != staticSignature.methodName) return false
        return true
    }

    boolean equals(Object staticSignature) {
        if (!equalsWithoutArguments(staticSignature)) return false
        if (arguments != staticSignature.arguments) return false
        return true
    }

    boolean match(Object staticSignature) {
        if (!equalsWithoutArguments(staticSignature)) return false
        if (!arguments.match(staticSignature.arguments)) return false
        return true
    }

    int hashCode() {
        aClass.hashCode() * 51  + methodName.hashCode() * 31 + arguments.hashCode()
    }

}
