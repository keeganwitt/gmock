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

import static org.gmock.internal.metaclass.MetaClassHelper.newSignatureForStaticMethod
import org.gmock.internal.signature.StaticSignature

class StaticMethodRecoder implements GroovyInterceptable {

    def expectation
    def aClass

    StaticMethodRecoder(aClass, expectation){
        this.expectation = expectation
        this.aClass = aClass
        this.expectation.signature = new StaticSignature(aClass)
    }

    Object invokeMethod(String name, Object args) {
        expectation.signature = newSignatureForStaticMethod(aClass, name, args)
        return new ReturnMethodRecorder(expectation)
    }

    Object getProperty(String property) {
        return new StaticPropertyRecorder(aClass, property, expectation)
    }

    void setProperty(String property, Object value) {
        throw new MissingPropertyException("Cannot use property setter in record mode. " +
                "Are you trying to mock a setter? Use 'static.${property}.set(${value.inspect()})' instead.")
    }

}
