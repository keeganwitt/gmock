package org.gmock.signature

class MethodSignature {

    def methodName
    def arguments

    MethodSignature(methodName, arguments){
        this.methodName = methodName
        this.arguments = arguments.toList()
    }

    String toString(){
        "${methodName}(${arguments.join(',')})"
    }


    boolean equals(Object method) {
        if (method == null || getClass() != method.getClass()) return false
        if (methodName != method.methodName) return false
        if (arguments != method.arguments) return false
        return true;
    }

    int hashCode() {
        methodName.hashCode() * 31  + arguments.hashCode()
    }



}