package org.gmock.internal.recorder

import org.gmock.internal.ReturnNull
import org.gmock.internal.ReturnRaiseException
import org.gmock.internal.ReturnValue
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature

class PropertyRecorder extends BaseRecorder {

    def propertyName

    PropertyRecorder(propertyName, expectation) {
        super(expectation)
        this.propertyName = propertyName
    }

    def sets(value) {
        expectation.signature = new PropertySetSignature(propertyName, value)
        expectation.returnValue = new ReturnNull()
        return this
    }

    def returns(value) {
        expectation.signature = new PropertyGetSignature(propertyName)
        expectation.returnValue = new ReturnValue(value)
        return this
    }

    private doRaises(Object[] params) {
        if (!expectation.signature){
            expectation.signature = new PropertyGetSignature(propertyName)
        }
        expectation.returnValue = ReturnRaiseException.metaClass.invokeConstructor(params)
        return this
    }

    def raises(Throwable exception) {
        return doRaises(exception)
    }

    def raises(Class exceptionClass, Object[] params) {
        return doRaises(exceptionClass, *params)
    }

}
