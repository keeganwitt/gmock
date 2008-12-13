package org.gmock.internal.signature


class PropertyGetSignature {

    def propertyName

    PropertyGetSignature(propertyName) {
        this.propertyName = propertyName
    }

    String toString() {
        "${propertyName}"
    }

    def validate(){
    }

    boolean equals(Object getSignature) {
        if (getSignature == null || getClass() != getSignature.getClass()) return false
        if (propertyName != getSignature.propertyName) return false
        return true
    }

    boolean match(Object getSignature) {
        return equals(getSignature)
    }

    int hashCode() {
        propertyName.hashCode()
    }

}
