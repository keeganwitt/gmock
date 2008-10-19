package org.gmock.internal.signature

import org.gmock.internal.Utils

class ConstructorSignature {

    Class aClass
    def arguments

    ConstructorSignature(aClass, arguments) {
        this.aClass = aClass
        this.arguments = new ParameterSignature(arguments)
    }

    String toString() {
        "new ${Utils.abreviateClassName(aClass.name)}($arguments)"
    }

    boolean equals(Object constructor) {
        if (constructor == null || getClass() != constructor.getClass()) return false
        if (aClass != constructor.aClass) return false
        if (arguments != constructor.arguments) return false
        return true
    }

    int hashCode() {
        aClass.hashCode() * 31  + arguments.hashCode()
    }

}
