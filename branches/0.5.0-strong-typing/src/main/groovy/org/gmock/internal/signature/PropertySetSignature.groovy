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

    def validate(){
    }

    private boolean equalsWithoutArgument(Object setSignature) {
        if (setSignature == null || getClass() != setSignature.getClass()) return false
        if (propertyName != setSignature.propertyName) return false
        return true
    }

    boolean equals(Object setSignature) {
        if (!equalsWithoutArgument(setSignature)) return false
        if (argument != setSignature.argument) return false
        return true
    }

    boolean match(Object setSignature) {
        if (!equalsWithoutArgument(setSignature)) return false
        if (!argument.match(setSignature.argument)) return false
        return true
    }

    int hashCode() {
        propertyName.hashCode() * 31 + argument.hashCode()
    }

}
