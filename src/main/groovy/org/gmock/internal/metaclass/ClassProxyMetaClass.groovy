/*
 * Copyright 2008-2009 Julien Gagnet, Johnny Jian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gmock.internal.metaclass

import java.beans.IntrospectionException
import static junit.framework.Assert.fail
import org.gmock.internal.ExpectationCollection
import static org.gmock.internal.InternalModeHelper.doInternal
import org.gmock.internal.signature.ConstructorSignature
import org.gmock.internal.signature.StaticSignature

/**
 * ClassProxyMetaClass capture all static and constructor call in replay mode.
 */
class ClassProxyMetaClass extends ProxyMetaClass {

    ExpectationCollection constructorExpectations = new ExpectationCollection()
    ExpectationCollection staticExpectations = new ExpectationCollection()
    def controller

    public ClassProxyMetaClass(MetaClassRegistry metaClassRegistry, Class aClass, MetaClass adaptee) throws IntrospectionException {
        super(metaClassRegistry, aClass, adaptee)
    }

    static getInstance(theClass){
        MetaClassRegistry registry = GroovySystem.metaClassRegistry
        MetaClass adaptee = registry.getMetaClass(theClass)
        return new ClassProxyMetaClass(registry, theClass, adaptee)
    }

    def startProxy(){
        adaptee = registry.getMetaClass(theClass)
        registry.setMetaClass(theClass, this)
    }

    def stopProxy(){
        registry.setMetaClass(theClass, adaptee)
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
        if (!constructorExpectations.empty()) {
            return doInternal(controller, { adaptee.invokeConstructor(arguments) }) {
                def signature = new ConstructorSignature(theClass, arguments)
                def expectation = constructorExpectations.findMatching(signature)
                if (expectation){
                    return expectation.answer()
                } else {
                    def callState = constructorExpectations.callState(signature).toString()
                    if (callState){ callState = "\n$callState" }
                    fail("Unexpected constructor call '${signature}'$callState")
                }
            }
        } else {
            return adaptee.invokeConstructor( arguments )
        }
    }

    public Object invokeStaticMethod(Object aClass, String method, Object[] arguments) {
        if (staticExpectations.empty()) {
            return adaptee.invokeStaticMethod(aClass, method, arguments)
        } else {
            return doInternal(controller, { adaptee.invokeStaticMethod(aClass, method, arguments) }) {
                def signature = new StaticSignature(aClass, method, arguments)
                def expectation = staticExpectations.findMatching(signature)
                if (expectation){
                    return expectation.answer()
                } else {
                    def callState = staticExpectations.callState(signature).toString()
                    if (callState){ callState = "\n$callState" }
                    fail("Unexpected static method call '${signature}'$callState")
                }
            }
        }
    }

}
