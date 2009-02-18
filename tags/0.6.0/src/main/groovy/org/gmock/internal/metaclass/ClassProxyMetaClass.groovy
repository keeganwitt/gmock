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
import org.gmock.internal.ExpectationCollection
import static org.gmock.internal.InternalModeHelper.doInternal
import static org.gmock.internal.metaclass.MetaClassHelper.findExpectation
import static org.gmock.internal.metaclass.MetaClassHelper.newSignatureForStaticMethod
import org.gmock.internal.signature.ConstructorSignature
import org.gmock.internal.signature.StaticPropertyGetSignature
import org.gmock.internal.signature.StaticPropertySetSignature

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
        if (!empty()) {
            adaptee = registry.getMetaClass(theClass)
            registry.setMetaClass(theClass, this)
        }
    }

    def stopProxy(){
        if (!empty()) {
            registry.setMetaClass(theClass, adaptee)
        }
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

    Object invokeConstructor(Object[] arguments) {
        checkAndDo constructorExpectations.empty(), { adaptee.invokeConstructor(arguments) }, {
            def signature = new ConstructorSignature(theClass, arguments)
            return findExpectation(constructorExpectations, signature, "Unexpected constructor call", arguments)
        }
    }

    Object invokeStaticMethod(Object aClass, String method, Object[] arguments) {
        checkAndDo staticExpectations.empty(), { adaptee.invokeStaticMethod(aClass, method, arguments) }, {
            def signature = newSignatureForStaticMethod(aClass, method, arguments)
            return findExpectation(staticExpectations, signature, "Unexpected static method call", arguments)
        }
    }

    Object getProperty(Object clazz, String property) {
        checkAndDo staticExpectations.empty() || !(clazz instanceof Class), { adaptee.getProperty(clazz, property) }, {
            def signature = new StaticPropertyGetSignature(clazz, property)
            return findExpectation(staticExpectations, signature, "Unexpected static property getter call", [])
        }
    }

    void setProperty(Object clazz, String property, Object value) {
        checkAndDo staticExpectations.empty() || !(clazz instanceof Class), { adaptee.setProperty(clazz, property, value) }, {
            def signature = new StaticPropertySetSignature(clazz, property, value)
            findExpectation(staticExpectations, signature, "Unexpected static property setter call", [value])
        }
    }

    private checkAndDo(condition, Closure invokeOriginal, Closure work) {
        if (condition) {
            return invokeOriginal()
        } else {
            return doInternal(controller, invokeOriginal, work)
        }
    }

    private empty() {
        constructorExpectations.empty() && staticExpectations.empty()
    }

}
