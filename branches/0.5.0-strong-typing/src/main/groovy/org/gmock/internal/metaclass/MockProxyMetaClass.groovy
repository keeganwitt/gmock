package org.gmock.internal.metaclass

import static junit.framework.Assert.fail
import org.gmock.internal.Expectation
import org.gmock.internal.ExpectationCollection
import static org.gmock.internal.InternalModeHelper.doInternal
import org.gmock.internal.recorder.PropertyRecorder
import org.gmock.internal.recorder.ReturnMethodRecorder
import org.gmock.internal.recorder.StaticMethodRecoder
import org.gmock.internal.signature.MethodSignature
import org.gmock.internal.signature.PropertyGetSignature
import org.gmock.internal.signature.PropertySetSignature

class MockProxyMetaClass extends ProxyMetaClass {

    def expectations = new ExpectationCollection()
    def classExpectations
    def replay = false
    def controller

    MockProxyMetaClass(Class clazz, classExpectations, controller) {
        super(GroovySystem.metaClassRegistry, clazz, GroovySystem.metaClassRegistry.getMetaClass(clazz))
        this.classExpectations = classExpectations
        this.controller = controller
    }

    Object invokeMethod(Object object, String methodName, Object[] arguments) {
        invokeMethod(theClass, object, methodName, arguments, false, false)
    }

    Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        doInternal controller, { adaptee.invokeMethod(receiver, methodName, arguments) }, {
            def signature = new MethodSignature(methodName, arguments)
            if (replay){
                def expectation = expectations.findMatching(signature)
                if (expectation){
                    return expectation.answer()
                } else {
                    def callState = expectations.callState(signature).toString()
                    if (callState){ callState = "\n$callState" }
                    fail("Unexpected method call '${signature}'$callState")
                }
            } else {
                def expectation = new Expectation(expectations: expectations, signature: signature)
                expectations.add( expectation )
                return new ReturnMethodRecorder(expectation)
            }
        }
    }

    Object getProperty(Object object, String property) {
        getProperty(theClass, object, property, false, false)
    }

    Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
        doInternal controller, { adaptee.getProperty(receiver, property) }, {
            if (replay){
                def signature = new PropertyGetSignature(property)
                def expectation = expectations.findMatching(signature)
                if (expectation){
                    return expectation.answer()
                } else {
                    def callState = expectations.callState(signature).toString()
                    if (callState){ callState = "\n$callState" }
                    fail("Unexpected property getter call '${signature}'$callState")
                }
            } else {
                if (property == "static"){
                    def expectation = new Expectation()
                    classExpectations.addStaticExpectation(theClass, expectation)
                    return new StaticMethodRecoder(theClass, expectation)
                } else {
                    def expectation = new Expectation(expectations: expectations)
                    expectations.add( expectation )
                    return new PropertyRecorder(property, expectation)
                }
            }
        }
    }

    void setProperty(Object object, String property, Object newValue) {
        setProperty(theClass, object, property, newValue, false, false)
    }

    void setProperty(Class sender, Object receiver, String property, Object value, boolean isCallToSuper, boolean fromInsideClass) {
        doInternal controller, { adaptee.setProperty(receiver, property, value) }, {
            if (replay){
                def signature = new PropertySetSignature(property, value)
                def expectation = expectations.findMatching(signature)
                if (expectation){
                    expectation.answer()
                } else {
                    def callState = expectations.callState(signature).toString()
                    if (callState){ callState = "\n$callState" }
                    fail("Unexpected property setter call '${signature}'$callState")
                }
            } else {
                throw new MissingPropertyException("Cannot use property setter in record mode. " +
                        "Are you trying to mock a setter? Use '${property}.set($value)' instead.")
            }
        }
    }

    void verify(){
        assert replay, "verify() must be called after replay()."
        expectations.verify()
    }

    void validate(){
        expectations.validate()
    }

    void reset(){
        this.expectations = new ExpectationCollection()
        replay = false
    }

    void replay(){
        replay = true
    }

}
