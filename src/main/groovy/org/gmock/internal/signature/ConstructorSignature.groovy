package org.gmock.internal.signature

import org.gmock.internal.Utils

class ConstructorSignature extends ParameterSignature {

    Class aClass

    ConstructorSignature(aClass, arguments) {
        super(arguments)
        this.aClass = aClass
    }

    String toString() {
        "new ${Utils.abreviateClassName(aClass.name)}(${super.toString()})"
    }

    boolean equals(Object constructor) {
        if (constructor == null || getClass() != constructor.getClass()) return false
        if (aClass != constructor.aClass) return false
        return super.equals(constructor)
    }

    int hashCode() {
        aClass.hashCode() * 31  + super.hashCode()
    }

}
