package org.gmock.internal.signature


class PropertySetSignature {

    def propertyName
    def argument

    PropertySetSignature(propertyName, argument) {
        this.propertyName = propertyName
        this.argument = new ParameterSignature([argument])
    }


    String toString() {
        "${propertyName} = ${argument}"
    }

    boolean equals(Object setSignature) {
        if (setSignature == null || getClass() != setSignature.getClass()) return false
        if (propertyName != setSignature.propertyName) return false
        if (argument != setSignature.argument) return false
        return true
    }

    int hashCode() {
        propertyName.hashCode() * 31 + argument.hashCode()
    }


}
