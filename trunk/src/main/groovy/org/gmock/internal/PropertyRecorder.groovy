package org.gmock.internal

import org.gmock.internal.signature.PropertySetSignature
import org.gmock.internal.signature.PropertyGetSignature


class PropertyRecorder {

    def propertyName
    def expectation

    PropertyRecorder(propertyName, expectation){
        this.propertyName = propertyName
        this.expectation = expectation
    }

    def methodMissing(String name, arg) {
        if (name == "sets"){
            expectation.signature = new PropertySetSignature(propertyName, arg[0])
            expectation.returnValue = new ReturnNull()
        } else if (name == "returns"){
            expectation.signature = new PropertyGetSignature(propertyName)
            expectation.returnValue = new ReturnValue(arg[0])
        } else if (name == "stub"){
            expectation.stubed = true
        } else if (name == "raises"){
            if (!expectation.signature){
                expectation.signature = new PropertyGetSignature(propertyName)
            }
            expectation.returnValue = ReturnRaiseException.metaClass.invokeConstructor(arg)
        }
        return this
    }




}
