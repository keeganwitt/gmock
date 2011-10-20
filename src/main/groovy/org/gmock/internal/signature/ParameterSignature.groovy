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
package org.gmock.internal.signature

import org.gmock.Matcher
import org.gmock.internal.MockHelper
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.codehaus.groovy.runtime.InvokerHelper

class ParameterSignature {

    private static final Class HAMCREST_MATCHER_CLASS

    static {
        try {
            HAMCREST_MATCHER_CLASS = ParameterSignature.classLoader.loadClass("org.hamcrest.Matcher")
        } catch (e) {
            HAMCREST_MATCHER_CLASS = null
        }
    }

    def arguments

    ParameterSignature(arguments) {
        this.arguments = arguments.toList()
    }

    String toString() {
        arguments.collect { 
            if (InvokerHelper.getMetaClass(it).getClass().package.name == 'org.gmock.internal.metaclass') {
                def s = MockHelper.toString(it)
                return "<$s>"
            } else {
                return DefaultGroovyMethods.inspect(it)
            }
        }.join(", ")
    }

    def validate(){
    }

    private boolean equalsWithoutArguments(Object signature) {
        if (signature?.class != ParameterSignature) return false
        return true
    }

    boolean equals(Object signature) {
        if (!equalsWithoutArguments(signature)) return false
        return arguments == signature.arguments
    }

    boolean match(Object signature) {
        if (!equalsWithoutArguments(signature)) return false
        if (arguments.size() != signature.arguments.size()) return false
        return [arguments, signature.arguments].transpose().every { arg1, arg2 ->
            if (isMatcher(arg1)) return arg1.matches(arg2)
            else return arg1 == arg2
        }
    }

    private boolean isMatcher(object) {
        if (object == null) return false

        // as the Hamcrest library is optional, it may be not present at runtime
        // so we cannot use "object instanceof org.hamcrest.Matcher" directly
        return object instanceof Matcher || HAMCREST_MATCHER_CLASS?.isInstance(object)
    }

    int hashCode() {
        arguments.hashCode()
    }

    int size() {
        arguments.size()
    }

}
