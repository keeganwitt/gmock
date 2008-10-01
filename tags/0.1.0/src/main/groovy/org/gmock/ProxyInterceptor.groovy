package org.gmock

import java.beans.IntrospectionException
import static junit.framework.Assert.*


class ProxyInterceptor extends ProxyMetaClass {

    MetaClass originalMetaClass
    def constructorExpectations = []

    public ProxyInterceptor(MetaClassRegistry metaClassRegistry, Class aClass, MetaClass adaptee) throws IntrospectionException {
        super(metaClassRegistry, aClass, adaptee)
    }


    static getInstance(theClass){
        MetaClassRegistry metaRegistry = GroovySystem.getMetaClassRegistry();
        MetaClass meta = metaRegistry.getMetaClass(theClass);
        return new ProxyInterceptor(metaRegistry, theClass, meta);
    }

    def startProxy(){
        originalMetaClass = registry.getMetaClass(theClass);
        registry.setMetaClass(theClass, this);
    }

    def stopProxy(){
        registry.setMetaClass(theClass, originalMetaClass);
    }



    def expectConstructor(args, mock){
        constructorExpectations << new ConstructorExpectation(theClass, args, mock)
    }

    public Object invokeConstructor(Object[] arguments) {
        if (constructorExpectations){
            def expectation = constructorExpectations.find { it.canCall(arguments)}
            println "found expectation=$expectation constructorExpectations=$constructorExpectations"
            if (expectation){
                return expectation.doReturn()
            } else {
                def signature = new ConstructorSignature(theClass, arguments)
                def callState = constructorState().toString()
                if (callState){ callState = "\n$callState" }
                fail("Unexpected constructor call '${signature}'$callState")
            }
        } else {
            return originalMetaClass.invokeConstructor( arguments )
        }
    }

    private constructorState(){
        def callState = new CallState()
        constructorExpectations.each {
            callState.append(it)
        }
        return callState
    }


}