package org.gmock.internal.signature


class PropertyUncompleteSignature {

    def propertyName

    PropertyUncompleteSignature(propertyName) {
        this.propertyName = propertyName
    }

    def validate(){
        throw new IllegalStateException("Missing property expectation for '$propertyName'")
    }

}