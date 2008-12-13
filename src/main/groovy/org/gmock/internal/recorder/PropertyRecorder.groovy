package org.gmock.internal.recorder

import org.gmock.internal.result.ReturnNull
import org.gmock.internal.result.ReturnValue
import org.gmock.internal.result.ThrowException
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature
import org.gmock.internal.signature.PropertyUncompleteSignature

class PropertyRecorder extends BaseRecorder {

    def propertyName

    PropertyRecorder(propertyName, expectation) {
        super(expectation)
        this.propertyName = propertyName
        this.expectation.signature = new PropertyUncompleteSignature(propertyName)
    }

    def sets(value) {
        throw new DeprecationException("'sets' is deprecated. Use 'set' instead.");
    }

    def set(value) {
        expectation.signature = new PropertySetSignature(propertyName, value)
        expectation.result = new ReturnNull()
        return this
    }

    def returns(value) {
        expectation.signature = new PropertyGetSignature(propertyName)
        expectation.result = new ReturnValue(value)
        return this
    }

    private doRaises(Object[] params) {
        if (expectation.signature.class == PropertyUncompleteSignature){
            expectation.signature = new PropertyGetSignature(propertyName)
        }
        expectation.result = ThrowException.metaClass.invokeConstructor(params)
        return this
    }

    def raises(Throwable exception) {
        return doRaises(exception)
    }

    def raises(Class exceptionClass, Object[] params) {
        return doRaises(exceptionClass, *params)
    }

}
