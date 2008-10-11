package org.gmock

import java.beans.IntrospectionException
import static junit.framework.Assert.*
import org.gmock.signature.ConstructorSignature

/**
 * StaticMock capture all static and constructor call in replay mode.
 */
class StaticMock extends ProxyMetaClass {

    MetaClass originalMetaClass
    def constructorExpectations = new ExpectationCollection()

    public StaticMock(MetaClassRegistry metaClassRegistry, Class aClass, MetaClass adaptee) throws IntrospectionException {
        super(metaClassRegistry, aClass, adaptee)
    }


    static getInstance(theClass){
        MetaClassRegistry metaRegistry = GroovySystem.getMetaClassRegistry();
        MetaClass meta = metaRegistry.getMetaClass(theClass);
        return new StaticMock(metaRegistry, theClass, meta);
    }

    def startProxy(){
        originalMetaClass = registry.getMetaClass(theClass);
        registry.setMetaClass(theClass, this);
    }

    def stopProxy(){
        registry.setMetaClass(theClass, originalMetaClass);
    }

    def verify(){
        constructorExpectations.verify()
    }


    def expectConstructor(arguments, mock){
        def signature = new ConstructorSignature(theClass, arguments)
        def expectation = new Expectation(signature, new ReturnValue(mock))
        constructorExpectations.add( expectation )
    }

    public Object invokeConstructor(Object[] arguments) {
        if (constructorExpectations){
            def signature = new ConstructorSignature(theClass, arguments)
            def expectation = constructorExpectations.findMatching(signature)
            if (expectation){
                return expectation.doReturn()
            } else {
                def callState = constructorExpectations.callState().toString()
                if (callState){ callState = "\n$callState" }
                fail("Unexpected constructor call '${signature}'$callState")
            }
        } else {
            return originalMetaClass.invokeConstructor( arguments )
        }
    }


}