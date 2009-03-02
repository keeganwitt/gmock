package org.gmock.internal

import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.result.ToStringDefaultBehavior
import org.gmock.internal.result.HashCodeDefaultBehavior
import org.gmock.internal.result.EqualsDefaultBehavior
import org.gmock.internal.matcher.AlwaysMatchMatcher
import org.gmock.internal.times.AnyTimes


class MockInternal {

    def expectations
    def mockInstance
    def controller
    def mockProxyMetaClass
    def mockName

    MockInternal(controller, mockInstance, mockName, mockProxyMetaClass){
        this.expectations = new ExpectationCollection(controller)
        this.controller = controller
        this.mockName = mockName
        this.mockProxyMetaClass = mockProxyMetaClass
        this.mockInstance = mockInstance
        mockProxyMetaClass.mock = this
    }

    void verify(){
        expectations.verify()
    }

    void validate(){
        expectations.validate()
    }

    void reset(){
        this.expectations = new ExpectationCollection(controller)
    }

    void replay() {
        addMethodDefaultBehavior("equals", [AlwaysMatchMatcher.INSTANCE], new EqualsDefaultBehavior(mockInstance))
        addMethodDefaultBehavior("hashCode", [], new HashCodeDefaultBehavior(mockInstance))
        addMethodDefaultBehavior("toString", [], new ToStringDefaultBehavior(mockName))
    }

    private addMethodDefaultBehavior(methodName, arguments, result) {
        def signature = new MethodSignature(mockProxyMetaClass, methodName, arguments)
        if (!expectations.findSignature(signature) && !controller.orderedExpectations.findSignature(signature)) {
            def expectation = new Expectation(signature: signature, result: result, times: AnyTimes.INSTANCE, hidden: true)
            expectations.add(expectation)
        }
    }

    
}