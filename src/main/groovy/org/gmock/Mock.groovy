package org.gmock

import static junit.framework.Assert.fail
import org.gmock.internal.Expectation
import org.gmock.internal.ExpectationCollection
import org.gmock.internal.ReturnValue
import org.gmock.internal.recorder.PropertyRecorder
import org.gmock.internal.recorder.ReturnMethodRecorder
import org.gmock.internal.recorder.StaticMethodRecoder
import org.gmock.internal.signature.ConstructorSignature
import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature

class Mock {

    def aClass
    def expectations = new ExpectationCollection()
    def classExpectations
    def replay = false

    Mock(classExpectations, Map constraints = [:], Class aClass = null){
        this.aClass = aClass
        this.classExpectations = classExpectations
        if (aClass && constraints.constructor != null){
            def signature = new ConstructorSignature(aClass, constraints.constructor)
            def expectation = new Expectation(signature, new ReturnValue(this))
            classExpectations.addConstructorExpectation(aClass, expectation)
        }
    }

    def methodMissing(String name, args) {
        def signature = new MethodSignature(name, args)
        if (replay){
            def expectation = expectations.findMatching(signature)
            if (expectation){
                return expectation.doReturn()
            } else {
                def callState = expectations.callState().toString()
                if (callState){ callState = "\n$callState" }
                fail("Unexpected method call '${signature}'$callState")
            }
            return result
        } else {
            def expectation = new Expectation(signature)
            expectations.add( expectation )
            return new ReturnMethodRecorder(expectation)
        }
    }

    def propertyMissing(name, arg) {
        if (replay){
            def signature = arg ? new PropertySetSignature(name, arg) : new PropertyGetSignature(name)
            def expectation = expectations.findMatching(signature)
            if (expectation){
                return expectation.doReturn()
            } else {
                def callState = expectations.callState().toString()
                if (callState){ callState = "\n$callState" }
                fail("Unexpected property call '${signature}'$callState")
            }
            return result
        } else {
            if (name == "static"){
                def expectation = new Expectation()
                classExpectations.addStaticExpectation(aClass, expectation)
                return new StaticMethodRecoder(aClass, expectation)
            } else {
                def expectation = new Expectation()
                expectations.add( expectation )
                return new PropertyRecorder(name, expectation)
            }
        }
    }

    private void _verify(){
        expectations.verify()
    }

    private void _reset(){
        this.expectations = new ExpectationCollection()
        replay = false
    }

    private void _replay(){
        replay = true
    }

}