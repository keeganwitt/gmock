package org.gmock.internal

import java.beans.IntrospectionException
import static junit.framework.Assert.fail
import org.gmock.internal.signature.ConstructorSignature
import org.gmock.internal.signature.StaticSignature

/**
 * ClassProxy capture all static and constructor call in replay mode.
 */
class ClassProxy extends ProxyMetaClass {

    MetaClass originalMetaClass
    ExpectationCollection constructorExpectations = new ExpectationCollection()
    ExpectationCollection staticExpectations = new ExpectationCollection()

    public ClassProxy(MetaClassRegistry metaClassRegistry, Class aClass, MetaClass adaptee) throws IntrospectionException {
        super(metaClassRegistry, aClass, adaptee)
    }

    static getInstance(theClass){
        MetaClassRegistry metaRegistry = GroovySystem.getMetaClassRegistry();
        MetaClass meta = metaRegistry.getMetaClass(theClass);
        return new ClassProxy(metaRegistry, theClass, meta);
    }

    def startProxy(){
        originalMetaClass = registry.getMetaClass(theClass);
        registry.setMetaClass(theClass, this);
    }

    def stopProxy(){
        registry.setMetaClass(theClass, originalMetaClass);
    }

    def validate(){
        staticExpectations.validate()
    }

    def verify(){
        constructorExpectations.verify()
        staticExpectations.verify()
    }

    def reset() {
        constructorExpectations = new ExpectationCollection()
        staticExpectations = new ExpectationCollection()
    }

    public Object invokeConstructor(Object[] arguments) {
        if (!constructorExpectations.empty()){
            def signature = new ConstructorSignature(theClass, arguments)
            def expectation = constructorExpectations.findMatching(signature)
            if (expectation){
                return expectation.doReturn()
            } else {
                def callState = constructorExpectations.callState(signature).toString()
                if (callState){ callState = "\n$callState" }
                fail("Unexpected constructor call '${signature}'$callState")
            }
        } else {
            return originalMetaClass.invokeConstructor( arguments )
        }
    }

    public Object invokeStaticMethod(Object aClass, String method, Object[] arguments) {
        if (staticExpectations.empty() || method == "hashCode"){
            return originalMetaClass.invokeStaticMethod(aClass, method, arguments)
        } else {
            def signature = new StaticSignature(aClass, method, arguments)
            def expectation = staticExpectations.findMatching(signature)
            if (expectation){
                return expectation.doReturn()
            } else {
                def callState = staticExpectations.callState(signature).toString()
                if (callState){ callState = "\n$callState" }
                fail("Unexpected static method call '${signature}'$callState")
            }
        }

    }


}