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
package org.gmock.internal.recorder

import org.gmock.internal.expectation.Expectation
import org.gmock.internal.signature.StaticMethodSignature
import org.gmock.internal.signature.StaticPropertySignature

class StaticRecoder implements GroovyInterceptable {

    def classExpectations
    def aClass
    boolean missingExpectation = true

    StaticRecoder(aClass, classExpectations){
        this.classExpectations = classExpectations
        this.aClass = aClass
        classExpectations.addStaticValidator { ->
            if (this.@missingExpectation) {
                throw new IllegalStateException("Missing static expectation for ${aClass.simpleName}")
            }
        }
    }

    Object invokeMethod(String name, Object args) {
        def expectation = new Expectation(signature: new StaticMethodSignature(aClass, name, args))
        classExpectations.addStaticExpectation(aClass, expectation)
        missingExpectation = false
        return new MethodRecorder(expectation)
    }

    Object getProperty(String property) {
        def expectation = new Expectation(signature: new StaticPropertySignature(aClass, property))
        classExpectations.addStaticExpectation(aClass, expectation)
        missingExpectation = false
        return new PropertyRecorder(expectation)
    }

    void setProperty(String property, Object value) {
        throw new MissingPropertyException("Cannot use property setter in record mode. " +
                "Are you trying to mock a setter? Use 'static.${property}.set(${value.inspect()})' instead.")
    }

}
