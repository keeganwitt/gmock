package org.gmock.internal.signature

import org.gmock.internal.Utils

class StaticSignature extends ParameterSignature {

    Class aClass
    def methodName

    StaticSignature(aClass, methodName, arguments) {
        super(arguments)
        this.aClass = aClass
        this.methodName = methodName
    }

    String toString() {
        "${Utils.abreviateClassName(aClass.name)}.$methodName(${super.toString()})"
    }

    boolean equals(Object staticSignature) {
        if (staticSignature == null || getClass() != staticSignature.getClass()) return false
        if (aClass != staticSignature.aClass) return false
        if (methodName != staticSignature.methodName) return false
        return super.equals(staticSignature)
    }

    int hashCode() {
        aClass.hashCode() * 51  + methodName.hashCode() * 31 + super.hashCode()
    }

}
